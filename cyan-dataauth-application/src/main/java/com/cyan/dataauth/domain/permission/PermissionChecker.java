package com.cyan.dataauth.domain.permission;

import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限校验器（领域服务）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class PermissionChecker {

    private final AuthPermissionRepository authPermissionRepository;

    /**
     * 校验用户是否有指定权限
     */
    public boolean hasPermission(String passport, String resourceType, String resourceId, String action) {
        if (resourceId == null || action == null) {
            return false;
        }
        List<AuthPermission> permissions = authPermissionRepository.selectByPassport(passport);
        for (AuthPermission p : permissions) {
            if (resourceType.equals(p.getResourceType())) {
                if (resourceId.equals(p.getResourceId()) || "*".equals(p.getResourceId())) {
                    if (action.equals(p.getAction()) || "ALL".equals(p.getAction())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
