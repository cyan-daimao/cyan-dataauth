package com.cyan.dataauth.client;

import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.cmd.MetricCheckCmd;
import com.cyan.dataauth.cmd.MetricFilterSqlCmd;
import com.cyan.dataauth.dto.MetricCheckResult;
import com.cyan.dataauth.dto.MetricFilterSqlResult;
import com.cyan.dataauth.dto.MetricResourceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 指标平台权限 RPC 客户端（服务间内部调用）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@FeignClient(name = "cyan-dataauth", contextId = "authMetricClient", path = "/rpc/v1/auth/metric", url = "${feign.cyan-dataauth.url:}")
public interface AuthMetricClient {

    /**
     * 指标权限校验
     */
    @PostMapping("/check")
    Response<MetricCheckResult> metricCheck(@RequestBody MetricCheckCmd cmd);

    /**
     * 查询指标资源列表
     */
    @GetMapping("/list")
    Response<List<MetricResourceDTO>> listMetrics(@RequestParam("passport") String passport,
                                                   @RequestParam("resourceType") String resourceType,
                                                   @RequestParam(value = "subjectCode", required = false) String subjectCode,
                                                   @RequestParam(value = "action", required = false) String action);

    /**
     * 指标SQL过滤
     */
    @PostMapping("/filter/sql")
    Response<MetricFilterSqlResult> metricFilterSql(@RequestBody MetricFilterSqlCmd cmd);
}
