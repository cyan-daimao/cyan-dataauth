package com.cyan.dataauth.domain.audit.repository;

import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.domain.audit.AuthAuditLog;
import com.cyan.dataauth.domain.audit.query.AuditLogPageQuery;

/**
 * 审计日志仓储
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthAuditLogRepository {

    /**
     * 分页查询
     */
    Page<AuthAuditLog> page(AuditLogPageQuery query);

    /**
     * 保存
     */
    void save(AuthAuditLog log);
}
