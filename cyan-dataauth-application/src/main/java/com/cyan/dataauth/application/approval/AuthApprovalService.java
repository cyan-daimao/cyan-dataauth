package com.cyan.dataauth.application.approval;

import com.cyan.dataauth.application.approval.bo.ApprovalBO;
import com.cyan.dataauth.cmd.ApprovalActionCmd;
import com.cyan.dataauth.cmd.ApprovalSubmitCmd;
import com.cyan.arch.common.api.Page;

/**
 * 审批应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthApprovalService {

    /**
     * 提交审批
     */
    ApprovalBO submit(ApprovalSubmitCmd cmd);

    /**
     * 分页查询
     */
    Page<ApprovalBO> list(String passport, String status, String type, long current, long size);

    /**
     * 审批操作
     */
    ApprovalBO action(String approvalId, ApprovalActionCmd cmd);
}
