package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库权限DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabasePermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String databaseName;
    private List<TablePermissionDTO> tables;
}
