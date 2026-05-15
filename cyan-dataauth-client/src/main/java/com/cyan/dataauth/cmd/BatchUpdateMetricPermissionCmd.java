package com.cyan.dataauth.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量更新指标权限配置命令
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateMetricPermissionCmd {

    private List<String> metricIds;
    private String visibility;
    private List<String> allowedRoles;
}
