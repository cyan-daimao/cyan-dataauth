package com.cyan.dataauth.application.role.impl;

import com.cyan.dataauth.application.role.AuthRoleService;
import com.cyan.dataauth.application.role.bo.RoleBO;
import com.cyan.dataauth.application.role.convert.AuthRoleAppConvert;
import com.cyan.dataauth.cmd.RoleCreateCmd;
import com.cyan.dataauth.cmd.RoleUpdateCmd;
import com.cyan.dataauth.domain.role.AuthRole;
import com.cyan.dataauth.domain.role.repository.AuthRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthRoleServiceImpl implements AuthRoleService {

    private final AuthRoleRepository authRoleRepository;
    private final AuthRoleAppConvert authRoleAppConvert;

    @Override
    public List<RoleBO> list() {
        List<AuthRole> roles = authRoleRepository.list();
        return authRoleAppConvert.toRoleBOList(roles);
    }

    @Override
    @Transactional
    public RoleBO create(RoleCreateCmd cmd) {
        AuthRole role = authRoleAppConvert.toAuthRole(cmd);
        role = role.create(authRoleRepository);
        return authRoleAppConvert.toRoleBO(role);
    }

    @Override
    @Transactional
    public RoleBO update(String id, RoleUpdateCmd cmd) {
        AuthRole role = authRoleAppConvert.toAuthRole(id, cmd);
        role = role.update(authRoleRepository);
        return authRoleAppConvert.toRoleBO(role);
    }

    @Override
    @Transactional
    public void delete(String id) {
        AuthRole role = authRoleRepository.getById(id);
        if (role != null) {
            role.delete(authRoleRepository);
        }
    }

    @Override
    @Transactional
    public void assignPermissions(String roleId, List<String> permissionIds) {
        AuthRole role = authRoleRepository.getById(roleId);
        if (role != null) {
            role.assignPermissions(permissionIds, authRoleRepository);
        }
    }
}
