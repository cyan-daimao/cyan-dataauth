package com.cyan.dataauth.adapter.metric.rpc;

import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.adapter.check.convert.AuthCheckAdapterConvert;
import com.cyan.dataauth.application.check.AuthCheckService;
import com.cyan.dataauth.application.check.bo.*;
import com.cyan.dataauth.client.AuthMetricClient;
import com.cyan.dataauth.cmd.MetricCheckCmd;
import com.cyan.dataauth.cmd.MetricFilterSqlCmd;
import com.cyan.dataauth.dto.MetricCheckResult;
import com.cyan.dataauth.dto.MetricFilterSqlResult;
import com.cyan.dataauth.dto.MetricResourceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 指标平台权限 RPC 服务（供内部服务调用）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/rpc/v1/auth/metric")
@RequiredArgsConstructor
public class AuthMetricRpc implements AuthMetricClient {

    private final AuthCheckService authCheckService;
    private final AuthCheckAdapterConvert authCheckAdapterConvert;

    @Override
    public Response<MetricCheckResult> metricCheck(MetricCheckCmd cmd) {
        MetricCheckResultBO bo = authCheckService.metricCheck(cmd);
        return Response.success(authCheckAdapterConvert.toMetricCheckResult(bo));
    }

    @Override
    public Response<List<MetricResourceDTO>> listMetrics(String passport, String resourceType,
                                                          String subjectCode, String action) {
        List<MetricResourceBO> bos = authCheckService.listMetricResources(passport, resourceType, subjectCode, action);
        return Response.success(authCheckAdapterConvert.toMetricResourceDTOList(bos));
    }

    @Override
    public Response<MetricFilterSqlResult> metricFilterSql(MetricFilterSqlCmd cmd) {
        MetricFilterSqlResultBO bo = authCheckService.metricFilterSql(cmd);
        return Response.success(authCheckAdapterConvert.toMetricFilterSqlResult(bo));
    }
}
