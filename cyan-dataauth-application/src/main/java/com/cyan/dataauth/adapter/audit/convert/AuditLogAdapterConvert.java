package com.cyan.dataauth.adapter.audit.convert;

import com.cyan.dataauth.application.audit.bo.AuditLogBO;
import com.cyan.dataauth.dto.AuditLogDTO;
import com.cyan.dataauth.dto.PageResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 审计日志适配器转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuditLogAdapterConvert {

    /**
     * BO转DTO
     */
    public AuditLogDTO toAuditLogDTO(AuditLogBO auditLogBO) {
        if (auditLogBO == null) {
            return null;
        }
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(auditLogBO.getId());
        dto.setUserId(auditLogBO.getUserId());
        dto.setAction(auditLogBO.getAction());
        dto.setResourceType(auditLogBO.getResourceType());
        dto.setResourceId(auditLogBO.getResourceId());
        dto.setOriginalSql(auditLogBO.getOriginalSql());
        dto.setRewrittenSql(auditLogBO.getRewrittenSql());
        dto.setIp(auditLogBO.getIp());
        dto.setCostTimeMs(auditLogBO.getCostTimeMs());
        dto.setRiskLevel(auditLogBO.getRiskLevel());
        dto.setTimestamp(auditLogBO.getTimestamp());
        dto.setCreatedBy(auditLogBO.getCreatedBy());
        dto.setUpdatedBy(auditLogBO.getUpdatedBy());
        dto.setCreatedAt(auditLogBO.getCreatedAt());
        dto.setUpdatedAt(auditLogBO.getUpdatedAt());
        dto.setDeletedAt(auditLogBO.getDeletedAt());
        return dto;
    }

    /**
     * BO列表转DTO列表
     */
    public List<AuditLogDTO> toAuditLogDTOList(List<AuditLogBO> auditLogBOs) {
        return Optional.ofNullable(auditLogBOs).orElse(List.of())
                .stream().map(this::toAuditLogDTO).collect(Collectors.toList());
    }

    /**
     * 分页BO转分页DTO
     */
    public PageResult<AuditLogDTO> toPageResult(com.cyan.arch.common.api.Page<AuditLogBO> page) {
        return new PageResult<>(toAuditLogDTOList(page.getData()), page.getTotal());
    }
}
