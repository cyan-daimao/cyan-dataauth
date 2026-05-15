package com.cyan.dataauth.infra.persistence.permission.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.dataauth.infra.persistence.permission.dos.AuthPermissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface AuthPermissionMapper extends BaseMapper<AuthPermissionDO> {

    @Select("SELECT p.* FROM auth_permission p " +
            "INNER JOIN auth_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN auth_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.passport = #{passport}")
    List<AuthPermissionDO> selectByPassport(@Param("passport") String passport);

    @Select("SELECT p.* FROM auth_permission p " +
            "INNER JOIN auth_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<AuthPermissionDO> selectByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT p.* FROM auth_permission p " +
            "WHERE p.resource_type = #{resourceType}")
    List<AuthPermissionDO> selectByResourceType(@Param("resourceType") String resourceType);

    @Select("SELECT * FROM auth_permission WHERE resource_type = #{resourceType} AND resource_id = #{resourceId} AND action = #{action}")
    AuthPermissionDO selectByResource(@Param("resourceType") String resourceType, @Param("resourceId") String resourceId, @Param("action") String action);
}
