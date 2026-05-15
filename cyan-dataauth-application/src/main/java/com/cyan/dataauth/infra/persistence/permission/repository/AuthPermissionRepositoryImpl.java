package com.cyan.dataauth.infra.persistence.permission.repository;

import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import com.cyan.dataauth.infra.persistence.permission.convert.AuthPermissionInfraConvert;
import com.cyan.dataauth.infra.persistence.permission.dos.AuthPermissionDO;
import com.cyan.dataauth.infra.persistence.permission.mappers.AuthPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public AuthPermission save(AuthPermission permission) {
        AuthPermissionDO permissionDO = authPermissionInfraConvert.toAuthPermissionDO(permission);
        authPermissionMapper.insert(permissionDO);
        return getById(String.valueOf(permissionDO.getId()));
    }
}
