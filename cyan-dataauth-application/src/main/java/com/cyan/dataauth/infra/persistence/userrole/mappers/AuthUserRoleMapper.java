package com.cyan.dataauth.infra.persistence.userrole.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.dataauth.infra.persistence.userrole.dos.AuthUserRoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface AuthUserRoleMapper extends BaseMapper<AuthUserRoleDO> {

    @Select("SELECT role_id FROM auth_user_role WHERE passport = #{passport}")
    List<Long> selectRoleIdsByPassport(@Param("passport") String passport);
}
