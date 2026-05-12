package com.cyan.dataauth.application.role.convert;

import com.cyan.dataauth.application.role.bo.RoleBO;
import com.cyan.dataauth.domain.role.AuthRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 角色应用层转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthRoleAppConvert {

    /**
     * Domain转BO
     */
    public RoleBO toRoleBO(AuthRole role) {
        if (role == null) {
            return null;
        }
        return new RoleBO()
                .setId(role.getId())
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

    /**
     * Domain列表转BO列表
     */
    public List<RoleBO> toRoleBOList(List<AuthRole> roles) {
        return Optional.ofNullable(roles).orElse(List.of())
                .stream().map(this::toRoleBO).collect(Collectors.toList());
    }

    /**
     * Cmd转Domain
     */
    public AuthRole toAuthRole(com.cyan.dataauth.cmd.RoleCreateCmd cmd) {
        if (cmd == null) {
            return null;
        }
        return new AuthRole()
                .setName(cmd.getName())
                .setCode(cmd.getCode())
                .setDescription(cmd.getDescription());
    }

    /**
     * Cmd转Domain（更新）
     */
    public AuthRole toAuthRole(String id, com.cyan.dataauth.cmd.RoleUpdateCmd cmd) {
        if (cmd == null) {
            return null;
        }
        return new AuthRole()
                .setId(id)
                .setName(cmd.getName())
                .setDescription(cmd.getDescription())
                .setStatus(cmd.getStatus());
    }
}
