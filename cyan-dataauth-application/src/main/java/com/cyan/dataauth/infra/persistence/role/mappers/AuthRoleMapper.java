package com.cyan.dataauth.infra.persistence.role.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.dataauth.infra.persistence.role.dos.AuthRoleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface AuthRoleMapper extends BaseMapper<AuthRoleDO> {
}
