package com.cyan.dataauth.domain.approval.repository;

import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.domain.approval.AuthApproval;
import com.cyan.dataauth.domain.approval.query.ApprovalPageQuery;

/**
 * 审批仓储
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthApprovalRepository {

    /**
     * 根据审批单号查询
     */
    AuthApproval getByApprovalId(String approvalId);

    /**
     * 分页查询
     */
    Page<AuthApproval> page(ApprovalPageQuery query);

    /**
     * 保存
     */
    AuthApproval save(AuthApproval approval);

    /**
     * 更新
     */
    AuthApproval update(AuthApproval approval);
}
