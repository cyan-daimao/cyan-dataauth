package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 功能权限树节点DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionPermissionNodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private String name;
    private String type;
    private List<FunctionPermissionNodeDTO> children;
}
