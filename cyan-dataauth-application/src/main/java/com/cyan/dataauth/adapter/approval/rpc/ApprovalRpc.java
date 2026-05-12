package com.cyan.dataauth.adapter.approval.rpc;

import com.cyan.arch.common.api.Response;
import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.adapter.approval.convert.ApprovalAdapterConvert;
import com.cyan.dataauth.application.approval.AuthApprovalService;
import com.cyan.dataauth.application.approval.bo.ApprovalBO;
import com.cyan.dataauth.client.ApprovalClient;
import com.cyan.dataauth.cmd.ApprovalActionCmd;
import com.cyan.dataauth.cmd.ApprovalSubmitCmd;
import com.cyan.dataauth.dto.ApprovalDTO;
import com.cyan.dataauth.dto.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批 RPC 服务（供内部服务调用）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/rpc/v1/approval")
@RequiredArgsConstructor
public class ApprovalRpc implements ApprovalClient {

    private final AuthApprovalService authApprovalService;
    private final ApprovalAdapterConvert approvalAdapterConvert;

    @Override
    public Response<ApprovalDTO> submit(ApprovalSubmitCmd cmd) {
        ApprovalBO bo = authApprovalService.submit(cmd);
        return Response.success(approvalAdapterConvert.toApprovalDTO(bo));
    }

    @Override
    public Response<PageResult<ApprovalDTO>> list(String passport, String status, String type,
                                                   int pageNum, int pageSize) {
        Page<ApprovalBO> page = authApprovalService.list(passport, status, type, pageNum, pageSize);
        return Response.success(approvalAdapterConvert.toPageResult(page));
    }

    @Override
    public Response<ApprovalDTO> action(String approvalId, ApprovalActionCmd cmd) {
        ApprovalBO bo = authApprovalService.action(approvalId, cmd);
        return Response.success(approvalAdapterConvert.toApprovalDTO(bo));
    }
}
