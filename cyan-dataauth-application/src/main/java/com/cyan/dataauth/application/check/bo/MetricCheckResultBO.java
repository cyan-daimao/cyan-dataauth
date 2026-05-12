package com.cyan.dataauth.application.check.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 指标权限校验结果业务对象
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricCheckResultBO {

    /**
     * 是否全部通过
     */
    private boolean allPermitted;

    /**
     * 明细结果
     */
    private List<MetricCheckItemResultBO> results;
}
