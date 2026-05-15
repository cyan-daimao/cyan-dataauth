package com.cyan.dataauth.adapter.mypermission.http;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.dto.*;
import com.cyan.dataauth.infra.persistence.approval.dos.AuthApprovalDO;
import com.cyan.dataauth.infra.persistence.approval.mappers.AuthApprovalMapper;
import com.cyan.dataauth.infra.persistence.permission.dos.AuthPermissionDO;
import com.cyan.dataauth.infra.persistence.permission.mappers.AuthPermissionMapper;
import com.cyan.dataauth.infra.persistence.role.dos.AuthRoleDO;
import com.cyan.dataauth.infra.persistence.role.mappers.AuthRoleMapper;
import com.cyan.dataauth.infra.persistence.rolepermission.dos.AuthRolePermissionDO;
import com.cyan.dataauth.infra.persistence.rolepermission.mappers.AuthRolePermissionMapper;
import com.cyan.dataauth.infra.persistence.userrole.mappers.AuthUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 我的权限控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthMyPermissionController {

    private final AuthUserRoleMapper authUserRoleMapper;
    private final AuthRoleMapper authRoleMapper;
    private final AuthRolePermissionMapper authRolePermissionMapper;
    private final AuthPermissionMapper authPermissionMapper;
    private final AuthApprovalMapper authApprovalMapper;

    /**
     * 模块名称映射
     */
    private static final Map<String, String> MODULE_NAME_MAP = new LinkedHashMap<>();
    static {
        MODULE_NAME_MAP.put("meta", "元数据平台");
        MODULE_NAME_MAP.put("metrics", "指标平台");
        MODULE_NAME_MAP.put("sql-editor", "SQL查询");
        MODULE_NAME_MAP.put("data-work", "数据加工");
        MODULE_NAME_MAP.put("bi", "智能分析");
        MODULE_NAME_MAP.put("auth", "权限管理");
    }

    /**
     * 我的权限聚合
     */
    @GetMapping("/my/permissions")
    // API: ready
    public Response<MyPermissionDTO> getMyPermissions(@RequestParam String passport) {
        MyPermissionDTO dto = new MyPermissionDTO();

        // 1. 查询用户的所有角色
        List<Long> roleIds = authUserRoleMapper.selectRoleIdsByPassport(passport);
        if (roleIds == null || roleIds.isEmpty()) {
            dto.setFunctionPermissions(List.of());
            dto.setDataPermissions(List.of());
            dto.setMetricPermissions(List.of());
            dto.setPendingApprovals(List.of());
            return Response.success(dto);
        }

        // 统一查询所有权限项
        Set<Long> permIds = new HashSet<>();
        for (Long roleId : roleIds) {
            permIds.addAll(authRolePermissionMapper.selectPermissionIdsByRoleId(roleId));
        }
        List<AuthPermissionDO> allPerms = permIds.isEmpty() ? List.of()
                : authPermissionMapper.selectBatchIds(new ArrayList<>(permIds));

        // 2. 功能权限：按模块分组（Round1: ready）
        dto.setFunctionPermissions(aggregateFunctionPermissions(allPerms));

        // 3. 数据权限：按数据源→数据库→表层级聚合（Round1: ready）
        dto.setDataPermissions(aggregateDataPermissions(allPerms));

        // 4. 指标权限：按主题域分组（Round1: ready）
        dto.setMetricPermissions(aggregateMetricPermissions(allPerms));

        // 5. 审批进度
        LambdaQueryWrapper<AuthApprovalDO> approvalWrapper = new LambdaQueryWrapper<>();
        approvalWrapper.eq(AuthApprovalDO::getApplicantPassport, passport);
        approvalWrapper.eq(AuthApprovalDO::getStatus, "PENDING");
        approvalWrapper.orderByDesc(AuthApprovalDO::getSubmittedAt);
        List<AuthApprovalDO> approvals = authApprovalMapper.selectList(approvalWrapper);
        List<PendingApprovalDTO> pendingApprovals = new ArrayList<>();
        if (approvals != null) {
            for (AuthApprovalDO a : approvals) {
                pendingApprovals.add(new PendingApprovalDTO(
                        a.getApprovalId(),
                        a.getApprovalType(),
                        a.getResourceId(),
                        a.getStatus(),
                        a.getCurrentNode(),
                        a.getSubmittedAt()
                ));
            }
        }
        dto.setPendingApprovals(pendingApprovals);

        return Response.success(dto);
    }

    /**
     * 功能权限聚合：按模块分组
     */
    private List<FunctionPermissionDTO> aggregateFunctionPermissions(List<AuthPermissionDO> allPerms) {
        Map<String, List<String>> moduleMap = new LinkedHashMap<>();
        for (AuthPermissionDO perm : allPerms) {
            String rt = perm.getResourceType();
            if (!"MENU".equals(rt) && !"BUTTON".equals(rt)) {
                continue;
            }
            String resourceId = perm.getResourceId();
            String moduleKey = resolveModuleKey(resourceId);
            String moduleName = MODULE_NAME_MAP.getOrDefault(moduleKey, moduleKey);
            String permKey = resourceId + ":" + perm.getAction();
            moduleMap.computeIfAbsent(moduleName, k -> new ArrayList<>()).add(permKey);
        }
        List<FunctionPermissionDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : moduleMap.entrySet()) {
            result.add(new FunctionPermissionDTO(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private String resolveModuleKey(String resourceId) {
        if (resourceId == null || resourceId.isEmpty()) {
            return "其他";
        }
        int colonIdx = resourceId.indexOf(':');
        String firstSegment = colonIdx > 0 ? resourceId.substring(0, colonIdx) : resourceId;
        return MODULE_NAME_MAP.containsKey(firstSegment) ? firstSegment : "其他";
    }

    /**
     * 数据权限聚合：处理 DATASOURCE / DB / TABLE 层级
     */
    private List<DataPermissionDTO> aggregateDataPermissions(List<AuthPermissionDO> allPerms) {
        // 先分类收集
        Map<String, Set<String>> datasourceActions = new LinkedHashMap<>();
        Map<String, Set<String>> dbActions = new LinkedHashMap<>();
        Map<String, Map<String, Map<String, Set<String>>>> tableActions = new LinkedHashMap<>();

        for (AuthPermissionDO perm : allPerms) {
            String rt = perm.getResourceType();
            String rid = perm.getResourceId();
            String action = perm.getAction();
            if (rid == null || action == null) {
                continue;
            }
            if ("DATASOURCE".equals(rt)) {
                datasourceActions.computeIfAbsent(rid, k -> new LinkedHashSet<>()).add(action);
            } else if ("DB".equals(rt)) {
                dbActions.computeIfAbsent(rid, k -> new LinkedHashSet<>()).add(action);
            } else if ("TABLE".equals(rt)) {
                String[] parts = rid.split("\\.");
                if (parts.length >= 3) {
                    String ds = parts[0];
                    String db = parts[1];
                    String tbl = parts[2];
                    tableActions.computeIfAbsent(ds, k -> new LinkedHashMap<>())
                            .computeIfAbsent(db, k -> new LinkedHashMap<>())
                            .computeIfAbsent(tbl, k -> new LinkedHashSet<>())
                            .add(action);
                }
            }
        }

        // 合并输出：数据源 -> 数据库 -> 表
        Set<String> allDs = new LinkedHashSet<>();
        allDs.addAll(datasourceActions.keySet());
        allDs.addAll(dbActions.keySet().stream().map(k -> k.split("\\.")[0]).collect(Collectors.toSet()));
        allDs.addAll(tableActions.keySet());

        List<DataPermissionDTO> result = new ArrayList<>();
        for (String ds : allDs) {
            List<DatabasePermissionDTO> dbList = new ArrayList<>();

            // DB 层级的数据库
            Set<String> dbNames = new LinkedHashSet<>();
            for (String dbKey : dbActions.keySet()) {
                String[] parts = dbKey.split("\\.");
                if (parts.length >= 2 && ds.equals(parts[0])) {
                    dbNames.add(parts[1]);
                }
            }
            // TABLE 层级的数据库
            dbNames.addAll(tableActions.getOrDefault(ds, Collections.emptyMap()).keySet());

            for (String db : dbNames) {
                List<TablePermissionDTO> tblList = new ArrayList<>();

                // TABLE 层级的表
                Map<String, Set<String>> tblMap = tableActions.getOrDefault(ds, Collections.emptyMap())
                        .getOrDefault(db, Collections.emptyMap());
                for (Map.Entry<String, Set<String>> tblEntry : tblMap.entrySet()) {
                    tblList.add(new TablePermissionDTO(tblEntry.getKey(), String.join(",", tblEntry.getValue()), null, null));
                }

                // DB 层级的权限（仅当没有表权限时展示，或合并）
                String dbKey = ds + "." + db;
                Set<String> dbAct = dbActions.get(dbKey);
                if (dbAct != null && !dbAct.isEmpty()) {
                    // 如果有DB权限但没有TABLE权限，用特殊标记表示整个数据库
                    if (tblList.isEmpty()) {
                        tblList.add(new TablePermissionDTO("*", String.join(",", dbAct), null, null));
                    }
                }

                if (!tblList.isEmpty()) {
                    dbList.add(new DatabasePermissionDTO(db, tblList));
                }
            }

            // DATASOURCE 层级的权限（仅当没有数据库权限时展示）
            Set<String> dsAct = datasourceActions.get(ds);
            if (dsAct != null && !dsAct.isEmpty() && dbList.isEmpty()) {
                List<TablePermissionDTO> tblList = new ArrayList<>();
                tblList.add(new TablePermissionDTO("*", String.join(",", dsAct), null, null));
                dbList.add(new DatabasePermissionDTO("*", tblList));
            }

            if (!dbList.isEmpty()) {
                result.add(new DataPermissionDTO(ds, dbList));
            }
        }
        return result;
    }

    /**
     * 指标权限聚合：按主题域分组
     */
    private List<MetricPermissionGroupDTO> aggregateMetricPermissions(List<AuthPermissionDO> allPerms) {
        Map<String, Set<String>> subjectMetrics = new LinkedHashMap<>();
        Map<String, Set<String>> subjectDimensions = new LinkedHashMap<>();

        for (AuthPermissionDO perm : allPerms) {
            String rt = perm.getResourceType();
            String rid = perm.getResourceId();
            if ("SUBJECT".equals(rt)) {
                String subject = rid != null ? rid : "默认主题";
                subjectMetrics.computeIfAbsent(subject, k -> new LinkedHashSet<>());
                subjectDimensions.computeIfAbsent(subject, k -> new LinkedHashSet<>());
            } else if ("METRIC".equals(rt)) {
                String subject = resolveSubjectCode(rid);
                subjectMetrics.computeIfAbsent(subject, k -> new LinkedHashSet<>()).add(rid);
            } else if ("DIMENSION".equals(rt)) {
                String subject = resolveSubjectCode(rid);
                subjectDimensions.computeIfAbsent(subject, k -> new LinkedHashSet<>()).add(rid);
            }
        }

        List<MetricPermissionGroupDTO> result = new ArrayList<>();
        for (String subject : subjectMetrics.keySet()) {
            result.add(new MetricPermissionGroupDTO(
                    subject,
                    new ArrayList<>(subjectMetrics.getOrDefault(subject, Collections.emptySet())),
                    new ArrayList<>(subjectDimensions.getOrDefault(subject, Collections.emptySet()))
            ));
        }
        // 处理仅有 DIMENSION 的主题
        for (String subject : subjectDimensions.keySet()) {
            if (!subjectMetrics.containsKey(subject)) {
                result.add(new MetricPermissionGroupDTO(
                        subject,
                        new ArrayList<>(),
                        new ArrayList<>(subjectDimensions.get(subject))
                ));
            }
        }
        return result;
    }

    /**
     * 尝试从 resourceId 解析 subjectCode
     */
    private String resolveSubjectCode(String resourceId) {
        if (resourceId == null) {
            return "默认主题";
        }
        int idx = resourceId.indexOf(':');
        return idx > 0 ? resourceId.substring(0, idx) : "默认主题";
    }

    /**
     * 功能权限树
     */
    @GetMapping("/function-permissions/tree")
    // API: ready
    public Response<List<FunctionPermissionNodeDTO>> getFunctionPermissionTree() {
        List<FunctionPermissionNodeDTO> tree = buildHardcodedTree();
        return Response.success(tree);
    }

    private List<FunctionPermissionNodeDTO> buildHardcodedTree() {
        List<FunctionPermissionNodeDTO> tree = new ArrayList<>();

        // 元数据平台
        FunctionPermissionNodeDTO meta = new FunctionPermissionNodeDTO("meta", "元数据平台", "MENU", new ArrayList<>());
        FunctionPermissionNodeDTO metaDs = new FunctionPermissionNodeDTO("meta:business-ds", "业务数据库", "MENU", new ArrayList<>());
        metaDs.getChildren().add(new FunctionPermissionNodeDTO("meta:business-ds:datasource", "数据源管理", "MENU", null));
        metaDs.getChildren().add(new FunctionPermissionNodeDTO("meta:business-ds:database", "数据库管理", "MENU", null));
        metaDs.getChildren().add(new FunctionPermissionNodeDTO("meta:business-ds:table", "表结构管理", "MENU", null));
        meta.getChildren().add(metaDs);
        meta.getChildren().add(new FunctionPermissionNodeDTO("meta:datasource", "元数据数据源", "MENU", null));
        meta.getChildren().add(new FunctionPermissionNodeDTO("meta:subject", "主题域管理", "MENU", null));
        meta.getChildren().add(new FunctionPermissionNodeDTO("meta:table", "元数据表管理", "MENU", null));
        tree.add(meta);

        // 指标平台
        FunctionPermissionNodeDTO metric = new FunctionPermissionNodeDTO("metric", "指标平台", "MENU", new ArrayList<>());
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:dashboard", "指标概览", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:definition", "指标定义", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:dictionary", "指标字典", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:analysis", "指标分析", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:dimension", "维度管理", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:config", "指标配置", "MENU", null));
        tree.add(metric);

        // SQL查询
        tree.add(new FunctionPermissionNodeDTO("sql-editor", "SQL查询", "MENU", null));

        // 数据加工
        tree.add(new FunctionPermissionNodeDTO("data-work", "数据加工", "MENU", null));

        // 智能分析
        FunctionPermissionNodeDTO bi = new FunctionPermissionNodeDTO("bi", "智能分析", "MENU", new ArrayList<>());
        bi.getChildren().add(new FunctionPermissionNodeDTO("bi:chart", "图表分析", "MENU", null));
        bi.getChildren().add(new FunctionPermissionNodeDTO("bi:dashboard", "看板管理", "MENU", null));
        bi.getChildren().add(new FunctionPermissionNodeDTO("bi:dataset", "数据集管理", "MENU", null));
        tree.add(bi);

        // 权限管理
        FunctionPermissionNodeDTO auth = new FunctionPermissionNodeDTO("auth", "权限管理", "MENU", new ArrayList<>());
        auth.getChildren().add(new FunctionPermissionNodeDTO("auth:role", "角色管理", "MENU", null));
        auth.getChildren().add(new FunctionPermissionNodeDTO("auth:user", "用户权限", "MENU", null));
        auth.getChildren().add(new FunctionPermissionNodeDTO("auth:metric", "指标平台权限", "MENU", null));
        auth.getChildren().add(new FunctionPermissionNodeDTO("auth:approval", "审批管理", "MENU", null));
        auth.getChildren().add(new FunctionPermissionNodeDTO("auth:audit", "审计日志", "MENU", null));
        auth.getChildren().add(new FunctionPermissionNodeDTO("auth:my", "我的权限", "MENU", null));
        tree.add(auth);

        return tree;
    }
}
