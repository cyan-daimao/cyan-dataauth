package com.cyan.dataauth.application.permission.convert;

import com.cyan.dataauth.application.permission.bo.PermissionBO;
import com.cyan.dataauth.domain.permission.AuthPermission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 权限应用层转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthPermissionAppConvert {

    /**
     * Domain转BO
     */
    public PermissionBO toPermissionBO(AuthPermission permission) {
        if (permission == null) {
            return null;
        }
        return new PermissionBO()
                .setId(permission.getId())
                .setResourceType(permission.getResourceType())
                .setResourceId(permission.getResourceId())
                .setAction(permission.getAction())
                .setDescription(permission.getDescription())
                .setCreatedBy(permission.getCreatedBy())
                .setUpdatedBy(permission.getUpdatedBy())
                .setCreatedAt(permission.getCreatedAt())
                .setUpdatedAt(permission.getUpdatedAt())
                .setDeletedAt(permission.getDeletedAt());
    }

    /**
     * Domain列表转BO列表
     */
    public List<PermissionBO> toPermissionBOList(List<AuthPermission> permissions) {
        return Optional.ofNullable(permissions).orElse(List.of())
                .stream().map(this::toPermissionBO).collect(Collectors.toList());
    }
}
