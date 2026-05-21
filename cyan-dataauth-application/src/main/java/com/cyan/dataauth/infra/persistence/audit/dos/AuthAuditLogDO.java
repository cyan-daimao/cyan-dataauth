package com.cyan.dataauth.infra.persistence.audit.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 审计日志数据对象
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("auth_audit_log")
public class AuthAuditLogDO {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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
    @TableField("created_by")
    private String createdBy;

    /**
     * 更新人
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 删除时间（逻辑删除）
     */
    @TableField("deleted_at")
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;
}
