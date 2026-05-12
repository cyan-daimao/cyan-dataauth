package com.cyan.dataauth.infra.persistence.audit.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.dataauth.infra.persistence.audit.dos.AuthAuditLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志Mapper接口
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface AuthAuditLogMapper extends BaseMapper<AuthAuditLogDO> {
}
