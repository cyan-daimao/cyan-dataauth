package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 指标权限配置DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricPermissionConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String metricId;
    private String metricCode;
    private String metricName;
    private String subjectCode;
    private String subjectName;
    private String status;
    private String visibility;
    private List<String> allowedRoles;
}
