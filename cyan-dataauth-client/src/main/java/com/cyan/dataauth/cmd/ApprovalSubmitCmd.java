package com.cyan.dataauth.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalSubmitCmd implements Serializable {

    private static final long serialVersionUID = 1L;

    private String applicantPassport;
    private String approvalType;
    private String resourceType;
    private String resourceId;
    private String action;
    private String reason;
    private Integer expireDays;
}
