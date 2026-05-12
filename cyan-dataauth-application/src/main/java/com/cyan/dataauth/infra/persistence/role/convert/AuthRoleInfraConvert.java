package com.cyan.dataauth.infra.persistence.role.convert;

import com.cyan.dataauth.domain.role.AuthRole;
import com.cyan.dataauth.infra.persistence.role.dos.AuthRoleDO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 角色仓储转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthRoleInfraConvert {

    /**
     * DO转Domain
     */
    public AuthRole toAuthRole(AuthRoleDO roleDO) {
        if (roleDO == null) {
            return null;
        }
        return new AuthRole()
                .setId(roleDO.getId() != null ? String.valueOf(roleDO.getId()) : null)
                .setName(roleDO.getName())
                .setCode(roleDO.getCode())
                .setDescription(roleDO.getDescription())
                .setStatus(roleDO.getStatus())
                .setCreatedBy(roleDO.getCreatedBy())
                .setUpdatedBy(roleDO.getUpdatedBy())
                .setCreatedAt(roleDO.getCreatedAt())
                .setUpdatedAt(roleDO.getUpdatedAt())
                .setDeletedAt(roleDO.getDeletedAt());
    }

    /**
     * DO列表转Domain列表
     */
    public List<AuthRole> toAuthRoleList(List<AuthRoleDO> roleDOs) {
        return Optional.ofNullable(roleDOs).orElse(List.of())
                .stream().map(this::toAuthRole).collect(Collectors.toList());
    }

    /**
     * Domain转DO
     */
    public AuthRoleDO toAuthRoleDO(AuthRole role) {
        if (role == null) {
            return null;
        }
        return new AuthRoleDO()
                .setId(role.getId() != null ? Long.valueOf(role.getId()) : null)
                .setName(role.getName())
                .setCode(role.getCode())
                .setDescription(role.getDescription())
                .setStatus(role.getStatus())
                .setCreatedBy(role.getCreatedBy())
                .setUpdatedBy(role.getUpdatedBy())
                .setCreatedAt(role.getCreatedAt())
                .setUpdatedAt(role.getUpdatedAt())
                .setDeletedAt(role.getDeletedAt());
    }
}
