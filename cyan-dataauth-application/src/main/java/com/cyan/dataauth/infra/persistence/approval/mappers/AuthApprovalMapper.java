package com.cyan.dataauth.infra.persistence.approval.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.dataauth.infra.persistence.approval.dos.AuthApprovalDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批单Mapper接口
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface AuthApprovalMapper extends BaseMapper<AuthApprovalDO> {
}
