package com.cyan.dataauth.adapter.approval.http;

import com.cyan.dataauth.adapter.approval.convert.ApprovalAdapterConvert;
import com.cyan.dataauth.application.approval.AuthApprovalService;
import com.cyan.dataauth.application.approval.bo.ApprovalBO;
import com.cyan.dataauth.cmd.ApprovalActionCmd;
import com.cyan.dataauth.cmd.ApprovalSubmitCmd;
import com.cyan.arch.common.api.Page;
import com.cyan.dataauth.dto.ApprovalDTO;
import com.cyan.dataauth.dto.PageResult;
import com.cyan.arch.common.api.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 审批控制器
 *
 * @author cy.Y
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final AuthApprovalService authApprovalService;
    private final ApprovalAdapterConvert approvalAdapterConvert;

    /**
     * 提交审批
     */
    @PostMapping("/submit")
    public Response<ApprovalDTO> submit(@RequestBody ApprovalSubmitCmd cmd) {
        ApprovalBO bo = authApprovalService.submit(cmd);
        return Response.success(approvalAdapterConvert.toApprovalDTO(bo));
    }

    /**
     * 分页查询
     */
    @GetMapping("/list")
    public Response<PageResult<ApprovalDTO>> list(@RequestParam String passport,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String type,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "20") int pageSize) {
        Page<ApprovalBO> page = authApprovalService.list(passport, status, type, pageNum, pageSize);
        return Response.success(approvalAdapterConvert.toPageResult(page));
    }

    /**
     * 审批操作
     */
    @PostMapping("/{approvalId}/action")
    public Response<ApprovalDTO> action(@PathVariable String approvalId, @RequestBody ApprovalActionCmd cmd) {
        ApprovalBO bo = authApprovalService.action(approvalId, cmd);
        return Response.success(approvalAdapterConvert.toApprovalDTO(bo));
    }
}
