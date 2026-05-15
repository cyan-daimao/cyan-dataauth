package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 权限授权目标DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionTargetDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String targetType;
    private String targetId;
    private String targetName;
}
