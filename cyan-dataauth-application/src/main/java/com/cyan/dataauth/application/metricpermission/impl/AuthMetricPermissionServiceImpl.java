package com.cyan.dataauth.application.metricpermission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyan.dataauth.application.metricpermission.AuthMetricPermissionService;
import com.cyan.dataauth.dto.*;
import com.cyan.dataauth.infra.persistence.permission.dos.AuthPermissionDO;
import com.cyan.dataauth.infra.persistence.permission.mappers.AuthPermissionMapper;
import com.cyan.dataauth.infra.persistence.role.dos.AuthRoleDO;
import com.cyan.dataauth.infra.persistence.role.mappers.AuthRoleMapper;
import com.cyan.dataauth.infra.persistence.rolepermission.dos.AuthRolePermissionDO;
import com.cyan.dataauth.infra.persistence.rolepermission.mappers.AuthRolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 指标权限配置应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthMetricPermissionServiceImpl implements AuthMetricPermissionService {

    private final AuthPermissionMapper authPermissionMapper;
    private final AuthRolePermissionMapper authRolePermissionMapper;
    private final AuthRoleMapper authRoleMapper;

    @Override
    public List<SubjectPermissionDTO> listSubjectPermissions() {
        // 查询所有 SUBJECT 类型的权限
        LambdaQueryWrapper<AuthPermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthPermissionDO::getResourceType, "SUBJECT");
        List<AuthPermissionDO> permissions = authPermissionMapper.selectList(wrapper);
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }

        // 查询角色权限关联
        List<AuthRolePermissionDO> rolePerms = authRolePermissionMapper.selectList(null);
        Map<Long, List<Long>> permRoleIdsMap = rolePerms.stream()
                .collect(Collectors.groupingBy(
                        AuthRolePermissionDO::getPermissionId,
                        Collectors.mapping(AuthRolePermissionDO::getRoleId, Collectors.toList())
                ));

        // 查询角色信息
        List<Long> allRoleIds = rolePerms.stream().map(AuthRolePermissionDO::getRoleId).distinct().collect(Collectors.toList());
        Map<Long, AuthRoleDO> roleMap = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            List<AuthRoleDO> roles = authRoleMapper.selectBatchIds(allRoleIds);
            if (roles != null) {
                for (AuthRoleDO r : roles) {
                    roleMap.put(r.getId(), r);
                }
            }
        }

        // 按 subjectCode 聚合
        Map<String, SubjectPermissionDTO> subjectMap = new LinkedHashMap<>();
        for (AuthPermissionDO perm : permissions) {
            String subjectCode = perm.getResourceId();
            SubjectPermissionDTO dto = subjectMap.computeIfAbsent(subjectCode, k -> {
                SubjectPermissionDTO d = new SubjectPermissionDTO();
                d.setSubjectCode(k);
                d.setSubjectName(k);
                d.setActions(new ArrayList<>());
                d.setTargets(new ArrayList<>());
                return d;
            });
            if (perm.getAction() != null && !dto.getActions().contains(perm.getAction())) {
                dto.getActions().add(perm.getAction());
            }

            List<Long> roleIds = permRoleIdsMap.getOrDefault(perm.getId(), List.of());
            for (Long roleId : roleIds) {
                AuthRoleDO role = roleMap.get(roleId);
                if (role == null) continue;
                boolean exists = dto.getTargets().stream()
                        .anyMatch(t -> "ROLE".equals(t.getTargetType()) && role.getCode().equals(t.getTargetId()));
                if (!exists) {
                    dto.getTargets().add(new PermissionTargetDTO("ROLE", role.getCode(), role.getName()));
                }
            }
        }
        return new ArrayList<>(subjectMap.values());
    }

    @Override
    @Transactional
    public void saveSubjectPermission(String subjectCode, List<String> actions, List<PermissionTargetDTO> targets) {
        // 删除该主题域所有现有权限项及关联
        LambdaQueryWrapper<AuthPermissionDO> permWrapper = new LambdaQueryWrapper<>();
        permWrapper.eq(AuthPermissionDO::getResourceType, "SUBJECT");
        permWrapper.eq(AuthPermissionDO::getResourceId, subjectCode);
        List<AuthPermissionDO> oldPerms = authPermissionMapper.selectList(permWrapper);
        if (oldPerms != null && !oldPerms.isEmpty()) {
            for (AuthPermissionDO old : oldPerms) {
                LambdaQueryWrapper<AuthRolePermissionDO> rpWrapper = new LambdaQueryWrapper<>();
                rpWrapper.eq(AuthRolePermissionDO::getPermissionId, old.getId());
                authRolePermissionMapper.delete(rpWrapper);
                authPermissionMapper.deleteById(old.getId());
            }
        }

        // 创建新的权限项
        if (actions == null || actions.isEmpty()) {
            return;
        }
        List<Long> targetRoleIds = resolveTargetRoleIds(targets);
        for (String action : actions) {
            AuthPermissionDO perm = new AuthPermissionDO();
            perm.setResourceType("SUBJECT");
            perm.setResourceId(subjectCode);
            perm.setAction(action);
            perm.setDescription("主题域权限: " + subjectCode);
            perm.setCreatedAt(LocalDateTime.now());
            authPermissionMapper.insert(perm);

            // 关联角色
            for (Long roleId : targetRoleIds) {
                AuthRolePermissionDO rp = new AuthRolePermissionDO();
                rp.setRoleId(roleId);
                rp.setPermissionId(perm.getId());
                rp.setCreatedAt(LocalDateTime.now());
                authRolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    public PageResult<MetricPermissionConfigDTO> listMetricPermissions(int pageNum, int pageSize, String subjectCode) {
        // 查询所有 METRIC 类型的权限项
        LambdaQueryWrapper<AuthPermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthPermissionDO::getResourceType, "METRIC");
        List<AuthPermissionDO> permissions = authPermissionMapper.selectList(wrapper);
        if (permissions == null || permissions.isEmpty()) {
            return new PageResult<>(List.of(), 0);
        }

        // 查询角色权限关联
        List<AuthRolePermissionDO> rolePerms = authRolePermissionMapper.selectList(null);
        Map<Long, List<Long>> permRoleIdsMap = rolePerms.stream()
                .collect(Collectors.groupingBy(
                        AuthRolePermissionDO::getPermissionId,
                        Collectors.mapping(AuthRolePermissionDO::getRoleId, Collectors.toList())
                ));

        // 查询角色信息
        List<Long> allRoleIds = rolePerms.stream().map(AuthRolePermissionDO::getRoleId).distinct().collect(Collectors.toList());
        Map<Long, AuthRoleDO> roleMap = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            List<AuthRoleDO> roles = authRoleMapper.selectBatchIds(allRoleIds);
            if (roles != null) {
                for (AuthRoleDO r : roles) {
                    roleMap.put(r.getId(), r);
                }
            }
        }

        // 按 metricCode 聚合
        Map<String, MetricPermissionConfigDTO> metricMap = new LinkedHashMap<>();
        for (AuthPermissionDO perm : permissions) {
            String metricCode = perm.getResourceId();
            MetricPermissionConfigDTO dto = metricMap.computeIfAbsent(metricCode, k -> {
                MetricPermissionConfigDTO d = new MetricPermissionConfigDTO();
                d.setMetricId(k);
                d.setMetricCode(k);
                d.setMetricName(k);
                d.setSubjectCode("");
                d.setSubjectName("");
                d.setStatus("PUBLISHED");
                d.setVisibility("PUBLIC");
                d.setAllowedRoles(new ArrayList<>());
                return d;
            });

            List<Long> roleIds = permRoleIdsMap.getOrDefault(perm.getId(), List.of());
            for (Long roleId : roleIds) {
                AuthRoleDO role = roleMap.get(roleId);
                if (role == null) continue;
                if (!dto.getAllowedRoles().contains(role.getCode())) {
                    dto.getAllowedRoles().add(role.getCode());
                }
            }
            if (!dto.getAllowedRoles().isEmpty()) {
                dto.setVisibility("ROLE");
            }
        }

        List<MetricPermissionConfigDTO> allList = new ArrayList<>(metricMap.values());

        // subjectCode 过滤
        if (subjectCode != null && !subjectCode.isBlank()) {
            allList = allList.stream()
                    .filter(m -> subjectCode.equals(m.getSubjectCode()))
                    .collect(Collectors.toList());
        }

        long total = allList.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allList.size());
        if (fromIndex >= allList.size()) {
            return new PageResult<>(List.of(), total);
        }
        List<MetricPermissionConfigDTO> pageList = allList.subList(fromIndex, toIndex);
        return new PageResult<>(pageList, total);
    }

    @Override
    @Transactional
    public void batchUpdateMetricPermissions(List<String> metricIds, String visibility, List<String> allowedRoles) {
        if (metricIds == null || metricIds.isEmpty()) {
            return;
        }
        // 删除这些指标的现有权限项及关联
        for (String metricId : metricIds) {
            LambdaQueryWrapper<AuthPermissionDO> permWrapper = new LambdaQueryWrapper<>();
            permWrapper.eq(AuthPermissionDO::getResourceType, "METRIC");
            permWrapper.eq(AuthPermissionDO::getResourceId, metricId);
            List<AuthPermissionDO> oldPerms = authPermissionMapper.selectList(permWrapper);
            if (oldPerms != null) {
                for (AuthPermissionDO old : oldPerms) {
                    LambdaQueryWrapper<AuthRolePermissionDO> rpWrapper = new LambdaQueryWrapper<>();
                    rpWrapper.eq(AuthRolePermissionDO::getPermissionId, old.getId());
                    authRolePermissionMapper.delete(rpWrapper);
                    authPermissionMapper.deleteById(old.getId());
                }
            }
        }

        // 如果 visibility=PUBLIC 则不需要创建权限项（默认公开）
        if ("PUBLIC".equals(visibility)) {
            return;
        }

        // 按 allowedRoles 创建新的 VIEW 权限项
        if (allowedRoles == null || allowedRoles.isEmpty()) {
            return;
        }
        List<Long> targetRoleIds = resolveRoleCodesToIds(allowedRoles);
        for (String metricId : metricIds) {
            AuthPermissionDO perm = new AuthPermissionDO();
            perm.setResourceType("METRIC");
            perm.setResourceId(metricId);
            perm.setAction("VIEW");
            perm.setDescription("指标权限: " + metricId);
            perm.setCreatedAt(LocalDateTime.now());
            authPermissionMapper.insert(perm);

            for (Long roleId : targetRoleIds) {
                AuthRolePermissionDO rp = new AuthRolePermissionDO();
                rp.setRoleId(roleId);
                rp.setPermissionId(perm.getId());
                rp.setCreatedAt(LocalDateTime.now());
                authRolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    public List<DimensionPermissionDTO> listDimensionPermissions() {
        // 查询所有 DIMENSION 类型的权限
        LambdaQueryWrapper<AuthPermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthPermissionDO::getResourceType, "DIMENSION");
        List<AuthPermissionDO> permissions = authPermissionMapper.selectList(wrapper);
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }

        // 查询角色权限关联
        List<AuthRolePermissionDO> rolePerms = authRolePermissionMapper.selectList(null);
        Map<Long, List<Long>> permRoleIdsMap = rolePerms.stream()
                .collect(Collectors.groupingBy(
                        AuthRolePermissionDO::getPermissionId,
                        Collectors.mapping(AuthRolePermissionDO::getRoleId, Collectors.toList())
                ));

        // 查询角色信息
        List<Long> allRoleIds = rolePerms.stream().map(AuthRolePermissionDO::getRoleId).distinct().collect(Collectors.toList());
        Map<Long, AuthRoleDO> roleMap = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            List<AuthRoleDO> roles = authRoleMapper.selectBatchIds(allRoleIds);
            if (roles != null) {
                for (AuthRoleDO r : roles) {
                    roleMap.put(r.getId(), r);
                }
            }
        }

        // 按 dimensionCode 聚合
        Map<String, DimensionPermissionDTO> dimMap = new LinkedHashMap<>();
        for (AuthPermissionDO perm : permissions) {
            String dimCode = perm.getResourceId();
            DimensionPermissionDTO dto = dimMap.computeIfAbsent(dimCode, k -> {
                DimensionPermissionDTO d = new DimensionPermissionDTO();
                d.setDimensionId(k);
                d.setDimensionCode(k);
                d.setDimensionName(k);
                d.setCategory("");
                d.setRelatedField("");
                d.setActions(new ArrayList<>());
                d.setAllowValuesQuery(false);
                d.setTargets(new ArrayList<>());
                return d;
            });
            if (perm.getAction() != null && !dto.getActions().contains(perm.getAction())) {
                dto.getActions().add(perm.getAction());
            }
            if ("VALUES".equals(perm.getAction())) {
                dto.setAllowValuesQuery(true);
            }

            List<Long> roleIds = permRoleIdsMap.getOrDefault(perm.getId(), List.of());
            for (Long roleId : roleIds) {
                AuthRoleDO role = roleMap.get(roleId);
                if (role == null) continue;
                boolean exists = dto.getTargets().stream()
                        .anyMatch(t -> "ROLE".equals(t.getTargetType()) && role.getCode().equals(t.getTargetId()));
                if (!exists) {
                    dto.getTargets().add(new PermissionTargetDTO("ROLE", role.getCode(), role.getName()));
                }
            }
        }
        return new ArrayList<>(dimMap.values());
    }

    @Override
    @Transactional
    public void saveDimensionPermission(String dimensionCode, List<String> actions, Boolean allowValuesQuery, List<PermissionTargetDTO> targets) {
        // 删除该维度所有现有权限项及关联
        LambdaQueryWrapper<AuthPermissionDO> permWrapper = new LambdaQueryWrapper<>();
        permWrapper.eq(AuthPermissionDO::getResourceType, "DIMENSION");
        permWrapper.eq(AuthPermissionDO::getResourceId, dimensionCode);
        List<AuthPermissionDO> oldPerms = authPermissionMapper.selectList(permWrapper);
        if (oldPerms != null && !oldPerms.isEmpty()) {
            for (AuthPermissionDO old : oldPerms) {
                LambdaQueryWrapper<AuthRolePermissionDO> rpWrapper = new LambdaQueryWrapper<>();
                rpWrapper.eq(AuthRolePermissionDO::getPermissionId, old.getId());
                authRolePermissionMapper.delete(rpWrapper);
                authPermissionMapper.deleteById(old.getId());
            }
        }

        // 创建新的权限项
        List<String> allActions = new ArrayList<>();
        if (actions != null) {
            allActions.addAll(actions);
        }
        if (Boolean.TRUE.equals(allowValuesQuery) && !allActions.contains("VALUES")) {
            allActions.add("VALUES");
        }
        if (allActions.isEmpty()) {
            return;
        }

        List<Long> targetRoleIds = resolveTargetRoleIds(targets);
        for (String action : allActions) {
            AuthPermissionDO perm = new AuthPermissionDO();
            perm.setResourceType("DIMENSION");
            perm.setResourceId(dimensionCode);
            perm.setAction(action);
            perm.setDescription("维度权限: " + dimensionCode);
            perm.setCreatedAt(LocalDateTime.now());
            authPermissionMapper.insert(perm);

            for (Long roleId : targetRoleIds) {
                AuthRolePermissionDO rp = new AuthRolePermissionDO();
                rp.setRoleId(roleId);
                rp.setPermissionId(perm.getId());
                rp.setCreatedAt(LocalDateTime.now());
                authRolePermissionMapper.insert(rp);
            }
        }
    }

    private List<Long> resolveTargetRoleIds(List<PermissionTargetDTO> targets) {
        if (targets == null || targets.isEmpty()) {
            return List.of();
        }
        List<String> roleCodes = targets.stream()
                .filter(t -> "ROLE".equals(t.getTargetType()) && t.getTargetId() != null)
                .map(PermissionTargetDTO::getTargetId)
                .distinct()
                .collect(Collectors.toList());
        return resolveRoleCodesToIds(roleCodes);
    }

    private List<Long> resolveRoleCodesToIds(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<AuthRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AuthRoleDO::getCode, roleCodes);
        List<AuthRoleDO> roles = authRoleMapper.selectList(wrapper);
        if (roles == null) {
            return List.of();
        }
        return roles.stream().map(AuthRoleDO::getId).collect(Collectors.toList());
    }
}
