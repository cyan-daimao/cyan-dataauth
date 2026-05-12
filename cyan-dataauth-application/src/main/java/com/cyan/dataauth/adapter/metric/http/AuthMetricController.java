package com.cyan.dataauth.adapter.metric.http;

import com.cyan.dataauth.adapter.check.convert.AuthCheckAdapterConvert;
import com.cyan.dataauth.application.check.AuthCheckService;
import com.cyan.dataauth.application.check.bo.*;
import com.cyan.dataauth.cmd.MetricCheckCmd;
import com.cyan.dataauth.cmd.MetricFilterSqlCmd;
import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 指标权限控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth/metric")
@RequiredArgsConstructor
public class AuthMetricController {

    private final AuthCheckService authCheckService;
    private final AuthCheckAdapterConvert authCheckAdapterConvert;

    /**
     * 指标权限校验
     */
    @PostMapping("/check")
    public Response<MetricCheckResult> metricCheck(@RequestBody MetricCheckCmd cmd) {
        MetricCheckResultBO bo = authCheckService.metricCheck(cmd);
        return Response.success(authCheckAdapterConvert.toMetricCheckResult(bo));
    }

    /**
     * 查询指标资源列表
     */
    @GetMapping("/list")
    public Response<List<MetricResourceDTO>> listMetrics(@RequestParam String passport,
                                                         @RequestParam String resourceType,
                                                         @RequestParam(required = false) String subjectCode,
                                                         @RequestParam(required = false) String action) {
        List<MetricResourceBO> bos = authCheckService.listMetricResources(passport, resourceType, subjectCode, action);
        return Response.success(authCheckAdapterConvert.toMetricResourceDTOList(bos));
    }

    /**
     * 指标SQL过滤
     */
    @PostMapping("/filter/sql")
    public Response<MetricFilterSqlResult> metricFilterSql(@RequestBody MetricFilterSqlCmd cmd) {
        MetricFilterSqlResultBO bo = authCheckService.metricFilterSql(cmd);
        return Response.success(authCheckAdapterConvert.toMetricFilterSqlResult(bo));
    }
}
