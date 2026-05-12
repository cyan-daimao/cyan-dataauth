package com.cyan.dataauth.application.permission;

import com.cyan.dataauth.application.permission.bo.PermissionBO;

import java.util.List;

/**
 * 权限应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthPermissionService {

    /**
     * 查询用户权限列表
     */
    List<PermissionBO> listByPassport(String passport);
}
