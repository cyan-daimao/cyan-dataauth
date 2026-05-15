package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 指标权限分组DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricPermissionGroupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String subjectName;
    private List<String> metrics;
    private List<String> dimensions;
}
