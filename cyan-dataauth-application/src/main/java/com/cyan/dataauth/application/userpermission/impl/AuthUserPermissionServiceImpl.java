package com.cyan.dataauth.application.userpermission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyan.dataauth.application.userpermission.AuthUserPermissionService;
import com.cyan.dataauth.dto.*;
import com.cyan.dataauth.infra.persistence.permission.dos.AuthPermissionDO;
import com.cyan.dataauth.infra.persistence.permission.mappers.AuthPermissionMapper;
import com.cyan.dataauth.infra.persistence.role.dos.AuthRoleDO;
import com.cyan.dataauth.infra.persistence.role.mappers.AuthRoleMapper;
import com.cyan.dataauth.infra.persistence.rolepermission.dos.AuthRolePermissionDO;
import com.cyan.dataauth.infra.persistence.rolepermission.mappers.AuthRolePermissionMapper;
import com.cyan.dataauth.infra.persistence.userrole.dos.AuthUserRoleDO;
import com.cyan.dataauth.infra.persistence.userrole.mappers.AuthUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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

    private final AuthUserRoleMapper authUserRoleMapper;
    private final AuthRoleMapper authRoleMapper;
    private final AuthRolePermissionMapper authRolePermissionMapper;
    private final AuthPermissionMapper authPermissionMapper;

    @Override
    public PageResult<UserPermissionDTO> listUserPermissions(int pageNum, int pageSize, String keyword) {
        // 查询所有有角色的用户（去重）
        List<AuthUserRoleDO> allUserRoles = authUserRoleMapper.selectList(null);
        if (allUserRoles == null || allUserRoles.isEmpty()) {
            return new PageResult<>(List.of(), 0);
        }

        // 去重并按passport排序
        List<String> passports = allUserRoles.stream()
                .map(AuthUserRoleDO::getPassport)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 关键词过滤（按passport模糊匹配，因无员工信息）
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

        // 构建用户 -> 角色ID列表映射
        Map<String, List<Long>> userRoleIdsMap = allUserRoles.stream()
                .collect(Collectors.groupingBy(
                        AuthUserRoleDO::getPassport,
                        Collectors.mapping(AuthUserRoleDO::getRoleId, Collectors.toList())
                ));

        // 查询所有角色信息
        List<Long> allRoleIds = allUserRoles.stream()
                .map(AuthUserRoleDO::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, AuthRoleDO> roleMap = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            List<AuthRoleDO> roles = authRoleMapper.selectBatchIds(allRoleIds);
            if (roles != null) {
                for (AuthRoleDO r : roles) {
                    roleMap.put(r.getId(), r);
                }
            }
        }

        // 查询所有角色权限关联
        List<AuthRolePermissionDO> allRolePerms = authRolePermissionMapper.selectList(null);
        Map<Long, List<Long>> rolePermIdsMap = allRolePerms.stream()
                .collect(Collectors.groupingBy(
                        AuthRolePermissionDO::getRoleId,
                        Collectors.mapping(AuthRolePermissionDO::getPermissionId, Collectors.toList())
                ));

        // 查询所有权限项
        List<AuthPermissionDO> allPermissions = authPermissionMapper.selectList(null);
        Map<Long, AuthPermissionDO> permMap = new HashMap<>();
        if (allPermissions != null) {
            for (AuthPermissionDO p : allPermissions) {
                permMap.put(p.getId(), p);
            }
        }

        List<UserPermissionDTO> result = new ArrayList<>();
        for (String passport : pagePassports) {
            UserPermissionDTO dto = new UserPermissionDTO();
            dto.setPassport(passport);
            dto.setCnName(passport);

            // 角色列表
            List<Long> userRoleIds = userRoleIdsMap.getOrDefault(passport, List.of());
            List<RoleDTO> roleDTOs = userRoleIds.stream()
                    .map(roleMap::get)
                    .filter(Objects::nonNull)
                    .map(r -> new RoleDTO(r.getId(), r.getName(), r.getCode(), r.getDescription(),
                            r.getStatus(), r.getMaxSecurityLevel(), r.getCreatedBy(), r.getUpdatedBy(),
                            r.getCreatedAt(), r.getUpdatedAt(), r.getDeletedAt(), null))
                    .collect(Collectors.toList());
            dto.setRoles(roleDTOs);

            // 直接权限（本期留空）
            dto.setDirectPermissions(List.of());

            // 聚合权限项
            Set<Long> userPermIds = new HashSet<>();
            for (Long rid : userRoleIds) {
                userPermIds.addAll(rolePermIdsMap.getOrDefault(rid, List.of()));
            }

            List<DataPermissionItemDTO> dataPerms = new ArrayList<>();
            List<MetricPermissionItemDTO> metricPerms = new ArrayList<>();

            for (Long pid : userPermIds) {
                AuthPermissionDO perm = permMap.get(pid);
                if (perm == null) continue;
                String rt = perm.getResourceType();
                if ("DATASOURCE".equals(rt) || "DB".equals(rt) || "TABLE".equals(rt)
                        || "COLUMN".equals(rt) || "ROW".equals(rt)) {
                    dataPerms.add(new DataPermissionItemDTO(rt, perm.getResourceId(), perm.getAction()));
                } else if ("SUBJECT".equals(rt) || "METRIC".equals(rt)
                        || "DIMENSION".equals(rt) || "MODIFIER".equals(rt)) {
                    // 按resourceId聚合actions
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
        // 先删除该用户的所有角色关联
        LambdaQueryWrapper<AuthUserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRoleDO::getPassport, passport);
        authUserRoleMapper.delete(wrapper);

        // 批量插入新的关联
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (String roleId : roleIds) {
            if (roleId == null || roleId.isBlank()) {
                continue;
            }
            AuthUserRoleDO ur = new AuthUserRoleDO();
            ur.setPassport(passport);
            ur.setRoleId(Long.valueOf(roleId));
            ur.setCreatedAt(LocalDateTime.now());
            authUserRoleMapper.insert(ur);
        }
    }
}
