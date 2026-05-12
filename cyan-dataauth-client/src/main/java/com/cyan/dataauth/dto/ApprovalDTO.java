package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String approvalId;
    private String applicantPassport;
    private String approvalType;
    private String resourceType;
    private String resourceId;
    private String action;
    private String reason;
    private String status;
    private String currentNode;
    private Integer expireDays;
    private LocalDateTime submittedAt;
    private LocalDateTime handledAt;
    private String operatorPassport;
    private String comment;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
