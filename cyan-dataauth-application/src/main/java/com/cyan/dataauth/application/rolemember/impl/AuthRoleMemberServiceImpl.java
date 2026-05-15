package com.cyan.dataauth.application.rolemember.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.dataauth.application.rolemember.AuthRoleMemberService;
import com.cyan.dataauth.dto.RoleMemberDTO;
import com.cyan.dataauth.infra.persistence.userrole.dos.AuthUserRoleDO;
import com.cyan.dataauth.infra.persistence.userrole.mappers.AuthUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 角色成员应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthRoleMemberServiceImpl implements AuthRoleMemberService {

    private final AuthUserRoleMapper authUserRoleMapper;

    @Override
    public List<RoleMemberDTO> listMembers(String roleId) {
        LambdaQueryWrapper<AuthUserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRoleDO::getRoleId, Long.valueOf(roleId));
        List<AuthUserRoleDO> list = authUserRoleMapper.selectList(wrapper);
        return list.stream().map(ur -> {
            RoleMemberDTO dto = new RoleMemberDTO();
            dto.setId(ur.getId() != null ? String.valueOf(ur.getId()) : UUID.randomUUID().toString());
            dto.setPassport(ur.getPassport());
            // dataauth 不存储员工信息，由前端另行查询
            dto.setCnName(ur.getPassport());
            dto.setDeptName("");
            dto.setJobTitle("");
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addMembers(String roleId, List<String> passports) {
        if (passports == null || passports.isEmpty()) {
            return;
        }
        Long rid = Long.valueOf(roleId);
        for (String passport : passports) {
            if (passport == null || passport.isBlank()) {
                continue;
            }
            LambdaQueryWrapper<AuthUserRoleDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AuthUserRoleDO::getPassport, passport);
            wrapper.eq(AuthUserRoleDO::getRoleId, rid);
            Long count = authUserRoleMapper.selectCount(wrapper);
            if (count != null && count > 0) {
                continue;
            }
            AuthUserRoleDO ur = new AuthUserRoleDO();
            ur.setPassport(passport);
            ur.setRoleId(rid);
            ur.setCreatedAt(LocalDateTime.now());
            authUserRoleMapper.insert(ur);
        }
    }

    @Override
    @Transactional
    public void removeMember(String roleId, String passport) {
        LambdaQueryWrapper<AuthUserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRoleDO::getRoleId, Long.valueOf(roleId));
        wrapper.eq(AuthUserRoleDO::getPassport, passport);
        authUserRoleMapper.delete(wrapper);
    }
}
