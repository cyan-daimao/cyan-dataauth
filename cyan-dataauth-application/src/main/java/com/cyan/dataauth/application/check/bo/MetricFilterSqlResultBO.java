package com.cyan.dataauth.application.check.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指标SQL过滤结果业务对象
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricFilterSqlResultBO {

    /**
     * 是否通过
     */
    private boolean permitted;

    /**
     * 原因
     */
    private String reason;

    /**
     * 原始SQL
     */
    private String originalSql;

    /**
     * 改写后SQL
     */
    private String rewrittenSql;

    /**
     * 被拦截的资源
     */
    private String blockedResource;
}
