package com.cyan.dataauth.domain.rolepermission.repository;

import java.util.List;
import java.util.Map;

/**
 * 角色权限关联仓储
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthRolePermissionRepository {

    /**
     * 根据角色ID查询权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(Long roleId);

    /**
     * 查询所有关联，按权限ID分组角色ID
     */
    Map<Long, List<Long>> getRoleIdsGroupedByPermissionId();

    /**
     * 查询所有关联，按角色ID分组权限ID
     */
    Map<Long, List<Long>> getPermissionIdsGroupedByRoleId();

    /**
     * 根据权限ID删除关联
     */
    void deleteByPermissionId(Long permissionId);

    /**
     * 保存角色权限关联
     */
    void save(Long roleId, Long permissionId);
}
