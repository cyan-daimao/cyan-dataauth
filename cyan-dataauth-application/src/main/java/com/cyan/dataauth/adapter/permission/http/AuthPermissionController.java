package com.cyan.dataauth.adapter.permission.http;

import com.cyan.dataauth.adapter.permission.convert.AuthPermissionAdapterConvert;
import com.cyan.dataauth.application.permission.AuthPermissionService;
import com.cyan.dataauth.application.permission.bo.PermissionBO;
import com.cyan.dataauth.dto.PermissionDTO;
import com.cyan.arch.common.api.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthPermissionController {

    private final AuthPermissionService authPermissionService;
    private final AuthPermissionAdapterConvert authPermissionAdapterConvert;

    /**
     * 查询用户权限
     */
    @GetMapping("/users/{passport}/permissions")
    public Response<List<PermissionDTO>> listUserPermissions(@PathVariable String passport) {
        List<PermissionBO> list = authPermissionService.listByPassport(passport);
        return Response.success(authPermissionAdapterConvert.toPermissionDTOList(list));
    }
}
