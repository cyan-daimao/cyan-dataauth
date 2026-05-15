package com.cyan.dataauth.adapter.metric.http;

import com.cyan.dataauth.adapter.check.convert.AuthCheckAdapterConvert;
import com.cyan.dataauth.application.check.AuthCheckService;
import com.cyan.dataauth.application.check.bo.*;
import com.cyan.dataauth.application.metricpermission.AuthMetricPermissionService;
import com.cyan.dataauth.cmd.BatchUpdateMetricPermissionCmd;
import com.cyan.dataauth.cmd.MetricCheckCmd;
import com.cyan.dataauth.cmd.MetricFilterSqlCmd;
import com.cyan.dataauth.cmd.SaveDimensionPermissionCmd;
import com.cyan.dataauth.cmd.SaveSubjectPermissionCmd;
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
    private final AuthMetricPermissionService authMetricPermissionService;

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

    /**
     * 查询主题域权限配置
     */
    @GetMapping("/subject-permissions")
    // API: ready
    public Response<List<SubjectPermissionDTO>> listSubjectPermissions() {
        List<SubjectPermissionDTO> list = authMetricPermissionService.listSubjectPermissions();
        return Response.success(list);
    }

    /**
     * 保存主题域权限配置
     */
    @PostMapping("/subject-permissions")
    // API: ready
    public Response<Void> saveSubjectPermission(@RequestBody SaveSubjectPermissionCmd cmd) {
        authMetricPermissionService.saveSubjectPermission(cmd.getSubjectCode(), cmd.getActions(), cmd.getTargets());
        return Response.success(null);
    }

    /**
     * 分页查询指标权限配置
     */
    @GetMapping("/metric-permissions")
    // API: ready
    public Response<PageResult<MetricPermissionConfigDTO>> listMetricPermissions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String subjectCode) {
        PageResult<MetricPermissionConfigDTO> result = authMetricPermissionService.listMetricPermissions(pageNum, pageSize, subjectCode);
        return Response.success(result);
    }

    /**
     * 批量更新指标权限配置
     */
    @PostMapping("/metric-permissions/batch-update")
    // API: ready
    public Response<Void> batchUpdateMetricPermissions(@RequestBody BatchUpdateMetricPermissionCmd cmd) {
        authMetricPermissionService.batchUpdateMetricPermissions(cmd.getMetricIds(), cmd.getVisibility(), cmd.getAllowedRoles());
        return Response.success(null);
    }

    /**
     * 查询维度权限配置
     */
    @GetMapping("/dimension-permissions")
    // API: ready
    public Response<List<DimensionPermissionDTO>> listDimensionPermissions() {
        List<DimensionPermissionDTO> list = authMetricPermissionService.listDimensionPermissions();
        return Response.success(list);
    }

    /**
     * 保存维度权限配置
     */
    @PostMapping("/dimension-permissions")
    // API: ready
    public Response<Void> saveDimensionPermission(@RequestBody SaveDimensionPermissionCmd cmd) {
        authMetricPermissionService.saveDimensionPermission(cmd.getDimensionCode(), cmd.getActions(), cmd.getAllowValuesQuery(), cmd.getTargets());
        return Response.success(null);
    }
}
