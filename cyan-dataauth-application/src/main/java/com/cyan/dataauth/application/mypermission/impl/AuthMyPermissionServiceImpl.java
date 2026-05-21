package com.cyan.dataauth.application.mypermission.impl;

import com.cyan.dataauth.application.mypermission.AuthMyPermissionService;
import com.cyan.dataauth.domain.approval.AuthApproval;
import com.cyan.dataauth.domain.approval.repository.AuthApprovalRepository;
import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import com.cyan.dataauth.domain.rolepermission.repository.AuthRolePermissionRepository;
import com.cyan.dataauth.domain.userrole.repository.AuthUserRoleRepository;
import com.cyan.dataauth.dto.DataPermissionDTO;
import com.cyan.dataauth.dto.DatabasePermissionDTO;
import com.cyan.dataauth.dto.FunctionPermissionDTO;
import com.cyan.dataauth.dto.FunctionPermissionNodeDTO;
import com.cyan.dataauth.dto.MetricPermissionGroupDTO;
import com.cyan.dataauth.dto.MyPermissionDTO;
import com.cyan.dataauth.dto.PendingApprovalDTO;
import com.cyan.dataauth.dto.TablePermissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 我的权限应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthMyPermissionServiceImpl implements AuthMyPermissionService {

    private final AuthUserRoleRepository authUserRoleRepository;
    private final AuthRolePermissionRepository authRolePermissionRepository;
    private final AuthPermissionRepository authPermissionRepository;
    private final AuthApprovalRepository authApprovalRepository;

    private static final Map<String, String> MODULE_NAME_MAP = new LinkedHashMap<>();
    static {
        MODULE_NAME_MAP.put("meta", "元数据平台");
        MODULE_NAME_MAP.put("metrics", "指标平台");
        MODULE_NAME_MAP.put("sql-editor", "SQL查询");
        MODULE_NAME_MAP.put("data-work", "数据加工");
        MODULE_NAME_MAP.put("bi", "智能分析");
        MODULE_NAME_MAP.put("auth", "权限管理");
    }

    private static final Set<String> SUPER_ADMINS = Set.of("admin", "cyan1");

    @Override
    public MyPermissionDTO getMyPermissions(String passport) {
        MyPermissionDTO dto = new MyPermissionDTO();

        List<Long> roleIds = authUserRoleRepository.listRoleIdsByPassport(passport);
        if (roleIds == null || roleIds.isEmpty()) {
            dto.setFunctionPermissions(List.of());
            dto.setDataPermissions(List.of());
            dto.setMetricPermissions(List.of());
            dto.setPendingApprovals(List.of());
            return dto;
        }

        Set<Long> permIds = new HashSet<>();
        for (Long roleId : roleIds) {
            permIds.addAll(authRolePermissionRepository.selectPermissionIdsByRoleId(roleId));
        }
        List<AuthPermission> allPerms = permIds.isEmpty() ? List.of()
                : authPermissionRepository.listByIds(new ArrayList<>(permIds));

        dto.setFunctionPermissions(aggregateFunctionPermissions(allPerms));
        dto.setDataPermissions(aggregateDataPermissions(allPerms));
        dto.setMetricPermissions(aggregateMetricPermissions(allPerms));

        List<AuthApproval> approvals = authApprovalRepository.listPendingByApplicant(passport);
        List<PendingApprovalDTO> pendingApprovals = new ArrayList<>();
        if (approvals != null) {
            for (AuthApproval a : approvals) {
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

        return dto;
    }

    private List<FunctionPermissionDTO> aggregateFunctionPermissions(List<AuthPermission> allPerms) {
        Map<String, List<String>> moduleMap = new LinkedHashMap<>();
        for (AuthPermission perm : allPerms) {
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

    private List<DataPermissionDTO> aggregateDataPermissions(List<AuthPermission> allPerms) {
        Map<String, Set<String>> datasourceActions = new LinkedHashMap<>();
        Map<String, Set<String>> dbActions = new LinkedHashMap<>();
        Map<String, Map<String, Map<String, Set<String>>>> tableActions = new LinkedHashMap<>();

        for (AuthPermission perm : allPerms) {
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

        Set<String> allDs = new LinkedHashSet<>();
        allDs.addAll(datasourceActions.keySet());
        allDs.addAll(dbActions.keySet().stream().map(k -> k.split("\\.")[0]).collect(Collectors.toSet()));
        allDs.addAll(tableActions.keySet());

        List<DataPermissionDTO> result = new ArrayList<>();
        for (String ds : allDs) {
            List<DatabasePermissionDTO> dbList = new ArrayList<>();

            Set<String> dbNames = new LinkedHashSet<>();
            for (String dbKey : dbActions.keySet()) {
                String[] parts = dbKey.split("\\.");
                if (parts.length >= 2 && ds.equals(parts[0])) {
                    dbNames.add(parts[1]);
                }
            }
            dbNames.addAll(tableActions.getOrDefault(ds, Collections.emptyMap()).keySet());

            for (String db : dbNames) {
                List<TablePermissionDTO> tblList = new ArrayList<>();

                Map<String, Set<String>> tblMap = tableActions.getOrDefault(ds, Collections.emptyMap())
                        .getOrDefault(db, Collections.emptyMap());
                for (Map.Entry<String, Set<String>> tblEntry : tblMap.entrySet()) {
                    tblList.add(new TablePermissionDTO(tblEntry.getKey(), String.join(",", tblEntry.getValue()), null, null));
                }

                String dbKey = ds + "." + db;
                Set<String> dbAct = dbActions.get(dbKey);
                if (dbAct != null && !dbAct.isEmpty()) {
                    if (tblList.isEmpty()) {
                        tblList.add(new TablePermissionDTO("*", String.join(",", dbAct), null, null));
                    }
                }

                if (!tblList.isEmpty()) {
                    dbList.add(new DatabasePermissionDTO(db, tblList));
                }
            }

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

    private List<MetricPermissionGroupDTO> aggregateMetricPermissions(List<AuthPermission> allPerms) {
        Map<String, Set<String>> subjectMetrics = new LinkedHashMap<>();
        Map<String, Set<String>> subjectDimensions = new LinkedHashMap<>();

        for (AuthPermission perm : allPerms) {
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

    private String resolveSubjectCode(String resourceId) {
        if (resourceId == null) {
            return "默认主题";
        }
        int idx = resourceId.indexOf(':');
        return idx > 0 ? resourceId.substring(0, idx) : "默认主题";
    }

    @Override
    public List<FunctionPermissionNodeDTO> getFunctionPermissionTree(String passport) {
        List<FunctionPermissionNodeDTO> tree = buildHardcodedTree();
        if (passport != null && SUPER_ADMINS.contains(passport)) {
            tree.add(0, new FunctionPermissionNodeDTO("*", "超级管理员", "MENU", null));
        }
        return tree;
    }

    private List<FunctionPermissionNodeDTO> buildHardcodedTree() {
        List<FunctionPermissionNodeDTO> tree = new ArrayList<>();

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

        FunctionPermissionNodeDTO metric = new FunctionPermissionNodeDTO("metric", "指标平台", "MENU", new ArrayList<>());
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:dashboard", "指标概览", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:definition", "指标定义", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:dictionary", "指标字典", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:analysis", "指标分析", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:dimension", "维度管理", "MENU", null));
        metric.getChildren().add(new FunctionPermissionNodeDTO("metric:config", "指标配置", "MENU", null));
        tree.add(metric);

        tree.add(new FunctionPermissionNodeDTO("sql-editor", "SQL查询", "MENU", null));
        tree.add(new FunctionPermissionNodeDTO("data-work", "数据加工", "MENU", null));

        FunctionPermissionNodeDTO bi = new FunctionPermissionNodeDTO("bi", "智能分析", "MENU", new ArrayList<>());
        bi.getChildren().add(new FunctionPermissionNodeDTO("bi:chart", "图表分析", "MENU", null));
        bi.getChildren().add(new FunctionPermissionNodeDTO("bi:dashboard", "看板管理", "MENU", null));
        bi.getChildren().add(new FunctionPermissionNodeDTO("bi:dataset", "数据集管理", "MENU", null));
        tree.add(bi);

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
