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
     * 校验用户是否有指定权限（支持层级权限推导）
     */
    public boolean hasPermission(String passport, String resourceType, String resourceId, String action) {
        if (resourceId == null || action == null) {
            return false;
        }
        List<AuthPermission> permissions = authPermissionRepository.selectByPassport(passport);

        // 1. 精确匹配或通配符匹配
        for (AuthPermission p : permissions) {
            if (resourceType.equals(p.getResourceType())) {
                if (resourceId.equals(p.getResourceId()) || "*".equals(p.getResourceId())) {
                    if (action.equals(p.getAction()) || "ALL".equals(p.getAction())) {
                        return true;
                    }
                }
            }
        }

        // 2. 层级权限推导：TABLE -> DB -> DATASOURCE
        if ("TABLE".equals(resourceType)) {
            String[] parts = resourceId.split("\\.");
            if (parts.length >= 2) {
                String dbResourceId = parts[0] + "." + parts[1];
                if (hasPermissionInList(permissions, "DB", dbResourceId, action)) {
                    return true;
                }
                if (hasPermissionInList(permissions, "DATASOURCE", parts[0], action)) {
                    return true;
                }
            }
        }

        // 3. 跨类型通配：任意 resourceType 的 * 权限
        for (AuthPermission p : permissions) {
            if ("*".equals(p.getResourceType()) && "*".equals(p.getResourceId())) {
                if (action.equals(p.getAction()) || "ALL".equals(p.getAction())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasPermissionInList(List<AuthPermission> permissions, String resourceType, String resourceId, String action) {
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
