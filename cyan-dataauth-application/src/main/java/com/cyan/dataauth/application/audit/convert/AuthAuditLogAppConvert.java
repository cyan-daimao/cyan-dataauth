package com.cyan.dataauth.application.audit.convert;

import com.cyan.dataauth.application.audit.bo.AuditLogBO;
import com.cyan.dataauth.domain.audit.AuthAuditLog;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 审计日志应用层转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthAuditLogAppConvert {

    /**
     * Domain转BO
     */
    public AuditLogBO toAuditLogBO(AuthAuditLog log) {
        if (log == null) {
            return null;
        }
        return new AuditLogBO()
                .setId(log.getId())
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

    /**
     * Domain列表转BO列表
     */
    public List<AuditLogBO> toAuditLogBOList(List<AuthAuditLog> logs) {
        return Optional.ofNullable(logs).orElse(List.of())
                .stream().map(this::toAuditLogBO).collect(Collectors.toList());
    }
}
