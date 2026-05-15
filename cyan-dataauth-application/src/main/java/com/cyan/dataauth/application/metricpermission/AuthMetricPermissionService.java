package com.cyan.dataauth.application.metricpermission;

import com.cyan.dataauth.dto.*;

import java.util.List;

/**
 * 指标权限配置应用服务
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthMetricPermissionService {

    /**
     * 查询主题域权限配置
     */
    List<SubjectPermissionDTO> listSubjectPermissions();

    /**
     * 保存主题域权限配置
     */
    void saveSubjectPermission(String subjectCode, List<String> actions, List<PermissionTargetDTO> targets);

    /**
     * 分页查询指标权限配置
     */
    PageResult<MetricPermissionConfigDTO> listMetricPermissions(int pageNum, int pageSize, String subjectCode);

    /**
     * 批量更新指标权限配置
     */
    void batchUpdateMetricPermissions(List<String> metricIds, String visibility, List<String> allowedRoles);

    /**
     * 查询维度权限配置
     */
    List<DimensionPermissionDTO> listDimensionPermissions();

    /**
     * 保存维度权限配置
     */
    void saveDimensionPermission(String dimensionCode, List<String> actions, Boolean allowValuesQuery, List<PermissionTargetDTO> targets);
}
