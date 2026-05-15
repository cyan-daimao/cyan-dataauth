package com.cyan.dataauth.infra.persistence.rolepermission.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyan.dataauth.infra.persistence.rolepermission.dos.AuthRolePermissionDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Mapper
public interface AuthRolePermissionMapper extends BaseMapper<AuthRolePermissionDO> {

    @Select("SELECT permission_id FROM auth_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 按角色ID和资源类型查询关联的权限项ID (Round2: ready)
     */
    @Select("SELECT rp.permission_id FROM auth_role_permission rp " +
            "INNER JOIN auth_permission p ON rp.permission_id = p.id " +
            "WHERE rp.role_id = #{roleId} AND p.resource_type = #{resourceType}")
    List<Long> selectPermissionIdsByRoleIdAndResourceType(@Param("roleId") Long roleId, @Param("resourceType") String resourceType);

    /**
     * 删除角色下指定资源类型的权限关联 (Round2: ready)
     */
    @Delete("DELETE FROM auth_role_permission WHERE role_id = #{roleId} " +
            "AND permission_id IN (SELECT id FROM auth_permission WHERE resource_type = #{resourceType})")
    int deleteByRoleIdAndResourceType(@Param("roleId") Long roleId, @Param("resourceType") String resourceType);
}
