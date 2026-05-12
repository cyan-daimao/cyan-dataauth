package com.cyan.dataauth.application.role;

import com.cyan.dataauth.application.role.bo.RoleBO;
import com.cyan.dataauth.cmd.RoleCreateCmd;
import com.cyan.dataauth.cmd.RoleUpdateCmd;

import java.util.List;

/**
 * 角色应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthRoleService {

    /**
     * 列表查询
     */
    List<RoleBO> list();

    /**
     * 创建角色
     */
    RoleBO create(RoleCreateCmd cmd);

    /**
     * 更新角色
     */
    RoleBO update(String id, RoleUpdateCmd cmd);

    /**
     * 删除角色
     */
    void delete(String id);

    /**
     * 分配权限
     */
    void assignPermissions(String roleId, List<String> permissionIds);
}
