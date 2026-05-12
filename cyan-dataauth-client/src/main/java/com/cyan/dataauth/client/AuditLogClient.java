package com.cyan.dataauth.client;

import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.dto.AuditLogDTO;
import com.cyan.dataauth.dto.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 审计日志 RPC 客户端（服务间内部调用）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@FeignClient(name = "cyan-dataauth", contextId = "cyan-dataauth.audit", path = "/rpc/v1/audit")
public interface AuditLogClient {

    /**
     * 分页查询审计日志
     */
    @GetMapping("/logs")
    Response<PageResult<AuditLogDTO>> list(@RequestParam(value = "passport", required = false) String passport,
                                           @RequestParam(value = "action", required = false) String action,
                                           @RequestParam(value = "resourceType", required = false) String resourceType,
                                           @RequestParam(value = "startTime", required = false) String startTime,
                                           @RequestParam(value = "endTime", required = false) String endTime,
                                           @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "20") int pageSize);
}
