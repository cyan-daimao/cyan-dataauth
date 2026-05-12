package com.cyan.dataauth.application.audit;

import com.cyan.dataauth.application.audit.bo.AuditLogBO;
import com.cyan.arch.common.api.Page;

/**
 * 审计日志应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthAuditLogService {

    /**
     * 分页查询
     */
    Page<AuditLogBO> list(String passport, String action, String resourceType,
                          String startTime, String endTime, long current, long size);
}
