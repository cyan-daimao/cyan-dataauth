package com.cyan.dataauth.infra.persistence.permission.convert;

import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.infra.persistence.permission.dos.AuthPermissionDO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 权限仓储转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthPermissionInfraConvert {

    /**
     * DO转Domain
     */
    public AuthPermission toAuthPermission(AuthPermissionDO permissionDO) {
        if (permissionDO == null) {
            return null;
        }
        return new AuthPermission()
                .setId(permissionDO.getId() != null ? String.valueOf(permissionDO.getId()) : null)
                .setResourceType(permissionDO.getResourceType())
                .setResourceId(permissionDO.getResourceId())
                .setAction(permissionDO.getAction())
                .setDescription(permissionDO.getDescription())
                .setCreatedBy(permissionDO.getCreatedBy())
                .setUpdatedBy(permissionDO.getUpdatedBy())
                .setCreatedAt(permissionDO.getCreatedAt())
                .setUpdatedAt(permissionDO.getUpdatedAt())
                .setDeletedAt(permissionDO.getDeletedAt());
    }

    /**
     * DO列表转Domain列表
     */
    public List<AuthPermission> toAuthPermissionList(List<AuthPermissionDO> permissionDOs) {
        return Optional.ofNullable(permissionDOs).orElse(List.of())
                .stream().map(this::toAuthPermission).collect(Collectors.toList());
    }
}
