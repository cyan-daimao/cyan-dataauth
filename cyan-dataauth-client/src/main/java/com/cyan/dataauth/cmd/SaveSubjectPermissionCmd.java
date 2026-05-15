package com.cyan.dataauth.cmd;

import com.cyan.dataauth.dto.PermissionTargetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 保存主题域权限配置命令
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveSubjectPermissionCmd {

    private String subjectCode;
    private List<String> actions;
    private List<PermissionTargetDTO> targets;
}
