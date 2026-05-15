package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 我的权限聚合DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<FunctionPermissionDTO> functionPermissions;
    private List<DataPermissionDTO> dataPermissions;
    private List<MetricPermissionGroupDTO> metricPermissions;
    private List<PendingApprovalDTO> pendingApprovals;
}
