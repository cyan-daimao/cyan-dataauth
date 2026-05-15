package com.cyan.dataauth.adapter.role.http;

import com.cyan.dataauth.adapter.role.convert.AuthRoleAdapterConvert;
import com.cyan.dataauth.application.role.AuthRoleService;
import com.cyan.dataauth.application.role.bo.RoleBO;
import com.cyan.dataauth.application.rolemember.AuthRoleMemberService;
import com.cyan.dataauth.cmd.AddRoleMembersCmd;
import com.cyan.dataauth.cmd.AssignPermissionsCmd;
import com.cyan.dataauth.cmd.RoleCreateCmd;
import com.cyan.dataauth.cmd.RoleUpdateCmd;
import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.dto.RoleDTO;
import com.cyan.dataauth.dto.RoleMemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth/roles")
@RequiredArgsConstructor
public class AuthRoleController {

    private final AuthRoleService authRoleService;
    private final AuthRoleMemberService authRoleMemberService;
    private final AuthRoleAdapterConvert authRoleAdapterConvert;

    /**
     * 列表查询
     */
    @GetMapping
    public Response<List<RoleDTO>> list() {
        List<RoleBO> list = authRoleService.list();
        return Response.success(authRoleAdapterConvert.toRoleDTOList(list));
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Response<RoleDTO> create(@RequestBody RoleCreateCmd cmd) {
        RoleBO role = authRoleService.create(cmd);
        return Response.success(authRoleAdapterConvert.toRoleDTO(role));
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Response<RoleDTO> update(@PathVariable String id, @RequestBody RoleUpdateCmd cmd) {
        RoleBO role = authRoleService.update(id, cmd);
        return Response.success(authRoleAdapterConvert.toRoleDTO(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable String id) {
        authRoleService.delete(id);
        return Response.success(null);
    }

    /**
     * 分配权限
     */
    @PostMapping("/{id}/permissions")
    public Response<Void> assignPermissions(@PathVariable String id, @RequestBody AssignPermissionsCmd cmd) {
        List<String> permissionIds = cmd.getPermissionIds() != null
                ? cmd.getPermissionIds().stream().map(String::valueOf).toList()
                : List.of();
        authRoleService.assignPermissions(id, permissionIds);
        return Response.success(null);
    }

    /**
     * 查询角色成员列表
     */
    @GetMapping("/{roleId}/members")
    // API: ready
    public Response<List<RoleMemberDTO>> listMembers(@PathVariable String roleId) {
        List<RoleMemberDTO> members = authRoleMemberService.listMembers(roleId);
        return Response.success(members);
    }

    /**
     * 批量添加角色成员
     */
    @PostMapping("/{roleId}/members")
    // API: ready
    public Response<Void> addMembers(@PathVariable String roleId, @RequestBody AddRoleMembersCmd cmd) {
        authRoleMemberService.addMembers(roleId, cmd.getPassports());
        return Response.success(null);
    }

    /**
     * 移除角色成员
     */
    @DeleteMapping("/{roleId}/members/{passport}")
    // API: ready
    public Response<Void> removeMember(@PathVariable String roleId, @PathVariable String passport) {
        authRoleMemberService.removeMember(roleId, passport);
        return Response.success(null);
    }
}
