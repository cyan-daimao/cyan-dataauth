package com.cyan.dataauth.infra.persistence.approval.convert;

import com.cyan.dataauth.domain.approval.AuthApproval;
import com.cyan.dataauth.infra.persistence.approval.dos.AuthApprovalDO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 审批仓储转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthApprovalInfraConvert {

    /**
     * DO转Domain
     */
    public AuthApproval toAuthApproval(AuthApprovalDO approvalDO) {
        if (approvalDO == null) {
            return null;
        }
        return new AuthApproval()
                .setId(approvalDO.getId() != null ? String.valueOf(approvalDO.getId()) : null)
                .setApprovalId(approvalDO.getApprovalId())
                .setApplicantPassport(approvalDO.getApplicantPassport())
                .setApprovalType(approvalDO.getApprovalType())
                .setResourceType(approvalDO.getResourceType())
                .setResourceId(approvalDO.getResourceId())
                .setAction(approvalDO.getAction())
                .setReason(approvalDO.getReason())
                .setStatus(approvalDO.getStatus())
                .setCurrentNode(approvalDO.getCurrentNode())
                .setExpireDays(approvalDO.getExpireDays())
                .setSubmittedAt(approvalDO.getSubmittedAt())
                .setHandledAt(approvalDO.getHandledAt())
                .setOperatorPassport(approvalDO.getOperatorPassport())
                .setComment(approvalDO.getComment())
                .setCreatedBy(approvalDO.getCreatedBy())
                .setUpdatedBy(approvalDO.getUpdatedBy())
                .setCreatedAt(approvalDO.getCreatedAt())
                .setUpdatedAt(approvalDO.getUpdatedAt())
                .setDeletedAt(approvalDO.getDeletedAt());
    }

    /**
     * DO列表转Domain列表
     */
    public List<AuthApproval> toAuthApprovalList(List<AuthApprovalDO> approvalDOs) {
        return Optional.ofNullable(approvalDOs).orElse(List.of())
                .stream().map(this::toAuthApproval).collect(Collectors.toList());
    }

    /**
     * Domain转DO
     */
    public AuthApprovalDO toAuthApprovalDO(AuthApproval approval) {
        if (approval == null) {
            return null;
        }
        return new AuthApprovalDO()
                .setId(approval.getId() != null ? Long.valueOf(approval.getId()) : null)
                .setApprovalId(approval.getApprovalId())
                .setApplicantPassport(approval.getApplicantPassport())
                .setApprovalType(approval.getApprovalType())
                .setResourceType(approval.getResourceType())
                .setResourceId(approval.getResourceId())
                .setAction(approval.getAction())
                .setReason(approval.getReason())
                .setStatus(approval.getStatus())
                .setCurrentNode(approval.getCurrentNode())
                .setExpireDays(approval.getExpireDays())
                .setSubmittedAt(approval.getSubmittedAt())
                .setHandledAt(approval.getHandledAt())
                .setOperatorPassport(approval.getOperatorPassport())
                .setComment(approval.getComment())
                .setCreatedBy(approval.getCreatedBy())
                .setUpdatedBy(approval.getUpdatedBy())
                .setCreatedAt(approval.getCreatedAt())
                .setUpdatedAt(approval.getUpdatedAt())
                .setDeletedAt(approval.getDeletedAt());
    }
}
