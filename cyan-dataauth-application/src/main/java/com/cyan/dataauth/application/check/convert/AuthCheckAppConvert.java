package com.cyan.dataauth.application.check.convert;

import com.cyan.dataauth.application.check.bo.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 权限校验应用层转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthCheckAppConvert {

    public AuthCheckResultBO toAuthCheckResultBO(boolean permitted, String reason) {
        return new AuthCheckResultBO(permitted, reason);
    }

    public FilterSqlResultBO toFilterSqlResultBO(boolean permitted, String reason, String originalSql, String rewrittenSql) {
        return new FilterSqlResultBO(permitted, reason, originalSql, rewrittenSql, null, null);
    }

    public MetricCheckResultBO toMetricCheckResultBO(boolean allPermitted, List<MetricCheckItemResultBO> results) {
        return new MetricCheckResultBO(allPermitted, results);
    }

    public MetricCheckItemResultBO toMetricCheckItemResultBO(String resourceType, String resourceId, boolean permitted, String reason) {
        return new MetricCheckItemResultBO(resourceType, resourceId, permitted, reason);
    }

    public MetricResourceBO toMetricResourceBO(String id, String code, String name, String subjectCode, String subjectName) {
        return new MetricResourceBO(id, code, name, subjectCode, subjectName);
    }

    public List<MetricResourceBO> toMetricResourceBOList(List<MetricResourceBO> list) {
        return Optional.ofNullable(list).orElse(List.of());
    }

    public MetricFilterSqlResultBO toMetricFilterSqlResultBO(boolean permitted, String reason, String originalSql, String rewrittenSql) {
        return new MetricFilterSqlResultBO(permitted, reason, originalSql, rewrittenSql, null);
    }
}
