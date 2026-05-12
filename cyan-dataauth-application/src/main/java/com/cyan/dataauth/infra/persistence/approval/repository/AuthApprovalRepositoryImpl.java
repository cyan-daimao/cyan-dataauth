package com.cyan.dataauth.infra.persistence.approval.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyan.arch.common.api.Pageable;
import com.cyan.dataauth.domain.approval.AuthApproval;
import com.cyan.dataauth.domain.approval.query.ApprovalPageQuery;
import com.cyan.dataauth.domain.approval.repository.AuthApprovalRepository;
import com.cyan.dataauth.infra.persistence.approval.convert.AuthApprovalInfraConvert;
import com.cyan.dataauth.infra.persistence.approval.dos.AuthApprovalDO;
import com.cyan.dataauth.infra.persistence.approval.mappers.AuthApprovalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 审批仓储实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class AuthApprovalRepositoryImpl implements AuthApprovalRepository {

    private final AuthApprovalMapper authApprovalMapper;
    private final AuthApprovalInfraConvert authApprovalInfraConvert;

    @Override
    public AuthApproval getByApprovalId(String approvalId) {
        LambdaQueryWrapper<AuthApprovalDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthApprovalDO::getApprovalId, approvalId);
        AuthApprovalDO approvalDO = authApprovalMapper.selectOne(wrapper);
        return authApprovalInfraConvert.toAuthApproval(approvalDO);
    }

    @Override
    public com.cyan.arch.common.api.Page<AuthApproval> page(ApprovalPageQuery query) {
        LambdaQueryWrapper<AuthApprovalDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthApprovalDO::getApplicantPassport, query.passport());
        if (query.status() != null && !query.status().isEmpty()) {
            wrapper.eq(AuthApprovalDO::getStatus, query.status());
        }
        if (query.type() != null && !query.type().isEmpty()) {
            wrapper.eq(AuthApprovalDO::getApprovalType, query.type());
        }
        wrapper.orderByDesc(AuthApprovalDO::getSubmittedAt);

        Page<AuthApprovalDO> page = new Page<>(query.current(), query.size());
        Page<AuthApprovalDO> resultPage = authApprovalMapper.selectPage(page, wrapper);

        return new com.cyan.arch.common.api.Page<>(
                authApprovalInfraConvert.toAuthApprovalList(resultPage.getRecords()),
                resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public AuthApproval save(AuthApproval approval) {
        AuthApprovalDO approvalDO = authApprovalInfraConvert.toAuthApprovalDO(approval);
        authApprovalMapper.insert(approvalDO);
        return getByApprovalId(approval.getApprovalId());
    }

    @Override
    public AuthApproval update(AuthApproval approval) {
        AuthApprovalDO approvalDO = authApprovalInfraConvert.toAuthApprovalDO(approval);
        authApprovalMapper.updateById(approvalDO);
        return getByApprovalId(approval.getApprovalId());
    }
}
