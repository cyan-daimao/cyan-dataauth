package com.cyan.dataauth.application.approval.convert;

import com.cyan.dataauth.application.approval.bo.ApprovalBO;
import com.cyan.dataauth.domain.approval.AuthApproval;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 审批应用层转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthApprovalAppConvert {

    /**
     * Domain转BO
     */
    public ApprovalBO toApprovalBO(AuthApproval approval) {
        if (approval == null) {
            return null;
        }
        return new ApprovalBO()
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

    /**
     * Domain列表转BO列表
     */
    public List<ApprovalBO> toApprovalBOList(List<AuthApproval> approvals) {
        return Optional.ofNullable(approvals).orElse(List.of())
                .stream().map(this::toApprovalBO).collect(Collectors.toList());
    }

    /**
     * Cmd转Domain
     */
    public AuthApproval toAuthApproval(com.cyan.dataauth.cmd.ApprovalSubmitCmd cmd) {
        if (cmd == null) {
            return null;
        }
        return new AuthApproval()
                .setApplicantPassport(cmd.getApplicantPassport())
                .setApprovalType(cmd.getApprovalType())
                .setResourceType(cmd.getResourceType())
                .setResourceId(cmd.getResourceId())
                .setAction(cmd.getAction())
                .setReason(cmd.getReason())
                .setExpireDays(cmd.getExpireDays());
    }
}
