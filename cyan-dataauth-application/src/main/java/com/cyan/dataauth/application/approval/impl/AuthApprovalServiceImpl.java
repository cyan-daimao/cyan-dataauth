package com.cyan.dataauth.application.approval.impl;

import com.cyan.dataauth.application.approval.AuthApprovalService;
import com.cyan.dataauth.application.approval.bo.ApprovalBO;
import com.cyan.dataauth.application.approval.convert.AuthApprovalAppConvert;
import com.cyan.dataauth.cmd.ApprovalActionCmd;
import com.cyan.dataauth.cmd.ApprovalSubmitCmd;
import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.domain.approval.AuthApproval;
import com.cyan.dataauth.domain.approval.query.ApprovalPageQuery;
import com.cyan.dataauth.domain.approval.repository.AuthApprovalRepository;
import com.cyan.dataauth.domain.permission.AuthPermission;
import com.cyan.dataauth.domain.permission.repository.AuthPermissionRepository;
import com.cyan.dataauth.domain.role.AuthRole;
import com.cyan.dataauth.domain.role.repository.AuthRoleRepository;
import com.cyan.dataauth.infra.persistence.userrole.dos.AuthUserRoleDO;
import com.cyan.dataauth.infra.persistence.userrole.mappers.AuthUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批应用服务实现
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApprovalServiceImpl implements AuthApprovalService {

    private final AuthApprovalRepository authApprovalRepository;
    private final AuthApprovalAppConvert authApprovalAppConvert;
    private final AuthRoleRepository authRoleRepository;
    private final AuthPermissionRepository authPermissionRepository;
    private final AuthUserRoleMapper authUserRoleMapper;

    @Override
    @Transactional
    public ApprovalBO submit(ApprovalSubmitCmd cmd) {
        AuthApproval approval = authApprovalAppConvert.toAuthApproval(cmd);
        approval = approval.submit(authApprovalRepository);
        return authApprovalAppConvert.toApprovalBO(approval);
    }

    @Override
    public Page<ApprovalBO> list(String passport, String status, String type, long current, long size) {
        ApprovalPageQuery query = new ApprovalPageQuery(passport, status, type, current, size);
        Page<AuthApproval> page = authApprovalRepository.page(query);
        return new Page<>(authApprovalAppConvert.toApprovalBOList(page.getData()),
                page.getCurrent(), page.getSize(), page.getTotal());
    }

    @Override
    @Transactional
    public ApprovalBO action(String approvalId, ApprovalActionCmd cmd) {
        AuthApproval approval = new AuthApproval().setApprovalId(approvalId);
        if ("APPROVE".equals(cmd.getAction())) {
            approval = approval.approve(cmd.getOperatorPassport(), cmd.getComment(), authApprovalRepository);
            // Round1: ready — 审批通过后自动授权
            grantPermission(approval);
        } else if ("REJECT".equals(cmd.getAction())) {
            approval = approval.reject(cmd.getOperatorPassport(), cmd.getComment(), authApprovalRepository);
        } else {
            throw new IllegalArgumentException("不支持的审批操作: " + cmd.getAction());
        }
        return authApprovalAppConvert.toApprovalBO(approval);
    }

    /**
     * 审批通过后自动授权
     */
    private void grantPermission(AuthApproval approval) {
        String applicantPassport = approval.getApplicantPassport();
        String resourceType = approval.getResourceType();
        String resourceId = approval.getResourceId();
        String action = approval.getAction();
        String approvalType = approval.getApprovalType();

        try {
            // 角色变更申请：直接关联用户和角色
            if ("ROLE_CHANGE".equals(approvalType) || "ROLE".equals(resourceType)) {
                AuthRole role = authRoleRepository.getByCode(resourceId);
                if (role != null) {
                    grantUserRole(applicantPassport, role.getId());
                    log.info("审批通过自动授权: 用户={} 关联角色={}", applicantPassport, role.getCode());
                } else {
                    log.warn("审批通过自动授权失败: 角色不存在 code={}", resourceId);
                }
                return;
            }

            // 具体资源权限申请
            // 1. 查找或创建个人权限角色
            String personalRoleCode = "PERSONAL_" + applicantPassport;
            AuthRole personalRole = authRoleRepository.getByCode(personalRoleCode);
            if (personalRole == null) {
                personalRole = new AuthRole()
                        .setName("个人权限-" + applicantPassport)
                        .setCode(personalRoleCode)
                        .setDescription("自动创建")
                        .setStatus(1);
                personalRole = authRoleRepository.save(personalRole);
            }

            // 2. 查找或创建权限项
            AuthPermission permission = authPermissionRepository.getByResource(resourceType, resourceId, action);
            if (permission == null) {
                permission = new AuthPermission()
                        .setResourceType(resourceType)
                        .setResourceId(resourceId)
                        .setAction(action)
                        .setDescription("审批自动授权");
                permission = authPermissionRepository.save(permission);
            }

            // 3. 关联角色和权限
            authRoleRepository.addPermission(personalRole.getId(), permission.getId());

            // 4. 关联用户和角色
            grantUserRole(applicantPassport, personalRole.getId());

            log.info("审批通过自动授权成功: 用户={} 资源={}/{} 操作={}",
                    applicantPassport, resourceType, resourceId, action);
        } catch (Exception e) {
            log.error("审批通过自动授权异常: 用户={} 资源={}/{} 操作={}",
                    applicantPassport, resourceType, resourceId, action, e);
            // 不抛出异常，避免影响审批状态更新
        }
    }

    /**
     * 关联用户和角色（如果不存在）
     */
    private void grantUserRole(String passport, String roleId) {
        if (passport == null || roleId == null) {
            return;
        }
        // 查询是否已存在
        List<Long> roleIds = authUserRoleMapper.selectRoleIdsByPassport(passport);
        if (roleIds != null && roleIds.contains(Long.valueOf(roleId))) {
            return;
        }
        AuthUserRoleDO ur = new AuthUserRoleDO();
        ur.setPassport(passport);
        ur.setRoleId(Long.valueOf(roleId));
        ur.setCreatedAt(LocalDateTime.now());
        authUserRoleMapper.insert(ur);
    }
}
