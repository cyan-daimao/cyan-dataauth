package com.cyan.dataauth.adapter.permission.convert;

import com.cyan.dataauth.application.permission.bo.PermissionBO;
import com.cyan.dataauth.dto.PermissionDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 权限适配器转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthPermissionAdapterConvert {

    /**
     * BO转DTO
     */
    public PermissionDTO toPermissionDTO(PermissionBO permissionBO) {
        if (permissionBO == null) {
            return null;
        }
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permissionBO.getId() != null ? Long.valueOf(permissionBO.getId()) : null);
        dto.setResourceType(permissionBO.getResourceType());
        dto.setResourceId(permissionBO.getResourceId());
        dto.setAction(permissionBO.getAction());
        dto.setDescription(permissionBO.getDescription());
        dto.setCreatedBy(permissionBO.getCreatedBy());
        dto.setUpdatedBy(permissionBO.getUpdatedBy());
        dto.setCreatedAt(permissionBO.getCreatedAt());
        dto.setUpdatedAt(permissionBO.getUpdatedAt());
        dto.setDeletedAt(permissionBO.getDeletedAt());
        return dto;
    }

    /**
     * BO列表转DTO列表
     */
    public List<PermissionDTO> toPermissionDTOList(List<PermissionBO> permissionBOs) {
        return Optional.ofNullable(permissionBOs).orElse(List.of())
                .stream().map(this::toPermissionDTO).collect(Collectors.toList());
    }
}
