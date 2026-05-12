package com.cyan.dataauth.domain.audit;

import com.cyan.dataauth.domain.audit.repository.AuthAuditLogRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 审计日志
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthAuditLog {

    /**
     * 主键id
     */
    private String id;

    /**
     * 日志编号
     */
    private String logId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 资源标识
     */
    private String resourceId;

    /**
     * 原始SQL
     */
    private String originalSql;

    /**
     * 改写后SQL
     */
    private String rewrittenSql;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 耗时(ms)
     */
    private Long costTimeMs;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 删除时间（逻辑删除）
     */
    private LocalDateTime deletedAt;

    /**
     * 记录日志
     */
    public AuthAuditLog record(AuthAuditLogRepository auditLogRepository) {
        if (logId == null) {
            logId = "AUD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        }
        if (riskLevel == null) {
            riskLevel = "LOW";
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        createdAt = timestamp;
        updatedAt = timestamp;
        auditLogRepository.save(this);
        return this;
    }
}
