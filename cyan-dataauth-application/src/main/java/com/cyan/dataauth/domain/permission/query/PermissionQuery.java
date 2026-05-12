package com.cyan.dataauth.domain.permission.query;

/**
 * 权限查询
 *
 * @author cy.Y
 * @since 1.0.0
 */
public record PermissionQuery(
        // 用户护照
        String passport,
        // 资源类型
        String resourceType,
        // 资源标识
        String resourceId,
        // 操作类型
        String action
) {
}
