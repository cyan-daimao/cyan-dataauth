package com.cyan.dataauth.domain.audit.query;

import com.cyan.arch.common.api.Pageable;

/**
 * 审计日志分页查询
 *
 * @author cy.Y
 * @since 1.0.0
 */
public record AuditLogPageQuery(
        // 用户护照
        String passport,
        // 操作类型
        String action,
        // 资源类型
        String resourceType,
        // 开始时间
        String startTime,
        // 结束时间
        String endTime,
        // 当前页
        long current,
        // 每页大小
        long size
) implements Pageable {
}
