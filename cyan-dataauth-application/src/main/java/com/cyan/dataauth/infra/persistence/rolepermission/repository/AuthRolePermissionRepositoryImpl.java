package com.cyan.dataauth.infra.persistence.rolepermission.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.dataauth.domain.rolepermission.repository.AuthRolePermissionRepository;
import com.cyan.dataauth.infra.persistence.rolepermission.dos.AuthRolePermissionDO;
import com.cyan.dataauth.infra.persistence.rolepermission.mappers.AuthRolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色权限关联仓储实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class AuthRolePermissionRepositoryImpl implements AuthRolePermissionRepository {

    private final AuthRolePermissionMapper authRolePermissionMapper;

    @Override
    public List<Long> selectPermissionIdsByRoleId(Long roleId) {
        return authRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    public Map<Long, List<Long>> getRoleIdsGroupedByPermissionId() {
        List<AuthRolePermissionDO> list = authRolePermissionMapper.selectList(null);
        if (list == null) {
            return Map.of();
        }
        return list.stream()
                .collect(Collectors.groupingBy(
                        AuthRolePermissionDO::getPermissionId,
                        Collectors.mapping(AuthRolePermissionDO::getRoleId, Collectors.toList())
                ));
    }

    @Override
    public Map<Long, List<Long>> getPermissionIdsGroupedByRoleId() {
        List<AuthRolePermissionDO> list = authRolePermissionMapper.selectList(null);
        if (list == null) {
            return Map.of();
        }
        return list.stream()
                .collect(Collectors.groupingBy(
                        AuthRolePermissionDO::getRoleId,
                        Collectors.mapping(AuthRolePermissionDO::getPermissionId, Collectors.toList())
                ));
    }

    @Override
    public void deleteByPermissionId(Long permissionId) {
        LambdaQueryWrapper<AuthRolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthRolePermissionDO::getPermissionId, permissionId);
        authRolePermissionMapper.delete(wrapper);
    }

    @Override
    public void save(Long roleId, Long permissionId) {
        AuthRolePermissionDO rp = new AuthRolePermissionDO();
        rp.setRoleId(roleId);
        rp.setPermissionId(permissionId);
        rp.setCreatedAt(LocalDateTime.now());
        authRolePermissionMapper.insert(rp);
    }
}
