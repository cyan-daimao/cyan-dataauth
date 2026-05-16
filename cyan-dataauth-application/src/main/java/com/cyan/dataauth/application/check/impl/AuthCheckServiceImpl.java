package com.cyan.dataauth.application.check.impl;

import com.cyan.dataauth.application.check.AuthCheckService;
import com.cyan.dataauth.application.check.bo.*;
import com.cyan.dataauth.application.check.convert.AuthCheckAppConvert;
import com.cyan.dataauth.cmd.*;
import com.cyan.dataauth.domain.audit.AuthAuditLog;
import com.cyan.dataauth.domain.audit.repository.AuthAuditLogRepository;
import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.domain.permission.PermissionChecker;
import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import com.cyan.dataauth.enums.SecurityLevel;
import com.cyan.dataauth.infra.persistence.role.dos.AuthRoleDO;
import com.cyan.dataauth.infra.persistence.role.mappers.AuthRoleMapper;
import com.cyan.dataauth.infra.persistence.userrole.mappers.AuthUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 权限校验应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCheckServiceImpl implements AuthCheckService {

    private final PermissionChecker permissionChecker;
    private final AuthPermissionRepository authPermissionRepository;
    private final AuthAuditLogRepository authAuditLogRepository;
    private final AuthCheckAppConvert authCheckAppConvert;
    private final AuthUserRoleMapper authUserRoleMapper;
    private final AuthRoleMapper authRoleMapper;
    private final RestTemplate restTemplate;

    private static final Pattern FROM_PATTERN = Pattern.compile(
            "\\bFROM\\b\\s+([a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)?(?:\\s+(?:AS\\s+)?[a-zA-Z_][a-zA-Z0-9_]*)?)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern JOIN_PATTERN = Pattern.compile(
            "\\bJOIN\\b\\s+([a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)?(?:\\s+(?:AS\\s+)?[a-zA-Z_][a-zA-Z0-9_]*)?)",
            Pattern.CASE_INSENSITIVE);

    @Override
    public AuthCheckResultBO check(AuthCheckCmd cmd) {
        boolean permitted = permissionChecker.hasPermission(cmd.getPassport(), cmd.getResourceType(), cmd.getResourceId(), cmd.getAction());
        String reason = permitted ? null : "无权限访问资源: " + cmd.getResourceId();

        recordAudit(cmd.getPassport(), "PERMISSION_CHECK", cmd.getResourceType(), cmd.getResourceId(),
                null, null, null, null, permitted ? "LOW" : "HIGH");

        return authCheckAppConvert.toAuthCheckResultBO(permitted, reason);
    }

    @Override
    public FilterSqlResultBO filterSql(FilterSqlCmd cmd) {
        String sql = cmd.getSql();
        String passport = cmd.getPassport();

        Set<String> tableNames = extractTableNames(sql);
        for (String tableName : tableNames) {
            String resourceId = normalizeTableName(tableName);
            if (!permissionChecker.hasPermission(passport, "TABLE", resourceId, "SELECT")) {
                // 层级权限推导失败后，检查表是否是 L1 公开
                if (isPublicTable(tableName)) {
                    continue;
                }
                recordAudit(passport, "SQL_EXECUTE", "TABLE", resourceId,
                        sql, null, null, null, "HIGH");
                return authCheckAppConvert.toFilterSqlResultBO(false, "无权限访问表 " + resourceId, sql, null);
            }
        }

        recordAudit(passport, "SQL_EXECUTE", "TABLE", String.join(",", tableNames),
                sql, sql, null, null, "LOW");

        return authCheckAppConvert.toFilterSqlResultBO(true, null, sql, sql);
    }

    /**
     * 查询元数据服务判断表是否是 L1 公开
     */
    private boolean isPublicTable(String tableName) {
        log.info("[isPublicTable] 开始查询表密级, tableName={}", tableName);
        try {
            String url = "http://cyan-dataman/rpc/v1/agent/meta/tables/" + tableName + "/security-level";
            log.info("[isPublicTable] 调用URL: {}", url);
            @SuppressWarnings("rawtypes")
            java.util.Map response = restTemplate.getForObject(url, java.util.Map.class);
            log.info("[isPublicTable] 响应: {}", response);
            if (response == null) {
                log.warn("[isPublicTable] 响应为空, tableName={}", tableName);
                return false;
            }
            Object code = response.get("code");
            Object data = response.get("data");
            log.info("[isPublicTable] code={}, data={}, tableName={}", code, data, tableName);
            boolean isL1 = "L1".equals(data);
            log.info("[isPublicTable] 结果: isL1={}, tableName={}", isL1, tableName);
            return isL1;
        } catch (Exception e) {
            log.warn("[isPublicTable] 查询表密级失败: tableName={}", tableName, e);
            return false;
        }
    }

    @Override
    public List<ResourceTreeNodeBO> listResources(String passport, String resourceType) {
        List<AuthPermission> permissions = authPermissionRepository.selectByPassport(passport);

        List<AuthPermission> filtered = permissions.stream()
                .filter(p -> {
                    if (!"TABLE".equals(p.getResourceType()) && !"DB".equals(p.getResourceType())
                            && !"DATASOURCE".equals(p.getResourceType())) {
                        return false;
                    }
                    if (resourceType != null && !resourceType.isEmpty()) {
                        return resourceType.equals(p.getResourceType());
                    }
                    return true;
                })
                .collect(Collectors.toList());

        Map<String, ResourceTreeNodeBO> dsMap = new LinkedHashMap<>();
        Map<String, ResourceTreeNodeBO> dbMap = new LinkedHashMap<>();

        for (AuthPermission p : filtered) {
            String[] parts = p.getResourceId().split("\\.");
            if (parts.length < 2) continue;

            String dsName = parts[0];
            String dbName = parts[1];
            String tblName = parts.length >= 3 ? parts[2] : null;

            String dsKey = dsName;
            ResourceTreeNodeBO dsNode = dsMap.computeIfAbsent(dsKey, k -> {
                ResourceTreeNodeBO n = new ResourceTreeNodeBO();
                n.setId(k);
                n.setName(k);
                n.setType("DATASOURCE");
                n.setChildren(new ArrayList<>());
                return n;
            });

            String dbKey = dsName + "." + dbName;
            ResourceTreeNodeBO dbNode = dbMap.computeIfAbsent(dbKey, k -> {
                ResourceTreeNodeBO n = new ResourceTreeNodeBO();
                n.setId(k);
                n.setName(dbName);
                n.setType("DB");
                n.setChildren(new ArrayList<>());
                dsNode.getChildren().add(n);
                return n;
            });

            if (tblName != null && "TABLE".equals(p.getResourceType())) {
                ResourceTreeNodeBO tblNode = new ResourceTreeNodeBO();
                tblNode.setId(p.getResourceId());
                tblNode.setName(tblName);
                tblNode.setType("TABLE");
                tblNode.setPermission(p.getAction());
                dbNode.getChildren().add(tblNode);
            }
        }

        return new ArrayList<>(dsMap.values());
    }

    @Override
    public MetricCheckResultBO metricCheck(MetricCheckCmd cmd) {
        List<MetricCheckItemResultBO> results = new ArrayList<>();
        boolean allPermitted = true;

        for (MetricCheckCmd.CheckItem item : cmd.getCheckItems()) {
            boolean permitted = permissionChecker.hasPermission(cmd.getPassport(), item.getResourceType(), item.getResourceId(), item.getAction());
            if (!permitted) {
                allPermitted = false;
            }
            results.add(authCheckAppConvert.toMetricCheckItemResultBO(
                    item.getResourceType(), item.getResourceId(), permitted,
                    permitted ? null : "无权限使用" + item.getResourceType() + " " + item.getResourceId()));
        }

        recordAudit(cmd.getPassport(), "PERMISSION_CHECK", "METRIC", null,
                null, null, null, null, allPermitted ? "LOW" : "HIGH");

        return authCheckAppConvert.toMetricCheckResultBO(allPermitted, results);
    }

    @Override
    public List<MetricResourceBO> listMetricResources(String passport, String resourceType, String subjectCode, String action) {
        List<AuthPermission> permissions = authPermissionRepository.selectByPassport(passport);

        return permissions.stream()
                .filter(p -> resourceType.equals(p.getResourceType()))
                .filter(p -> action == null || action.isEmpty() || action.equals(p.getAction()))
                .map(p -> authCheckAppConvert.toMetricResourceBO(
                        p.getId() != null ? p.getId().toString() : null,
                        p.getResourceId(),
                        p.getDescription() != null ? p.getDescription() : p.getResourceId(),
                        subjectCode, null))
                .collect(Collectors.toList());
    }

    @Override
    public MetricFilterSqlResultBO metricFilterSql(MetricFilterSqlCmd cmd) {
        String sql = cmd.getSql();
        String passport = cmd.getPassport();

        if (cmd.getMetricCodes() != null) {
            for (String metricCode : cmd.getMetricCodes()) {
                if (!permissionChecker.hasPermission(passport, "METRIC", metricCode, "USE")) {
                    recordAudit(passport, "SQL_EXECUTE", "METRIC", metricCode,
                            sql, null, null, null, "HIGH");
                    return authCheckAppConvert.toMetricFilterSqlResultBO(false, "无权限使用指标 " + metricCode, sql, null);
                }
            }
        }

        if (cmd.getDimCodes() != null) {
            for (String dimCode : cmd.getDimCodes()) {
                if (!permissionChecker.hasPermission(passport, "DIMENSION", dimCode, "USE")) {
                    recordAudit(passport, "SQL_EXECUTE", "DIMENSION", dimCode,
                            sql, null, null, null, "HIGH");
                    return authCheckAppConvert.toMetricFilterSqlResultBO(false, "无权限使用维度 " + dimCode, sql, null);
                }
            }
        }

        recordAudit(passport, "SQL_EXECUTE", "METRIC",
                String.join(",", cmd.getMetricCodes() != null ? cmd.getMetricCodes() : Collections.emptyList()),
                sql, sql, null, null, "LOW");

        return authCheckAppConvert.toMetricFilterSqlResultBO(true, null, sql, sql);
    }

    private void recordAudit(String userId, String action, String resourceType, String resourceId,
                             String originalSql, String rewrittenSql, String ip, Long costTimeMs, String riskLevel) {
        AuthAuditLog log = new AuthAuditLog()
                .setUserId(userId)
                .setAction(action)
                .setResourceType(resourceType)
                .setResourceId(resourceId)
                .setOriginalSql(originalSql)
                .setRewrittenSql(rewrittenSql)
                .setIp(ip)
                .setCostTimeMs(costTimeMs)
                .setRiskLevel(riskLevel);
        log.record(authAuditLogRepository);
    }

    private Set<String> extractTableNames(String sql) {
        Set<String> tables = new LinkedHashSet<>();
        String upperSql = sql.toUpperCase();

        Matcher fromMatcher = FROM_PATTERN.matcher(sql);
        while (fromMatcher.find()) {
            tables.add(cleanTableName(fromMatcher.group(1)));
        }

        Matcher joinMatcher = JOIN_PATTERN.matcher(sql);
        while (joinMatcher.find()) {
            tables.add(cleanTableName(joinMatcher.group(1)));
        }

        int fromIdx = upperSql.indexOf("FROM");
        if (fromIdx >= 0) {
            int afterFrom = fromIdx + 4;
            String afterFromStr = sql.substring(afterFrom).trim();
            int endIdx = findClauseEnd(afterFromStr);
            String tableSection = afterFromStr.substring(0, endIdx);
            for (String part : tableSection.split(",")) {
                String t = cleanTableName(part.trim());
                if (!t.isEmpty()) {
                    tables.add(t);
                }
            }
        }

        return tables;
    }

    private int findClauseEnd(String s) {
        String upper = s.toUpperCase();
        int min = s.length();
        String[] keywords = {" WHERE ", " GROUP ", " ORDER ", " LIMIT ", " UNION ", " HAVING "};
        for (String kw : keywords) {
            int idx = upper.indexOf(kw);
            if (idx >= 0 && idx < min) {
                min = idx;
            }
        }
        return min;
    }

    private String cleanTableName(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        int spaceIdx = raw.indexOf(' ');
        if (spaceIdx > 0) {
            raw = raw.substring(0, spaceIdx);
        }
        return raw;
    }

    private String normalizeTableName(String tableName) {
        if (tableName == null) return "";
        if (tableName.contains(".")) {
            return tableName;
        }
        return tableName;
    }

    @Override
    public String getUserMaxSecurityLevel(String passport) {
        List<Long> roleIds = authUserRoleMapper.selectRoleIdsByPassport(passport);
        if (roleIds == null || roleIds.isEmpty()) {
            return SecurityLevel.L1.getCode();
        }
        List<AuthRoleDO> roles = authRoleMapper.selectBatchIds(roleIds);
        if (roles == null || roles.isEmpty()) {
            return SecurityLevel.L1.getCode();
        }
        return roles.stream()
                .map(r -> SecurityLevel.of(r.getMaxSecurityLevel()))
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(SecurityLevel::ordinal))
                .map(SecurityLevel::getCode)
                .orElse(SecurityLevel.L1.getCode());
    }
}
