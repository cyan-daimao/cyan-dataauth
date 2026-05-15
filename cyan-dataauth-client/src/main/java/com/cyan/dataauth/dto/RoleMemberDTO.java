package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色成员DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMemberDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String passport;
    private String cnName;
    private String deptName;
    private String jobTitle;
}
