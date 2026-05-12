package com.cyan.dataauth.adapter.audit.http;

import com.cyan.dataauth.adapter.audit.convert.AuditLogAdapterConvert;
import com.cyan.dataauth.application.audit.AuthAuditLogService;
import com.cyan.dataauth.application.audit.bo.AuditLogBO;
import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.dto.AuditLogDTO;
import com.cyan.dataauth.dto.PageResult;
import com.cyan.arch.common.api.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审计日志控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuthAuditLogService authAuditLogService;
    private final AuditLogAdapterConvert auditLogAdapterConvert;

    /**
     * 分页查询审计日志
     */
    @GetMapping("/logs")
    public Response<PageResult<AuditLogDTO>> list(@RequestParam(required = false) String passport,
                                                  @RequestParam(required = false) String action,
                                                  @RequestParam(required = false) String resourceType,
                                                  @RequestParam(required = false) String startTime,
                                                  @RequestParam(required = false) String endTime,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "20") int pageSize) {
        Page<AuditLogBO> page = authAuditLogService.list(passport, action, resourceType, startTime, endTime, pageNum, pageSize);
        return Response.success(auditLogAdapterConvert.toPageResult(page));
    }
}
