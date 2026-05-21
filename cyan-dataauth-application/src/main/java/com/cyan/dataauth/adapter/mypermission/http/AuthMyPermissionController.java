package com.cyan.dataauth.adapter.mypermission.http;

import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.application.mypermission.AuthMyPermissionService;
import com.cyan.dataauth.dto.FunctionPermissionNodeDTO;
import com.cyan.dataauth.dto.MyPermissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 我的权限控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthMyPermissionController {

    private final AuthMyPermissionService authMyPermissionService;

    /**
     * 我的权限聚合
     */
    @GetMapping("/my/permissions")
    public Response<MyPermissionDTO> getMyPermissions(@RequestParam String passport) {
        return Response.success(authMyPermissionService.getMyPermissions(passport));
    }

    /**
     * 功能权限树
     */
    @GetMapping("/function-permissions/tree")
    public Response<List<FunctionPermissionNodeDTO>> getFunctionPermissionTree(@RequestParam(required = false) String passport) {
        return Response.success(authMyPermissionService.getFunctionPermissionTree(passport));
    }
}
