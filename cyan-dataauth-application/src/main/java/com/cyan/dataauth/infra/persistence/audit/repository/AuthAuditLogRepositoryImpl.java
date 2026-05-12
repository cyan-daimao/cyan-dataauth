package com.cyan.dataauth.infra.persistence.audit.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyan.dataauth.domain.audit.AuthAuditLog;
import com.cyan.dataauth.domain.audit.query.AuditLogPageQuery;
import com.cyan.dataauth.domain.audit.repository.AuthAuditLogRepository;
import com.cyan.dataauth.infra.persistence.audit.convert.AuthAuditLogInfraConvert;
import com.cyan.dataauth.infra.persistence.audit.dos.AuthAuditLogDO;
import com.cyan.dataauth.infra.persistence.audit.mappers.AuthAuditLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 审计日志仓储实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class AuthAuditLogRepositoryImpl implements AuthAuditLogRepository {

    private final AuthAuditLogMapper authAuditLogMapper;
    private final AuthAuditLogInfraConvert authAuditLogInfraConvert;

    @Override
    public com.cyan.arch.common.api.Page<AuthAuditLog> page(AuditLogPageQuery query) {
        LambdaQueryWrapper<AuthAuditLogDO> wrapper = new LambdaQueryWrapper<>();
        if (query.passport() != null && !query.passport().isEmpty()) {
            wrapper.eq(AuthAuditLogDO::getUserId, query.passport());
        }
        if (query.action() != null && !query.action().isEmpty()) {
            wrapper.eq(AuthAuditLogDO::getAction, query.action());
        }
        if (query.resourceType() != null && !query.resourceType().isEmpty()) {
            wrapper.eq(AuthAuditLogDO::getResourceType, query.resourceType());
        }
        if (query.startTime() != null && !query.startTime().isEmpty()) {
            wrapper.ge(AuthAuditLogDO::getTimestamp, LocalDateTime.parse(query.startTime(), DateTimeFormatter.ISO_DATE_TIME));
        }
        if (query.endTime() != null && !query.endTime().isEmpty()) {
            wrapper.le(AuthAuditLogDO::getTimestamp, LocalDateTime.parse(query.endTime(), DateTimeFormatter.ISO_DATE_TIME));
        }
        wrapper.orderByDesc(AuthAuditLogDO::getTimestamp);

        Page<AuthAuditLogDO> page = new Page<>(query.current(), query.size());
        Page<AuthAuditLogDO> resultPage = authAuditLogMapper.selectPage(page, wrapper);

        return new com.cyan.arch.common.api.Page<>(
                authAuditLogInfraConvert.toAuthAuditLogList(resultPage.getRecords()),
                resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public void save(AuthAuditLog log) {
        AuthAuditLogDO logDO = authAuditLogInfraConvert.toAuthAuditLogDO(log);
        authAuditLogMapper.insert(logDO);
    }
}
