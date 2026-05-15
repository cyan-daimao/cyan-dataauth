package com.cyan.dataauth.adapter.userpermission.http;

import com.cyan.dataauth.application.userpermission.AuthUserPermissionService;
import com.cyan.dataauth.cmd.AssignUserRolesCmd;
import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.dto.PageResult;
import com.cyan.dataauth.dto.UserPermissionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户权限控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth/users")
@RequiredArgsConstructor
public class AuthUserPermissionController {

    private final AuthUserPermissionService authUserPermissionService;

    /**
     * 分页查询所有有权限的用户
     */
    @GetMapping("/permissions")
    // API: ready
    public Response<PageResult<UserPermissionDTO>> listUserPermissions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        PageResult<UserPermissionDTO> result = authUserPermissionService.listUserPermissions(pageNum, pageSize, keyword);
        return Response.success(result);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/{passport}/roles")
    // API: ready
    public Response<Void> assignUserRoles(@PathVariable String passport, @RequestBody AssignUserRolesCmd cmd) {
        authUserPermissionService.assignUserRoles(passport, cmd.getRoleIds());
        return Response.success(null);
    }
}
