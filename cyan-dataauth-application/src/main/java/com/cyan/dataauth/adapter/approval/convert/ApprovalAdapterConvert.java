package com.cyan.dataauth.adapter.approval.convert;

import com.cyan.dataauth.application.approval.bo.ApprovalBO;
import com.cyan.dataauth.dto.ApprovalDTO;
import com.cyan.dataauth.dto.PageResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 审批适配器转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class ApprovalAdapterConvert {

    /**
     * BO转DTO
     */
    public ApprovalDTO toApprovalDTO(ApprovalBO approvalBO) {
        if (approvalBO == null) {
            return null;
        }
        ApprovalDTO dto = new ApprovalDTO();
        dto.setApprovalId(approvalBO.getApprovalId());
        dto.setApplicantPassport(approvalBO.getApplicantPassport());
        dto.setApprovalType(approvalBO.getApprovalType());
        dto.setResourceType(approvalBO.getResourceType());
        dto.setResourceId(approvalBO.getResourceId());
        dto.setAction(approvalBO.getAction());
        dto.setReason(approvalBO.getReason());
        dto.setStatus(approvalBO.getStatus());
        dto.setCurrentNode(approvalBO.getCurrentNode());
        dto.setExpireDays(approvalBO.getExpireDays());
        dto.setSubmittedAt(approvalBO.getSubmittedAt());
        dto.setHandledAt(approvalBO.getHandledAt());
        dto.setOperatorPassport(approvalBO.getOperatorPassport());
        dto.setComment(approvalBO.getComment());
        dto.setCreatedBy(approvalBO.getCreatedBy());
        dto.setUpdatedBy(approvalBO.getUpdatedBy());
        dto.setCreatedAt(approvalBO.getCreatedAt());
        dto.setUpdatedAt(approvalBO.getUpdatedAt());
        dto.setDeletedAt(approvalBO.getDeletedAt());
        return dto;
    }

    /**
     * BO列表转DTO列表
     */
    public List<ApprovalDTO> toApprovalDTOList(List<ApprovalBO> approvalBOs) {
        return Optional.ofNullable(approvalBOs).orElse(List.of())
                .stream().map(this::toApprovalDTO).collect(Collectors.toList());
    }

    /**
     * 分页BO转分页DTO
     */
    public PageResult<ApprovalDTO> toPageResult(com.cyan.arch.common.api.Page<ApprovalBO> page) {
        return new PageResult<>(toApprovalDTOList(page.getData()), page.getTotal());
    }
}
