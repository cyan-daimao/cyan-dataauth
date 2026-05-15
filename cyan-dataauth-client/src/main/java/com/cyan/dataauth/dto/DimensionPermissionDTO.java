package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 维度权限配置DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionPermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dimensionId;
    private String dimensionCode;
    private String dimensionName;
    private String category;
    private String relatedField;
    private List<String> actions;
    private Boolean allowValuesQuery;
    private List<PermissionTargetDTO> targets;
}
