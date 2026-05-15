package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 指标权限项DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricPermissionItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String permissionType;
    private String resourceId;
    private List<String> actions;
}
