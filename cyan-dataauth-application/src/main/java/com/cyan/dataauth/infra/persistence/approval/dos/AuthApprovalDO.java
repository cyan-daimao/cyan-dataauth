package com.cyan.dataauth.infra.persistence.approval.dos;

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
 * 审批单数据对象
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("auth_approval")
public class AuthApprovalDO {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 审批单号
     */
    private String approvalId;

    /**
     * 申请人护照
     */
    private String applicantPassport;

    /**
     * 审批类型
     */
    private String approvalType;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 资源标识
     */
    private String resourceId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 状态
     */
    private String status;

    /**
     * 当前节点
     */
    private String currentNode;

    /**
     * 过期天数
     */
    private Integer expireDays;

    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;

    /**
     * 处理时间
     */
    private LocalDateTime handledAt;

    /**
     * 操作人护照
     */
    private String operatorPassport;

    /**
     * 审批意见
     */
    private String comment;

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
