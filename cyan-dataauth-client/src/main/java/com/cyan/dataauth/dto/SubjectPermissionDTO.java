package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 主题域权限配置DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectPermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String subjectCode;
    private String subjectName;
    private List<String> actions;
    private List<PermissionTargetDTO> targets;
}
