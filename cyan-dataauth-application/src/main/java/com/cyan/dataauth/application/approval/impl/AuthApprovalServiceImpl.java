package com.cyan.dataauth.application.approval.impl;

import com.cyan.dataauth.application.approval.AuthApprovalService;
import com.cyan.dataauth.application.approval.bo.ApprovalBO;
import com.cyan.dataauth.application.approval.convert.AuthApprovalAppConvert;
import com.cyan.dataauth.cmd.ApprovalActionCmd;
import com.cyan.dataauth.cmd.ApprovalSubmitCmd;
import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.domain.approval.AuthApproval;
import com.cyan.dataauth.domain.approval.query.ApprovalPageQuery;
import com.cyan.dataauth.domain.approval.repository.AuthApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthApprovalServiceImpl implements AuthApprovalService {

    private final AuthApprovalRepository authApprovalRepository;
    private final AuthApprovalAppConvert authApprovalAppConvert;

    @Override
    @Transactional
    public ApprovalBO submit(ApprovalSubmitCmd cmd) {
        AuthApproval approval = authApprovalAppConvert.toAuthApproval(cmd);
        approval = approval.submit(authApprovalRepository);
        return authApprovalAppConvert.toApprovalBO(approval);
    }

    @Override
    public Page<ApprovalBO> list(String passport, String status, String type, long current, long size) {
        ApprovalPageQuery query = new ApprovalPageQuery(passport, status, type, current, size);
        Page<AuthApproval> page = authApprovalRepository.page(query);
        return new Page<>(authApprovalAppConvert.toApprovalBOList(page.getData()),
                page.getCurrent(), page.getSize(), page.getTotal());
    }

    @Override
    @Transactional
    public ApprovalBO action(String approvalId, ApprovalActionCmd cmd) {
        AuthApproval approval = new AuthApproval().setApprovalId(approvalId);
        if ("APPROVE".equals(cmd.getAction())) {
            approval = approval.approve(cmd.getOperatorPassport(), cmd.getComment(), authApprovalRepository);
        } else if ("REJECT".equals(cmd.getAction())) {
            approval = approval.reject(cmd.getOperatorPassport(), cmd.getComment(), authApprovalRepository);
        } else {
            throw new IllegalArgumentException("不支持的审批操作: " + cmd.getAction());
        }
        return authApprovalAppConvert.toApprovalBO(approval);
    }
}
