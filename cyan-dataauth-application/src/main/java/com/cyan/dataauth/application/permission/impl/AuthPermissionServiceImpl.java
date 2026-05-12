package com.cyan.dataauth.application.permission.impl;

import com.cyan.dataauth.application.permission.AuthPermissionService;
import com.cyan.dataauth.application.permission.bo.PermissionBO;
import com.cyan.dataauth.application.permission.convert.AuthPermissionAppConvert;
import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthPermissionServiceImpl implements AuthPermissionService {

    private final AuthPermissionRepository authPermissionRepository;
    private final AuthPermissionAppConvert authPermissionAppConvert;

    @Override
    public List<PermissionBO> listByPassport(String passport) {
        List<AuthPermission> permissions = authPermissionRepository.selectByPassport(passport);
        return authPermissionAppConvert.toPermissionBOList(permissions);
    }
}
