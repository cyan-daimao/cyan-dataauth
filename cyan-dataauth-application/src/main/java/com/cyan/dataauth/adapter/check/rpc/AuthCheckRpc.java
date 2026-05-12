package com.cyan.dataauth.adapter.check.rpc;

import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.adapter.check.convert.AuthCheckAdapterConvert;
import com.cyan.dataauth.application.check.AuthCheckService;
import com.cyan.dataauth.application.check.bo.*;
import com.cyan.dataauth.client.AuthCheckClient;
import com.cyan.dataauth.cmd.AuthCheckCmd;
import com.cyan.dataauth.cmd.FilterSqlCmd;
import com.cyan.dataauth.dto.AuthCheckResult;
import com.cyan.dataauth.dto.FilterSqlResult;
import com.cyan.dataauth.dto.ResourceTreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 元数据权限校验 RPC 服务（供内部服务调用）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/rpc/v1/auth/check")
@RequiredArgsConstructor
public class AuthCheckRpc implements AuthCheckClient {

    private final AuthCheckService authCheckService;
    private final AuthCheckAdapterConvert authCheckAdapterConvert;

    @Override
    public Response<AuthCheckResult> check(AuthCheckCmd cmd) {
        AuthCheckResultBO bo = authCheckService.check(cmd);
        return Response.success(authCheckAdapterConvert.toAuthCheckResult(bo));
    }

    @Override
    public Response<FilterSqlResult> filterSql(FilterSqlCmd cmd) {
        FilterSqlResultBO bo = authCheckService.filterSql(cmd);
        return Response.success(authCheckAdapterConvert.toFilterSqlResult(bo));
    }

    @Override
    public Response<List<ResourceTreeNode>> listResources(String passport, String resourceType) {
        List<ResourceTreeNodeBO> bos = authCheckService.listResources(passport, resourceType);
        return Response.success(authCheckAdapterConvert.toResourceTreeNodeList(bos));
    }
}
