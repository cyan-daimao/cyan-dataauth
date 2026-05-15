package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 数据权限DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String datasourceName;
    private List<DatabasePermissionDTO> databases;
}
