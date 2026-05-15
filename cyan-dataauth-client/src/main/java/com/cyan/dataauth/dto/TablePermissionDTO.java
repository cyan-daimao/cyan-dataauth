package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 表权限DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TablePermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tableName;
    private String action;
    private String rowFilter;
    private String columnMasks;
}
