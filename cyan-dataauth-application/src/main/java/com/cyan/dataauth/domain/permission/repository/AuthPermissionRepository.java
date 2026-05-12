package com.cyan.dataauth.domain.permission.repository;

import com.cyan.dataauth.domain.permission.AuthPermission;

import java.util.List;

/**
 * 权限仓储
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthPermissionRepository {

    /**
     * 根据id查询
     */
    AuthPermission getById(String id);

    /**
     * 列表查询
     */
    List<AuthPermission> list();

    /**
     * 根据用户护照查询权限
     */
    List<AuthPermission> selectByPassport(String passport);
}
