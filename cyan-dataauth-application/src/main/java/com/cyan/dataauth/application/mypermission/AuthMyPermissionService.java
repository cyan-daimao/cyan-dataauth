package com.cyan.dataauth.application.mypermission;

import com.cyan.dataauth.dto.FunctionPermissionNodeDTO;
import com.cyan.dataauth.dto.MyPermissionDTO;

import java.util.List;

/**
 * 我的权限应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthMyPermissionService {

    /**
     * 获取我的权限聚合
     */
    MyPermissionDTO getMyPermissions(String passport);

    /**
     * 获取功能权限树
     */
    List<FunctionPermissionNodeDTO> getFunctionPermissionTree(String passport);
}
