package com.cyan.dataauth.application.metricpermission.impl;

import com.cyan.dataauth.application.metricpermission.AuthMetricPermissionService;
import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import com.cyan.dataauth.domain.role.AuthRole;
import com.cyan.dataauth.domain.role.repository.AuthRoleRepository;
import com.cyan.dataauth.domain.rolepermission.repository.AuthRolePermissionRepository;
import com.cyan.dataauth.dto.DimensionPermissionDTO;
import com.cyan.dataauth.dto.MetricPermissionConfigDTO;
import com.cyan.dataauth.dto.PageResult;
import com.cyan.dataauth.dto.PermissionTargetDTO;
import com.cyan.dataauth.dto.SubjectPermissionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    private final AuthPermissionRepository authPermissionRepository;
    private final AuthRolePermissionRepository authRolePermissionRepository;
    private final AuthRoleRepository authRoleRepository;

    @Override
    public List<SubjectPermissionDTO> listSubjectPermissions() {
        List<AuthPermission> permissions = authPermissionRepository.listByResourceType("SUBJECT");
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }

        Map<Long, List<Long>> permRoleIdsMap = authRolePermissionRepository.getRoleIdsGroupedByPermissionId();
        List<Long> allRoleIds = permRoleIdsMap.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, AuthRole> roleMap = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            List<AuthRole> roles = authRoleRepository.listByIds(allRoleIds);
            if (roles != null) {
                for (AuthRole r : roles) {
                    roleMap.put(Long.valueOf(r.getId()), r);
                }
            }
        }

        Map<String, SubjectPermissionDTO> subjectMap = new LinkedHashMap<>();
        for (AuthPermission perm : permissions) {
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

            List<Long> roleIds = permRoleIdsMap.getOrDefault(Long.valueOf(perm.getId()), List.of());
            for (Long roleId : roleIds) {
                AuthRole role = roleMap.get(roleId);
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
        List<AuthPermission> oldPerms = authPermissionRepository.listByResourceTypeAndResourceId("SUBJECT", subjectCode);
        if (oldPerms != null && !oldPerms.isEmpty()) {
            for (AuthPermission old : oldPerms) {
                authRolePermissionRepository.deleteByPermissionId(Long.valueOf(old.getId()));
                authPermissionRepository.delete(old.getId());
            }
        }

        if (actions == null || actions.isEmpty()) {
            return;
        }
        List<Long> targetRoleIds = resolveTargetRoleIds(targets);
        for (String action : actions) {
            AuthPermission perm = new AuthPermission()
                    .setResourceType("SUBJECT")
                    .setResourceId(subjectCode)
                    .setAction(action)
                    .setDescription("主题域权限: " + subjectCode);
            perm = perm.save(authPermissionRepository);

            for (Long roleId : targetRoleIds) {
                authRolePermissionRepository.save(roleId, Long.valueOf(perm.getId()));
            }
        }
    }

    @Override
    public PageResult<MetricPermissionConfigDTO> listMetricPermissions(int pageNum, int pageSize, String subjectCode) {
        List<AuthPermission> permissions = authPermissionRepository.listByResourceType("METRIC");
        if (permissions == null || permissions.isEmpty()) {
            return new PageResult<>(List.of(), 0);
        }

        Map<Long, List<Long>> permRoleIdsMap = authRolePermissionRepository.getRoleIdsGroupedByPermissionId();
        List<Long> allRoleIds = permRoleIdsMap.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, AuthRole> roleMap = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            List<AuthRole> roles = authRoleRepository.listByIds(allRoleIds);
            if (roles != null) {
                for (AuthRole r : roles) {
                    roleMap.put(Long.valueOf(r.getId()), r);
                }
            }
        }

        Map<String, MetricPermissionConfigDTO> metricMap = new LinkedHashMap<>();
        for (AuthPermission perm : permissions) {
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

            List<Long> roleIds = permRoleIdsMap.getOrDefault(Long.valueOf(perm.getId()), List.of());
            for (Long roleId : roleIds) {
                AuthRole role = roleMap.get(roleId);
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
        for (String metricId : metricIds) {
            List<AuthPermission> oldPerms = authPermissionRepository.listByResourceTypeAndResourceId("METRIC", metricId);
            if (oldPerms != null) {
                for (AuthPermission old : oldPerms) {
                    authRolePermissionRepository.deleteByPermissionId(Long.valueOf(old.getId()));
                    authPermissionRepository.delete(old.getId());
                }
            }
        }

        if ("PUBLIC".equals(visibility)) {
            return;
        }

        if (allowedRoles == null || allowedRoles.isEmpty()) {
            return;
        }
        List<Long> targetRoleIds = resolveRoleCodesToIds(allowedRoles);
        for (String metricId : metricIds) {
            AuthPermission perm = new AuthPermission()
                    .setResourceType("METRIC")
                    .setResourceId(metricId)
                    .setAction("VIEW")
                    .setDescription("指标权限: " + metricId);
            perm = perm.save(authPermissionRepository);

            for (Long roleId : targetRoleIds) {
                authRolePermissionRepository.save(roleId, Long.valueOf(perm.getId()));
            }
        }
    }

    @Override
    public List<DimensionPermissionDTO> listDimensionPermissions() {
        List<AuthPermission> permissions = authPermissionRepository.listByResourceType("DIMENSION");
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }

        Map<Long, List<Long>> permRoleIdsMap = authRolePermissionRepository.getRoleIdsGroupedByPermissionId();
        List<Long> allRoleIds = permRoleIdsMap.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, AuthRole> roleMap = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            List<AuthRole> roles = authRoleRepository.listByIds(allRoleIds);
            if (roles != null) {
                for (AuthRole r : roles) {
                    roleMap.put(Long.valueOf(r.getId()), r);
                }
            }
        }

        Map<String, DimensionPermissionDTO> dimMap = new LinkedHashMap<>();
        for (AuthPermission perm : permissions) {
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

            List<Long> roleIds = permRoleIdsMap.getOrDefault(Long.valueOf(perm.getId()), List.of());
            for (Long roleId : roleIds) {
                AuthRole role = roleMap.get(roleId);
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
        List<AuthPermission> oldPerms = authPermissionRepository.listByResourceTypeAndResourceId("DIMENSION", dimensionCode);
        if (oldPerms != null && !oldPerms.isEmpty()) {
            for (AuthPermission old : oldPerms) {
                authRolePermissionRepository.deleteByPermissionId(Long.valueOf(old.getId()));
                authPermissionRepository.delete(old.getId());
            }
        }

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
            AuthPermission perm = new AuthPermission()
                    .setResourceType("DIMENSION")
                    .setResourceId(dimensionCode)
                    .setAction(action)
                    .setDescription("维度权限: " + dimensionCode);
            perm = perm.save(authPermissionRepository);

            for (Long roleId : targetRoleIds) {
                authRolePermissionRepository.save(roleId, Long.valueOf(perm.getId()));
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
        List<AuthRole> roles = authRoleRepository.listByCodes(roleCodes);
        if (roles == null) {
            return List.of();
        }
        return roles.stream().map(r -> Long.valueOf(r.getId())).collect(Collectors.toList());
    }
}
