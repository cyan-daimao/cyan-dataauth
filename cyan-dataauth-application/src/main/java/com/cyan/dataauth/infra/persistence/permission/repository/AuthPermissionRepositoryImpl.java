package com.cyan.dataauth.infra.persistence.permission.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import com.cyan.dataauth.infra.persistence.permission.convert.AuthPermissionInfraConvert;
import com.cyan.dataauth.infra.persistence.permission.dos.AuthPermissionDO;
import com.cyan.dataauth.infra.persistence.permission.mappers.AuthPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限仓储实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class AuthPermissionRepositoryImpl implements AuthPermissionRepository {

    private final AuthPermissionMapper authPermissionMapper;
    private final AuthPermissionInfraConvert authPermissionInfraConvert;

    @Override
    public AuthPermission getById(String id) {
        AuthPermissionDO permissionDO = authPermissionMapper.selectById(Long.valueOf(id));
        return authPermissionInfraConvert.toAuthPermission(permissionDO);
    }

    @Override
    public List<AuthPermission> list() {
        List<AuthPermissionDO> permissionDOs = authPermissionMapper.selectList(null);
        return authPermissionInfraConvert.toAuthPermissionList(permissionDOs);
    }

    @Override
    public List<AuthPermission> selectByPassport(String passport) {
        List<AuthPermissionDO> permissionDOs = authPermissionMapper.selectByPassport(passport);
        return authPermissionInfraConvert.toAuthPermissionList(permissionDOs);
    }

    @Override
    public AuthPermission getByResource(String resourceType, String resourceId, String action) {
        AuthPermissionDO permissionDO = authPermissionMapper.selectByResource(resourceType, resourceId, action);
        return authPermissionInfraConvert.toAuthPermission(permissionDO);
    }

    @Override
    public List<AuthPermission> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<AuthPermissionDO> permissionDOs = authPermissionMapper.selectBatchIds(ids);
        return authPermissionInfraConvert.toAuthPermissionList(permissionDOs);
    }

    @Override
    public List<AuthPermission> listByResourceType(String resourceType) {
        List<AuthPermissionDO> permissionDOs = authPermissionMapper.selectByResourceType(resourceType);
        return authPermissionInfraConvert.toAuthPermissionList(permissionDOs);
    }

    @Override
    public List<AuthPermission> listByResourceTypeAndResourceId(String resourceType, String resourceId) {
        LambdaQueryWrapper<AuthPermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthPermissionDO::getResourceType, resourceType);
        wrapper.eq(AuthPermissionDO::getResourceId, resourceId);
        List<AuthPermissionDO> permissionDOs = authPermissionMapper.selectList(wrapper);
        return authPermissionInfraConvert.toAuthPermissionList(permissionDOs);
    }

    @Override
    public AuthPermission save(AuthPermission permission) {
        AuthPermissionDO permissionDO = authPermissionInfraConvert.toAuthPermissionDO(permission);
        authPermissionMapper.insert(permissionDO);
        return getById(String.valueOf(permissionDO.getId()));
    }

    @Override
    public AuthPermission update(AuthPermission permission) {
        AuthPermissionDO permissionDO = authPermissionInfraConvert.toAuthPermissionDO(permission);
        permissionDO.setUpdatedAt(LocalDateTime.now());
        authPermissionMapper.updateById(permissionDO);
        return getById(permission.getId());
    }

    @Override
    public void delete(String id) {
        authPermissionMapper.deleteById(Long.valueOf(id));
    }
}
