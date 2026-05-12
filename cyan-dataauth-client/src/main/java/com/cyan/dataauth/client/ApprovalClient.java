package com.cyan.dataauth.client;

import com.cyan.arch.common.api.Response;
import com.cyan.dataauth.cmd.ApprovalActionCmd;
import com.cyan.dataauth.cmd.ApprovalSubmitCmd;
import com.cyan.dataauth.dto.ApprovalDTO;
import com.cyan.dataauth.dto.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 审批 RPC 客户端（服务间内部调用）
 *
 * @author cy.Y
 * @since 1.0.0
 */
@FeignClient(name = "cyan-dataauth", contextId = "cyan-dataauth.approval", path = "/rpc/v1/approval")
public interface ApprovalClient {

    /**
     * 提交审批
     */
    @PostMapping("/submit")
    Response<ApprovalDTO> submit(@RequestBody ApprovalSubmitCmd cmd);

    /**
     * 分页查询
     */
    @GetMapping("/list")
    Response<PageResult<ApprovalDTO>> list(@RequestParam("passport") String passport,
                                           @RequestParam(value = "status", required = false) String status,
                                           @RequestParam(value = "type", required = false) String type,
                                           @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "20") int pageSize);

    /**
     * 审批操作
     */
    @PostMapping("/{approvalId}/action")
    Response<ApprovalDTO> action(@PathVariable("approvalId") String approvalId,
                                 @RequestBody ApprovalActionCmd cmd);
}
