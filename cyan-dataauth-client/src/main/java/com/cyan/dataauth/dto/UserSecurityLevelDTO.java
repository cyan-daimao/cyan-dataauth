package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户密级信息
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSecurityLevelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String passport;

    /**
     * 最高可访问密级: L1/L2/L3/L4
     */
    private String maxSecurityLevel;
}
