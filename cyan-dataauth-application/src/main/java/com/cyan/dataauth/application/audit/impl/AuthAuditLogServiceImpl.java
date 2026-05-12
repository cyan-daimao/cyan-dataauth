package com.cyan.dataauth.application.audit.impl;

import com.cyan.dataauth.application.audit.AuthAuditLogService;
import com.cyan.dataauth.application.audit.bo.AuditLogBO;
import com.cyan.dataauth.application.audit.convert.AuthAuditLogAppConvert;
import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.domain.audit.AuthAuditLog;
import com.cyan.dataauth.domain.audit.query.AuditLogPageQuery;
import com.cyan.dataauth.domain.audit.repository.AuthAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 审计日志应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthAuditLogServiceImpl implements AuthAuditLogService {

    private final AuthAuditLogRepository authAuditLogRepository;
    private final AuthAuditLogAppConvert authAuditLogAppConvert;

    @Override
    public Page<AuditLogBO> list(String passport, String action, String resourceType,
                                 String startTime, String endTime, long current, long size) {
        AuditLogPageQuery query = new AuditLogPageQuery(passport, action, resourceType, startTime, endTime, current, size);
        Page<AuthAuditLog> page = authAuditLogRepository.page(query);
        return new Page<>(authAuditLogAppConvert.toAuditLogBOList(page.getData()),
                page.getCurrent(), page.getSize(), page.getTotal());
    }
}
