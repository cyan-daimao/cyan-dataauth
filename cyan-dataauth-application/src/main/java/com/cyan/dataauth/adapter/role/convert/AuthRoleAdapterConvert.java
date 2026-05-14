package com.cyan.dataauth.adapter.role.convert;

import com.cyan.dataauth.application.role.bo.RoleBO;
import com.cyan.dataauth.dto.RoleDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 角色适配器转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthRoleAdapterConvert {

    /**
     * BO转DTO
     */
    public RoleDTO toRoleDTO(RoleBO roleBO) {
        if (roleBO == null) {
            return null;
        }
        RoleDTO dto = new RoleDTO();
        dto.setId(roleBO.getId() != null ? Long.valueOf(roleBO.getId()) : null);
        dto.setName(roleBO.getName());
        dto.setCode(roleBO.getCode());
        dto.setDescription(roleBO.getDescription());
        dto.setStatus(roleBO.getStatus());
        dto.setMaxSecurityLevel(roleBO.getMaxSecurityLevel());
        dto.setCreatedBy(roleBO.getCreatedBy());
        dto.setUpdatedBy(roleBO.getUpdatedBy());
        dto.setCreatedAt(roleBO.getCreatedAt());
        dto.setUpdatedAt(roleBO.getUpdatedAt());
        dto.setDeletedAt(roleBO.getDeletedAt());
        return dto;
    }

    /**
     * BO列表转DTO列表
     */
    public List<RoleDTO> toRoleDTOList(List<RoleBO> roleBOs) {
        return Optional.ofNullable(roleBOs).orElse(List.of())
                .stream().map(this::toRoleDTO).collect(Collectors.toList());
    }
}
