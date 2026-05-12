package com.cyan.dataauth.domain.approval.query;

import com.cyan.arch.common.api.Pageable;

/**
 * 审批分页查询
 *
 * @author cy.Y
 * @since 1.0.0
 */
public record ApprovalPageQuery(
        // 申请人护照
        String passport,
        // 状态
        String status,
        // 类型
        String type,
        // 当前页
        long current,
        // 每页大小
        long size
) implements Pageable {
}
