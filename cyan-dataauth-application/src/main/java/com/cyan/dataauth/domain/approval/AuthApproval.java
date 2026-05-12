package com.cyan.dataauth.domain.approval;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.dataauth.domain.approval.repository.AuthApprovalRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 审批单
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthApproval {

    /**
     * 主键id
     */
    private String id;

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

    /**
     * 提交审批
     */
    public AuthApproval submit(AuthApprovalRepository approvalRepository) {
        Assert.notBlank(applicantPassport, new SilentException("申请人不能为空"));
        Assert.notBlank(approvalType, new SilentException("审批类型不能为空"));
        Assert.notBlank(resourceType, new SilentException("资源类型不能为空"));
        Assert.notBlank(resourceId, new SilentException("资源标识不能为空"));
        approvalId = generateApprovalId();
        status = "PENDING";
        currentNode = "直属上级审批";
        submittedAt = LocalDateTime.now();
        createdAt = submittedAt;
        updatedAt = submittedAt;
        return approvalRepository.save(this);
    }

    /**
     * 审批通过
     */
    public AuthApproval approve(String operatorPassport, String comment, AuthApprovalRepository approvalRepository) {
        Assert.notBlank(approvalId, new SilentException("审批单号不能为空"));
        AuthApproval exist = approvalRepository.getByApprovalId(approvalId);
        Assert.notNull(exist, new SilentException("审批单不存在: " + approvalId));
        Assert.isTrue("PENDING".equals(exist.status), new SilentException("审批单状态不是待审批"));
        this.id = exist.id;
        this.status = "APPROVED";
        this.operatorPassport = operatorPassport;
        this.comment = comment;
        this.handledAt = LocalDateTime.now();
        this.updatedAt = this.handledAt;
        return approvalRepository.update(this);
    }

    /**
     * 审批驳回
     */
    public AuthApproval reject(String operatorPassport, String comment, AuthApprovalRepository approvalRepository) {
        Assert.notBlank(approvalId, new SilentException("审批单号不能为空"));
        AuthApproval exist = approvalRepository.getByApprovalId(approvalId);
        Assert.notNull(exist, new SilentException("审批单不存在: " + approvalId));
        Assert.isTrue("PENDING".equals(exist.status), new SilentException("审批单状态不是待审批"));
        this.id = exist.id;
        this.status = "REJECTED";
        this.operatorPassport = operatorPassport;
        this.comment = comment;
        this.handledAt = LocalDateTime.now();
        this.updatedAt = this.handledAt;
        return approvalRepository.update(this);
    }

    private String generateApprovalId() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seq = String.format("%03d", (int) (Math.random() * 1000));
        return "APV-" + date + "-" + seq;
    }
}
