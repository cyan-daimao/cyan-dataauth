package com.cyan.dataauth.application.userpermission;

import com.cyan.dataauth.dto.PageResult;
import com.cyan.dataauth.dto.UserPermissionDTO;

import java.util.List;

/**
 * 用户权限应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthUserPermissionService {

    /**
     * 分页查询用户权限聚合
     */
    PageResult<UserPermissionDTO> listUserPermissions(int pageNum, int pageSize, String keyword);

    /**
     * 为用户分配角色
     */
    void assignUserRoles(String passport, List<String> roleIds);
}
