package com.cyan.dataauth.application.userpermission.impl;

import com.cyan.dataauth.application.userpermission.AuthUserPermissionService;
import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import com.cyan.dataauth.domain.role.AuthRole;
import com.cyan.dataauth.domain.role.repository.AuthRoleRepository;
import com.cyan.dataauth.domain.rolepermission.repository.AuthRolePermissionRepository;
import com.cyan.dataauth.domain.userrole.repository.AuthUserRoleRepository;
import com.cyan.dataauth.dto.DataPermissionItemDTO;
import com.cyan.dataauth.dto.MetricPermissionItemDTO;
import com.cyan.dataauth.dto.PageResult;
import com.cyan.dataauth.dto.RoleDTO;
import com.cyan.dataauth.dto.UserPermissionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户权限应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthUserPermissionServiceImpl implements AuthUserPermissionService {

    private final AuthUserRoleRepository authUserRoleRepository;
    private final AuthRoleRepository authRoleRepository;
    private final AuthRolePermissionRepository authRolePermissionRepository;
    private final AuthPermissionRepository authPermissionRepository;

    @Override
    public PageResult<UserPermissionDTO> listUserPermissions(int pageNum, int pageSize, String keyword) {
        List<AuthUserRoleRepository.AuthUserRoleEntry> allUserRoles = authUserRoleRepository.listAll();
        if (allUserRoles == null || allUserRoles.isEmpty()) {
            return new PageResult<>(List.of(), 0);
        }

        List<String> passports = allUserRoles.stream()
                .map(AuthUserRoleRepository.AuthUserRoleEntry::getPassport)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.toLowerCase();
            passports = passports.stream()
                    .filter(p -> p.toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }

        long total = passports.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, passports.size());
        if (fromIndex >= passports.size()) {
            return new PageResult<>(List.of(), total);
        }
        List<String> pagePassports = passports.subList(fromIndex, toIndex);

        Map<String, List<Long>> userRoleIdsMap = allUserRoles.stream()
                .collect(Collectors.groupingBy(
                        AuthUserRoleRepository.AuthUserRoleEntry::getPassport,
                        Collectors.mapping(AuthUserRoleRepository.AuthUserRoleEntry::getRoleId, Collectors.toList())
                ));

        List<Long> allRoleIds = allUserRoles.stream()
                .map(AuthUserRoleRepository.AuthUserRoleEntry::getRoleId)
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

        Map<Long, List<Long>> rolePermIdsMap = authRolePermissionRepository.getPermissionIdsGroupedByRoleId();
        List<AuthPermission> allPermissions = authPermissionRepository.list();
        Map<Long, AuthPermission> permMap = new HashMap<>();
        if (allPermissions != null) {
            for (AuthPermission p : allPermissions) {
                permMap.put(Long.valueOf(p.getId()), p);
            }
        }

        List<UserPermissionDTO> result = new ArrayList<>();
        for (String passport : pagePassports) {
            UserPermissionDTO dto = new UserPermissionDTO();
            dto.setPassport(passport);
            dto.setCnName(passport);

            List<Long> userRoleIds = userRoleIdsMap.getOrDefault(passport, List.of());
            List<RoleDTO> roleDTOs = userRoleIds.stream()
                    .map(roleMap::get)
                    .filter(Objects::nonNull)
                    .map(r -> new RoleDTO(r.getId() != null ? Long.valueOf(r.getId()) : null, r.getName(), r.getCode(), r.getDescription(),
                            r.getStatus(), r.getMaxSecurityLevel(), r.getCreatedBy(), r.getUpdatedBy(),
                            r.getCreatedAt(), r.getUpdatedAt(), r.getDeletedAt(), null))
                    .collect(Collectors.toList());
            dto.setRoles(roleDTOs);

            dto.setDirectPermissions(List.of());

            Set<Long> userPermIds = new HashSet<>();
            for (Long rid : userRoleIds) {
                userPermIds.addAll(rolePermIdsMap.getOrDefault(rid, List.of()));
            }

            List<DataPermissionItemDTO> dataPerms = new ArrayList<>();
            List<MetricPermissionItemDTO> metricPerms = new ArrayList<>();

            for (Long pid : userPermIds) {
                AuthPermission perm = permMap.get(pid);
                if (perm == null) continue;
                String rt = perm.getResourceType();
                if ("DATASOURCE".equals(rt) || "DB".equals(rt) || "TABLE".equals(rt)
                        || "COLUMN".equals(rt) || "ROW".equals(rt)) {
                    dataPerms.add(new DataPermissionItemDTO(rt, perm.getResourceId(), perm.getAction()));
                } else if ("SUBJECT".equals(rt) || "METRIC".equals(rt)
                        || "DIMENSION".equals(rt) || "MODIFIER".equals(rt)) {
                    boolean found = false;
                    for (MetricPermissionItemDTO mp : metricPerms) {
                        if (mp.getResourceId().equals(perm.getResourceId()) && mp.getPermissionType().equals(rt)) {
                            if (!mp.getActions().contains(perm.getAction())) {
                                mp.getActions().add(perm.getAction());
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        List<String> actions = new ArrayList<>();
                        actions.add(perm.getAction());
                        metricPerms.add(new MetricPermissionItemDTO(rt, perm.getResourceId(), actions));
                    }
                }
            }
            dto.setDataPermissions(dataPerms);
            dto.setMetricPermissions(metricPerms);

            result.add(dto);
        }

        return new PageResult<>(result, total);
    }

    @Override
    @Transactional
    public void assignUserRoles(String passport, List<String> roleIds) {
        authUserRoleRepository.removeByPassport(passport);

        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (String roleId : roleIds) {
            if (roleId == null || roleId.isBlank()) {
                continue;
            }
            authUserRoleRepository.save(passport, Long.valueOf(roleId));
        }
    }
}
