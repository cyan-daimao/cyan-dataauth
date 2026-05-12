package com.cyan.dataauth.client;

import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.cmd.AuthCheckCmd;
import com.cyan.dataauth.cmd.FilterSqlCmd;
import com.cyan.dataauth.dto.AuthCheckResult;
import com.cyan.dataauth.dto.FilterSqlResult;
import com.cyan.dataauth.dto.ResourceTreeNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 元数据权限校验 RPC 客户端（服务间内部调用）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@FeignClient(name = "cyan-dataauth", contextId = "cyan-dataauth.check", path = "/rpc/v1/auth/check")
public interface AuthCheckClient {

    /**
     * 权限校验
     */
    @PostMapping
    Response<AuthCheckResult> check(@RequestBody AuthCheckCmd cmd);

    /**
     * SQL过滤
     */
    @PostMapping("/filter/sql")
    Response<FilterSqlResult> filterSql(@RequestBody FilterSqlCmd cmd);

    /**
     * 查询资源树
     */
    @GetMapping("/resources")
    Response<List<ResourceTreeNode>> listResources(@RequestParam("passport") String passport,
                                                    @RequestParam(value = "resourceType", required = false) String resourceType);
}
