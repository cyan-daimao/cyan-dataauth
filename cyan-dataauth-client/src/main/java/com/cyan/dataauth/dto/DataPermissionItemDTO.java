package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据权限项DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPermissionItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String resourceType;
    private String resourceId;
    private String action;
}
