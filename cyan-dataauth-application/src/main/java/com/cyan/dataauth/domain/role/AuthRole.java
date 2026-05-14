package com.cyan.dataauth.domain.role;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.dataauth.domain.role.repository.AuthRoleRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthRole {

    /**
     * 主键id
     */
    private String id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 最高可访问密级: L1/L2/L3/L4
     */
    private String maxSecurityLevel;

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
     * 创建角色
     */
    public AuthRole create(AuthRoleRepository roleRepository) {
        Assert.notBlank(name, new SilentException("角色名称不能为空"));
        Assert.notBlank(code, new SilentException("角色编码不能为空"));
        AuthRole exist = roleRepository.getByCode(code);
        Assert.isNull(exist, new SilentException("角色编码已存在: " + code));
        if (status == null) {
            status = 1;
        }
        if (maxSecurityLevel == null || maxSecurityLevel.isBlank()) {
            maxSecurityLevel = "L1";
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        return roleRepository.save(this);
    }

    /**
     * 更新角色
     */
    public AuthRole update(AuthRoleRepository roleRepository) {
        Assert.notBlank(id, new SilentException("角色ID不能为空"));
        Assert.notBlank(name, new SilentException("角色名称不能为空"));
        AuthRole exist = roleRepository.getById(id);
        Assert.notNull(exist, new SilentException("角色不存在: " + id));
        if (code != null && !code.equals(exist.code)) {
            AuthRole codeExist = roleRepository.getByCode(code);
            Assert.isNull(codeExist, new SilentException("角色编码已存在: " + code));
        }
        if (maxSecurityLevel == null || maxSecurityLevel.isBlank()) {
            maxSecurityLevel = "L1";
        }
        updatedAt = LocalDateTime.now();
        return roleRepository.update(this);
    }

    /**
     * 删除角色
     */
    public void delete(AuthRoleRepository roleRepository) {
        Assert.notBlank(id, new SilentException("角色ID不能为空"));
        AuthRole exist = roleRepository.getById(id);
        Assert.notNull(exist, new SilentException("角色不存在: " + id));
        roleRepository.delete(id);
        roleRepository.deleteRolePermissions(id);
    }

    /**
     * 分配权限
     */
    public void assignPermissions(List<String> permissionIds, AuthRoleRepository roleRepository) {
        Assert.notBlank(id, new SilentException("角色ID不能为空"));
        AuthRole exist = roleRepository.getById(id);
        Assert.notNull(exist, new SilentException("角色不存在: " + id));
        roleRepository.assignPermissions(id, permissionIds);
    }
}
