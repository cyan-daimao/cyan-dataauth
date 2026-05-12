package com.cyan.dataauth.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalActionCmd implements Serializable {

    private static final long serialVersionUID = 1L;

    private String operatorPassport;
    private String action;
    private String comment;
    private String transferTo;
}
