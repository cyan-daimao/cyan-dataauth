package com.cyan.dataauth.domain.role.repository;

import com.cyan.dataauth.domain.role.AuthRole;

import java.util.List;

/**
 * 角色仓储
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthRoleRepository {

    /**
     * 根据id查询
     */
    AuthRole getById(String id);

    /**
     * 根据编码查询
     */
    AuthRole getByCode(String code);

    /**
     * 列表查询
     */
    List<AuthRole> list();

    /**
     * 保存
     */
    AuthRole save(AuthRole role);

    /**
     * 更新
     */
    AuthRole update(AuthRole role);

    /**
     * 删除
     */
    void delete(String id);

    /**
     * 删除角色的权限关联
     */
    void deleteRolePermissions(String roleId);

    /**
     * 分配权限
     */
    void assignPermissions(String roleId, List<String> permissionIds);
}
