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

    /**
     * 根据资源查询权限
     */
    AuthPermission getByResource(String resourceType, String resourceId, String action);

    /**
     * 根据ID列表查询
     */
    List<AuthPermission> listByIds(List<Long> ids);

    /**
     * 根据资源类型查询
     */
    List<AuthPermission> listByResourceType(String resourceType);

    /**
     * 根据资源类型和资源标识查询
     */
    List<AuthPermission> listByResourceTypeAndResourceId(String resourceType, String resourceId);

    /**
     * 保存权限
     */
    AuthPermission save(AuthPermission permission);

    /**
     * 更新权限
     */
    AuthPermission update(AuthPermission permission);

    /**
     * 根据id删除
     */
    void delete(String id);
}
