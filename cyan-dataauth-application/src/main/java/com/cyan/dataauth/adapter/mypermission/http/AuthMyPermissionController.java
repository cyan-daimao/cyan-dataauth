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
     * 我的权限聚合
     */
    @GetMapping("/my/permissions")
    // API: ready
    public Response<MyPermissionDTO> getMyPermissions(@RequestParam String passport) {
        MyPermissionDTO dto = new MyPermissionDTO();

        // 1. 查询用户的所有角色
        List<Long> roleIds = authUserRoleMapper.selectRoleIdsByPassport(passport);

        // 2. 功能权限：按模块分组（本期简化，按resourceType分组）
        List<FunctionPermissionDTO> functionPermissions = new ArrayList<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Long> permIds = new HashSet<>();
            for (Long roleId : roleIds) {
                permIds.addAll(authRolePermissionMapper.selectPermissionIdsByRoleId(roleId));
            }
            if (!permIds.isEmpty()) {
                List<AuthPermissionDO> perms = authPermissionMapper.selectBatchIds(new ArrayList<>(permIds));
                Map<String, List<String>> moduleMap = new LinkedHashMap<>();
                for (AuthPermissionDO perm : perms) {
                    String rt = perm.getResourceType();
                    if ("MENU".equals(rt) || "BUTTON".equals(rt)) {
                        String module = perm.getDescription() != null ? perm.getDescription() : rt;
                        moduleMap.computeIfAbsent(module, k -> new ArrayList<>())
                                .add(perm.getResourceId() + "-" + perm.getAction());
                    }
                }
                for (Map.Entry<String, List<String>> entry : moduleMap.entrySet()) {
                    functionPermissions.add(new FunctionPermissionDTO(entry.getKey(), entry.getValue()));
                }
            }
        }
        dto.setFunctionPermissions(functionPermissions);

        // 3. 数据权限：按数据源→数据库→表层级聚合
        List<DataPermissionDTO> dataPermissions = new ArrayList<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Long> permIds = new HashSet<>();
            for (Long roleId : roleIds) {
                permIds.addAll(authRolePermissionMapper.selectPermissionIdsByRoleId(roleId));
            }
            if (!permIds.isEmpty()) {
                List<AuthPermissionDO> perms = authPermissionMapper.selectBatchIds(new ArrayList<>(permIds));
                Map<String, Map<String, List<TablePermissionDTO>>> dsMap = new LinkedHashMap<>();
                for (AuthPermissionDO perm : perms) {
                    String rt = perm.getResourceType();
                    if ("TABLE".equals(rt) || "DB".equals(rt) || "DATASOURCE".equals(rt)) {
                        String[] parts = perm.getResourceId().split("\\.");
                        if (parts.length >= 3 && "TABLE".equals(rt)) {
                            String ds = parts[0];
                            String db = parts[1];
                            String tbl = parts[2];
                            dsMap.computeIfAbsent(ds, k -> new LinkedHashMap<>())
                                    .computeIfAbsent(db, k -> new ArrayList<>())
                                    .add(new TablePermissionDTO(tbl, perm.getAction(), null, null));
                        }
                    }
                }
                for (Map.Entry<String, Map<String, List<TablePermissionDTO>>> dsEntry : dsMap.entrySet()) {
                    List<DatabasePermissionDTO> dbList = new ArrayList<>();
                    for (Map.Entry<String, List<TablePermissionDTO>> dbEntry : dsEntry.getValue().entrySet()) {
                        dbList.add(new DatabasePermissionDTO(dbEntry.getKey(), dbEntry.getValue()));
                    }
                    dataPermissions.add(new DataPermissionDTO(dsEntry.getKey(), dbList));
                }
            }
        }
        dto.setDataPermissions(dataPermissions);

        // 4. 指标权限：按主题域分组
        List<MetricPermissionGroupDTO> metricPermissions = new ArrayList<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Long> permIds = new HashSet<>();
            for (Long roleId : roleIds) {
                permIds.addAll(authRolePermissionMapper.selectPermissionIdsByRoleId(roleId));
            }
            if (!permIds.isEmpty()) {
                List<AuthPermissionDO> perms = authPermissionMapper.selectBatchIds(new ArrayList<>(permIds));
                Map<String, Set<String>> subjectMetrics = new LinkedHashMap<>();
                Map<String, Set<String>> subjectDimensions = new LinkedHashMap<>();
                for (AuthPermissionDO perm : perms) {
                    String rt = perm.getResourceType();
                    if ("METRIC".equals(rt)) {
                        subjectMetrics.computeIfAbsent("默认主题", k -> new LinkedHashSet<>()).add(perm.getResourceId());
                    } else if ("DIMENSION".equals(rt)) {
                        subjectDimensions.computeIfAbsent("默认主题", k -> new LinkedHashSet<>()).add(perm.getResourceId());
                    } else if ("SUBJECT".equals(rt)) {
                        subjectMetrics.computeIfAbsent(perm.getResourceId(), k -> new LinkedHashSet<>());
                        subjectDimensions.computeIfAbsent(perm.getResourceId(), k -> new LinkedHashSet<>());
                    }
                }
                for (String subject : subjectMetrics.keySet()) {
                    metricPermissions.add(new MetricPermissionGroupDTO(
                            subject,
                            new ArrayList<>(subjectMetrics.getOrDefault(subject, Collections.emptySet())),
                            new ArrayList<>(subjectDimensions.getOrDefault(subject, Collections.emptySet()))
                    ));
                }
            }
        }
        dto.setMetricPermissions(metricPermissions);

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
