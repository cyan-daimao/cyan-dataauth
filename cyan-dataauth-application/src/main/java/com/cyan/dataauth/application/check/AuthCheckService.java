package com.cyan.dataauth.application.check;

import com.cyan.dataauth.application.check.bo.*;
import com.cyan.dataauth.cmd.*;

import java.util.List;

/**
 * 权限校验应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthCheckService {

    /**
     * 元数据权限校验
     */
    AuthCheckResultBO check(AuthCheckCmd cmd);

    /**
     * SQL过滤
     */
    FilterSqlResultBO filterSql(FilterSqlCmd cmd);

    /**
     * 查询用户资源树
     */
    List<ResourceTreeNodeBO> listResources(String passport, String resourceType);

    /**
     * 指标权限校验
     */
    MetricCheckResultBO metricCheck(MetricCheckCmd cmd);

    /**
     * 查询指标资源列表
     */
    List<MetricResourceBO> listMetricResources(String passport, String resourceType, String subjectCode, String action);

    /**
     * 指标SQL过滤
     */
    MetricFilterSqlResultBO metricFilterSql(MetricFilterSqlCmd cmd);

    /**
     * 获取用户最高可访问密级
     */
    String getUserMaxSecurityLevel(String passport);
}
