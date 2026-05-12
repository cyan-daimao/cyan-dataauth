package com.cyan.dataauth.adapter.audit.rpc;

import com.cyan.arch.common.api.Response;
import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.adapter.audit.convert.AuditLogAdapterConvert;
import com.cyan.dataauth.application.audit.AuthAuditLogService;
import com.cyan.dataauth.application.audit.bo.AuditLogBO;
import com.cyan.dataauth.client.AuditLogClient;
import com.cyan.dataauth.dto.AuditLogDTO;
import com.cyan.dataauth.dto.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审计日志 RPC 服务（供内部服务调用）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/rpc/v1/audit")
@RequiredArgsConstructor
public class AuditLogRpc implements AuditLogClient {

    private final AuthAuditLogService authAuditLogService;
    private final AuditLogAdapterConvert auditLogAdapterConvert;

    @Override
    public Response<PageResult<AuditLogDTO>> list(String passport, String action, String resourceType,
                                                   String startTime, String endTime, int pageNum, int pageSize) {
        Page<AuditLogBO> page = authAuditLogService.list(passport, action, resourceType, startTime, endTime, pageNum, pageSize);
        return Response.success(auditLogAdapterConvert.toPageResult(page));
    }
}
