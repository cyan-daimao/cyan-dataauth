package com.cyan.dataauth.adapter.check.http;

import com.cyan.dataauth.adapter.check.convert.AuthCheckAdapterConvert;
import com.cyan.dataauth.application.check.AuthCheckService;
import com.cyan.dataauth.application.check.bo.*;
import com.cyan.dataauth.cmd.AuthCheckCmd;
import com.cyan.dataauth.cmd.FilterSqlCmd;
import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限校验控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthCheckController {

    private final AuthCheckService authCheckService;
    private final AuthCheckAdapterConvert authCheckAdapterConvert;

    /**
     * 权限校验
     */
    @PostMapping("/check")
    public Response<AuthCheckResult> check(@RequestBody AuthCheckCmd cmd) {
        AuthCheckResultBO bo = authCheckService.check(cmd);
        return Response.success(authCheckAdapterConvert.toAuthCheckResult(bo));
    }

    /**
     * SQL过滤
     */
    @PostMapping("/filter/sql")
    public Response<FilterSqlResult> filterSql(@RequestBody FilterSqlCmd cmd) {
        FilterSqlResultBO bo = authCheckService.filterSql(cmd);
        return Response.success(authCheckAdapterConvert.toFilterSqlResult(bo));
    }

    /**
     * 查询资源树
     */
    @GetMapping("/resources")
    public Response<List<ResourceTreeNode>> listResources(@RequestParam String passport,
                                                          @RequestParam(required = false) String resourceType) {
        List<ResourceTreeNodeBO> bos = authCheckService.listResources(passport, resourceType);
        return Response.success(authCheckAdapterConvert.toResourceTreeNodeList(bos));
    }
}
