package com.cyan.dataauth.domain.permission;

import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 权限
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthPermission {

    /**
     * 主键id
     */
    private String id;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 资源标识
     */
    private String resourceId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 删除时间（逻辑删除）
     */
    private LocalDateTime deletedAt;

    /**
     * 保存权限
     */
    public AuthPermission save(AuthPermissionRepository repository) {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        return repository.save(this);
    }

    /**
     * 更新权限
     */
    public AuthPermission update(AuthPermissionRepository repository) {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        return repository.update(this);
    }

    /**
     * 删除权限
     */
    public void delete(AuthPermissionRepository repository) {
        repository.delete(this.id);
    }
}
