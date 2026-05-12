package com.cyan.dataauth.application.approval.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 审批单业务对象
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ApprovalBO {

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
}
