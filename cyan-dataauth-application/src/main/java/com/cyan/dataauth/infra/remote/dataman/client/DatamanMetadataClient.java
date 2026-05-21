package com.cyan.dataauth.infra.remote.dataman.client;

import com.cyan.arch.common.api.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 元数据平台元数据 Feign 客户端
 *
 * @author cy.Y
 * @since 1.0.0
 */
@FeignClient(name = "cyan-dataman", path = "/rpc/v1/agent/meta")
public interface DatamanMetadataClient {

    /**
     * 获取表的安全等级
     *
     * @param tableName 表名
     * @return 安全等级
     */
    @GetMapping("/tables/{tableName}/security-level")
    Response<String> getTableSecurityLevel(@PathVariable("tableName") String tableName);
}
