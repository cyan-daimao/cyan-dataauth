package com.cyan.dataauth.infra.persistence.role.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.dataauth.domain.role.AuthRole;
import com.cyan.dataauth.domain.role.repository.AuthRoleRepository;
import com.cyan.dataauth.infra.persistence.permission.dos.AuthPermissionDO;
import com.cyan.dataauth.infra.persistence.permission.mappers.AuthPermissionMapper;
import com.cyan.dataauth.infra.persistence.role.convert.AuthRoleInfraConvert;
import com.cyan.dataauth.infra.persistence.role.dos.AuthRoleDO;
import com.cyan.dataauth.infra.persistence.role.mappers.AuthRoleMapper;
import com.cyan.dataauth.infra.persistence.rolepermission.dos.AuthRolePermissionDO;
import com.cyan.dataauth.infra.persistence.rolepermission.mappers.AuthRolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色仓储实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class AuthRoleRepositoryImpl implements AuthRoleRepository {

    private final AuthRoleMapper authRoleMapper;
    private final AuthRolePermissionMapper authRolePermissionMapper;
    private final AuthRoleInfraConvert authRoleInfraConvert;
    private final AuthPermissionMapper authPermissionMapper;

    @Override
    public AuthRole getById(String id) {
        AuthRoleDO roleDO = authRoleMapper.selectById(Long.valueOf(id));
        return authRoleInfraConvert.toAuthRole(roleDO);
    }

    @Override
    public AuthRole getByCode(String code) {
        LambdaQueryWrapper<AuthRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthRoleDO::getCode, code);
        AuthRoleDO roleDO = authRoleMapper.selectOne(wrapper);
        return authRoleInfraConvert.toAuthRole(roleDO);
    }

    @Override
    public List<AuthRole> list() {
        List<AuthRoleDO> roleDOs = authRoleMapper.selectList(null);
        return authRoleInfraConvert.toAuthRoleList(roleDOs);
    }

    @Override
    public AuthRole save(AuthRole role) {
        AuthRoleDO roleDO = authRoleInfraConvert.toAuthRoleDO(role);
        authRoleMapper.insert(roleDO);
        return getById(String.valueOf(roleDO.getId()));
    }

    @Override
    public AuthRole update(AuthRole role) {
        AuthRoleDO roleDO = authRoleInfraConvert.toAuthRoleDO(role);
        roleDO.setUpdatedAt(LocalDateTime.now());
        authRoleMapper.updateById(roleDO);
        return getById(role.getId());
    }

    @Override
    public void delete(String id) {
        authRoleMapper.deleteById(Long.valueOf(id));
    }

    @Override
    public void deleteRolePermissions(String roleId) {
        LambdaQueryWrapper<AuthRolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthRolePermissionDO::getRoleId, Long.valueOf(roleId));
        authRolePermissionMapper.delete(wrapper);
    }

    @Override
    public void assignPermissions(String roleId, List<String> permissionIds) {
        deleteRolePermissions(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (String permissionId : permissionIds) {
                AuthRolePermissionDO rp = new AuthRolePermissionDO();
                rp.setRoleId(Long.valueOf(roleId));
                rp.setPermissionId(Long.valueOf(permissionId));
                rp.setCreatedAt(LocalDateTime.now());
                authRolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    public void addPermission(String roleId, String permissionId) {
        // 查询是否已存在
        LambdaQueryWrapper<AuthRolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthRolePermissionDO::getRoleId, Long.valueOf(roleId));
        wrapper.eq(AuthRolePermissionDO::getPermissionId, Long.valueOf(permissionId));
        Long count = authRolePermissionMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return;
        }
        AuthRolePermissionDO rp = new AuthRolePermissionDO();
        rp.setRoleId(Long.valueOf(roleId));
        rp.setPermissionId(Long.valueOf(permissionId));
        rp.setCreatedAt(LocalDateTime.now());
        authRolePermissionMapper.insert(rp);
    }

    @Override
    public String findPermissionIdByResource(String resourceType, String resourceId, String action) {
        LambdaQueryWrapper<AuthPermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthPermissionDO::getResourceType, resourceType);
        wrapper.eq(AuthPermissionDO::getResourceId, resourceId);
        wrapper.eq(AuthPermissionDO::getAction, action);
        AuthPermissionDO perm = authPermissionMapper.selectOne(wrapper);
        return perm != null ? String.valueOf(perm.getId()) : null;
    }

    @Override
    public List<String> selectPermissionIdsByRoleIdAndResourceType(Long roleId, String resourceType) {
        List<Long> permissionIds = authRolePermissionMapper.selectPermissionIdsByRoleIdAndResourceType(roleId, resourceType);
        return permissionIds.stream().map(String::valueOf).toList();
    }

    @Override
    public void deleteRolePermissionsByResourceType(String roleId, String resourceType) {
        authRolePermissionMapper.deleteByRoleIdAndResourceType(Long.valueOf(roleId), resourceType);
    }

    @Override
    public List<String> selectFunctionPermissionKeysByRoleId(String roleId, String resourceType) {
        List<AuthPermissionDO> permissions = authPermissionMapper.selectByRoleId(Long.valueOf(roleId));
        return permissions.stream()
                .filter(p -> resourceType.equals(p.getResourceType()))
                .map(p -> p.getResourceType() + ":" + p.getResourceId() + ":" + p.getAction())
                .toList();
    }
}
