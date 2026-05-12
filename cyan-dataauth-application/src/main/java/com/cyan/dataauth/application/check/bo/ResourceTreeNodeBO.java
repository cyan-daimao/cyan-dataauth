package com.cyan.dataauth.application.check.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 资源树节点业务对象
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceTreeNodeBO {

    /**
     * 节点id
     */
    private String id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点类型
     */
    private String type;

    /**
     * 权限操作
     */
    private String permission;

    /**
     * 子节点
     */
    private List<ResourceTreeNodeBO> children;
}
