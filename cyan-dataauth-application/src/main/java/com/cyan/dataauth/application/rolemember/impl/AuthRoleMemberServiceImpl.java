package com.cyan.dataauth.application.rolemember.impl;

import com.cyan.dataauth.application.rolemember.AuthRoleMemberService;
import com.cyan.dataauth.domain.userrole.repository.AuthUserRoleRepository;
import com.cyan.dataauth.dto.RoleMemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final AuthUserRoleRepository authUserRoleRepository;

    @Override
    public List<RoleMemberDTO> listMembers(String roleId) {
        List<String> passports = authUserRoleRepository.listPassportsByRoleId(Long.valueOf(roleId));
        if (passports == null) {
            return List.of();
        }
        return passports.stream().map(passport -> {
            RoleMemberDTO dto = new RoleMemberDTO();
            dto.setId(UUID.randomUUID().toString());
            dto.setPassport(passport);
            dto.setCnName(passport);
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
            long count = authUserRoleRepository.countByRoleIdAndPassport(rid, passport);
            if (count > 0) {
                continue;
            }
            authUserRoleRepository.save(passport, rid);
        }
    }

    @Override
    @Transactional
    public void removeMember(String roleId, String passport) {
        authUserRoleRepository.removeByRoleIdAndPassport(Long.valueOf(roleId), passport);
    }
}
