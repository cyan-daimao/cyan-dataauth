package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审批进度DTO
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingApprovalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String approvalId;
    private String approvalType;
    private String resourceName;
    private String status;
    private String currentNode;
    private LocalDateTime submittedAt;
}
