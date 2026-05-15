package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户权限聚合DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String passport;
    private String cnName;
    private List<RoleDTO> roles;
    private List<PermissionDTO> directPermissions;
    private List<DataPermissionItemDTO> dataPermissions;
    private List<MetricPermissionItemDTO> metricPermissions;
}
