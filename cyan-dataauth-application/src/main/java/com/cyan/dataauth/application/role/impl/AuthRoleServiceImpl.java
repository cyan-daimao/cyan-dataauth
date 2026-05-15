package com.cyan.dataauth.application.role.impl;

import com.cyan.dataauth.application.role.AuthRoleService;
import com.cyan.dataauth.application.role.bo.RoleBO;
import com.cyan.dataauth.application.role.convert.AuthRoleAppConvert;
import com.cyan.dataauth.cmd.RoleCreateCmd;
import com.cyan.dataauth.cmd.RoleUpdateCmd;
import com.cyan.dataauth.domain.role.AuthRole;
import com.cyan.dataauth.domain.role.repository.AuthRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthRoleServiceImpl implements AuthRoleService {

    private final AuthRoleRepository authRoleRepository;
    private final AuthRoleAppConvert authRoleAppConvert;

    @Override
    public List<RoleBO> list() {
        List<AuthRole> roles = authRoleRepository.list();
        List<RoleBO> bos = authRoleAppConvert.toRoleBOList(roles);
        // Round2: ready — 为每个角色填充功能权限key列表
        for (RoleBO bo : bos) {
            List<String> functionPermissions = authRoleRepository.selectFunctionPermissionKeysByRoleId(bo.getId(), "MENU");
            bo.setFunctionPermissions(functionPermissions);
        }
        return bos;
    }

    @Override
    @Transactional
    public RoleBO create(RoleCreateCmd cmd) {
        AuthRole role = authRoleAppConvert.toAuthRole(cmd);
        role = role.create(authRoleRepository);
        // Round1: ready — 处理功能权限关联
        // Round2: ready — 保存时会先清除旧MENU关联再添加新关联
        assignFunctionPermissions(role.getId(), cmd.getFunctionPermissionKeys());
        return authRoleAppConvert.toRoleBO(role);
    }

    @Override
    @Transactional
    public RoleBO update(String id, RoleUpdateCmd cmd) {
        AuthRole role = authRoleAppConvert.toAuthRole(id, cmd);
        role = role.update(authRoleRepository);
        // Round1: ready — 处理功能权限关联
        // Round2: ready — 保存时会先清除旧MENU关联再添加新关联
        assignFunctionPermissions(role.getId(), cmd.getFunctionPermissionKeys());
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

    /**
     * 根据 functionPermissionKeys 查找并关联功能权限 (Round2: ready)
     * 1. 先清除该角色所有 MENU 类型的旧权限关联（不影响数据权限/指标权限）
     * 2. 再逐个查找 permissionId 并追加关联
     */
    private void assignFunctionPermissions(String roleId, List<String> functionPermissionKeys) {
        // 清除该角色所有 MENU 类型的旧权限关联
        authRoleRepository.deleteRolePermissionsByResourceType(roleId, "MENU");

        if (functionPermissionKeys == null || functionPermissionKeys.isEmpty()) {
            return;
        }
        for (String key : functionPermissionKeys) {
            if (key == null || key.isBlank()) {
                continue;
            }
            String[] parts = parsePermissionKey(key);
            if (parts == null) {
                log.warn("功能权限Key格式错误: {}", key);
                continue;
            }
            String permissionId = authRoleRepository.findPermissionIdByResource(parts[0], parts[1], parts[2]);
            if (permissionId != null) {
                authRoleRepository.addPermission(roleId, permissionId);
            } else {
                log.warn("功能权限Key未找到对应权限项: {}", key);
            }
        }
    }

    /**
     * 解析功能权限Key: resourceType:resourceId:action
     * resourceId 本身可能包含冒号，如 MENU:meta:business-ds:datasource:VIEW
     */
    private String[] parsePermissionKey(String key) {
        int firstColon = key.indexOf(':');
        int lastColon = key.lastIndexOf(':');
        if (firstColon <= 0 || lastColon <= firstColon || lastColon >= key.length() - 1) {
            return null;
        }
        String resourceType = key.substring(0, firstColon);
        String resourceId = key.substring(firstColon + 1, lastColon);
        String action = key.substring(lastColon + 1);
        return new String[]{resourceType, resourceId, action};
    }
}
