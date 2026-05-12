package com.cyan.dataauth.infra.persistence.audit.convert;

import com.cyan.dataauth.domain.audit.AuthAuditLog;
import com.cyan.dataauth.infra.persistence.audit.dos.AuthAuditLogDO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 审计日志仓储转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthAuditLogInfraConvert {

    /**
     * DO转Domain
     */
    public AuthAuditLog toAuthAuditLog(AuthAuditLogDO logDO) {
        if (logDO == null) {
            return null;
        }
        return new AuthAuditLog()
                .setId(logDO.getId() != null ? String.valueOf(logDO.getId()) : null)
                .setLogId(logDO.getLogId())
                .setUserId(logDO.getUserId())
                .setAction(logDO.getAction())
                .setResourceType(logDO.getResourceType())
                .setResourceId(logDO.getResourceId())
                .setOriginalSql(logDO.getOriginalSql())
                .setRewrittenSql(logDO.getRewrittenSql())
                .setIp(logDO.getIp())
                .setCostTimeMs(logDO.getCostTimeMs())
                .setRiskLevel(logDO.getRiskLevel())
                .setTimestamp(logDO.getTimestamp())
                .setCreatedBy(logDO.getCreatedBy())
                .setUpdatedBy(logDO.getUpdatedBy())
                .setCreatedAt(logDO.getCreatedAt())
                .setUpdatedAt(logDO.getUpdatedAt())
                .setDeletedAt(logDO.getDeletedAt());
    }

    /**
     * DO列表转Domain列表
     */
    public List<AuthAuditLog> toAuthAuditLogList(List<AuthAuditLogDO> logDOs) {
        return Optional.ofNullable(logDOs).orElse(List.of())
                .stream().map(this::toAuthAuditLog).collect(Collectors.toList());
    }

    /**
     * Domain转DO
     */
    public AuthAuditLogDO toAuthAuditLogDO(AuthAuditLog log) {
        if (log == null) {
            return null;
        }
        return new AuthAuditLogDO()
                .setId(log.getId() != null ? Long.valueOf(log.getId()) : null)
                .setLogId(log.getLogId())
                .setUserId(log.getUserId())
                .setAction(log.getAction())
                .setResourceType(log.getResourceType())
                .setResourceId(log.getResourceId())
                .setOriginalSql(log.getOriginalSql())
                .setRewrittenSql(log.getRewrittenSql())
                .setIp(log.getIp())
                .setCostTimeMs(log.getCostTimeMs())
                .setRiskLevel(log.getRiskLevel())
                .setTimestamp(log.getTimestamp())
                .setCreatedBy(log.getCreatedBy())
                .setUpdatedBy(log.getUpdatedBy())
                .setCreatedAt(log.getCreatedAt())
                .setUpdatedAt(log.getUpdatedAt())
                .setDeletedAt(log.getDeletedAt());
    }
}
