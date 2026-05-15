package com.cyan.dataauth.application.rolemember;

import com.cyan.dataauth.dto.RoleMemberDTO;

import java.util.List;

/**
 * 角色成员应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthRoleMemberService {

    /**
     * 查询角色成员列表
     */
    List<RoleMemberDTO> listMembers(String roleId);

    /**
     * 批量添加角色成员
     */
    void addMembers(String roleId, List<String> passports);

    /**
     * 移除角色成员
     */
    void removeMember(String roleId, String passport);
}
