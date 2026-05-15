package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 功能权限模块DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionPermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String moduleName;
    private List<String> permissions;
}
