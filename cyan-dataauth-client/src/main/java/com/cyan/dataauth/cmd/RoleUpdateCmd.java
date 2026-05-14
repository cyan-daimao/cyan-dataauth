package com.cyan.dataauth.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateCmd implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Integer status;
    private String maxSecurityLevel;
}
