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
     * 根据ID列表查询
     */
    List<AuthRole> listByIds(List<Long> ids);

    /**
     * 根据编码列表查询
     */
    List<AuthRole> listByCodes(List<String> codes);

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

    /**
     * 追加单个权限（不删除已有）
     */
    void addPermission(String roleId, String permissionId);

    /**
     * 根据资源查找权限ID
     */
    String findPermissionIdByResource(String resourceType, String resourceId, String action);

    /**
     * 按角色ID和资源类型查询关联的权限项ID (Round2: ready)
     */
    List<String> selectPermissionIdsByRoleIdAndResourceType(Long roleId, String resourceType);

    /**
     * 删除角色下指定资源类型的权限关联 (Round2: ready)
     */
    void deleteRolePermissionsByResourceType(String roleId, String resourceType);

    /**
     * 查询角色下指定资源类型的功能权限key列表 (Round2: ready)
     */
    List<String> selectFunctionPermissionKeysByRoleId(String roleId, String resourceType);
}
