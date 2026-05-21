package com.cyan.dataauth.infra.persistence.userrole.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.dataauth.domain.userrole.repository.AuthUserRoleRepository;
import com.cyan.dataauth.infra.persistence.userrole.dos.AuthUserRoleDO;
import com.cyan.dataauth.infra.persistence.userrole.mappers.AuthUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联仓储实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class AuthUserRoleRepositoryImpl implements AuthUserRoleRepository {

    private final AuthUserRoleMapper authUserRoleMapper;

    @Override
    public List<Long> listRoleIdsByPassport(String passport) {
        return authUserRoleMapper.selectRoleIdsByPassport(passport);
    }

    @Override
    public List<String> listPassportsByRoleId(Long roleId) {
        return authUserRoleMapper.selectPassportsByRoleId(roleId);
    }

    @Override
    public List<AuthUserRoleEntry> listAll() {
        List<AuthUserRoleDO> list = authUserRoleMapper.selectList(null);
        if (list == null) {
            return List.of();
        }
        return list.stream()
                .map(do_ -> new AuthUserRoleEntry(do_.getPassport(), do_.getRoleId()))
                .collect(Collectors.toList());
    }

    @Override
    public long countByRoleIdAndPassport(Long roleId, String passport) {
        LambdaQueryWrapper<AuthUserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRoleDO::getPassport, passport);
        wrapper.eq(AuthUserRoleDO::getRoleId, roleId);
        Long count = authUserRoleMapper.selectCount(wrapper);
        return count != null ? count : 0;
    }

    @Override
    public void save(String passport, Long roleId) {
        AuthUserRoleDO ur = new AuthUserRoleDO();
        ur.setPassport(passport);
        ur.setRoleId(roleId);
        ur.setCreatedAt(LocalDateTime.now());
        authUserRoleMapper.insert(ur);
    }

    @Override
    public void removeByRoleIdAndPassport(Long roleId, String passport) {
        LambdaQueryWrapper<AuthUserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRoleDO::getRoleId, roleId);
        wrapper.eq(AuthUserRoleDO::getPassport, passport);
        authUserRoleMapper.delete(wrapper);
    }

    @Override
    public void removeByPassport(String passport) {
        LambdaQueryWrapper<AuthUserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRoleDO::getPassport, passport);
        authUserRoleMapper.delete(wrapper);
    }
}
