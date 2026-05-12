package com.cyan.dataauth.application.check.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限校验结果业务对象
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthCheckResultBO {

    /**
     * 是否有权限
     */
    private boolean permitted;

    /**
     * 原因
     */
    private String reason;
}
