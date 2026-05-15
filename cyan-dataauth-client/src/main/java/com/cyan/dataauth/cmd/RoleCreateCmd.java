package com.cyan.dataauth.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateCmd implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String code;
    private String description;
    private String maxSecurityLevel;
    private List<String> functionPermissionKeys;
}
