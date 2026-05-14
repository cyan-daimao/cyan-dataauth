package com.cyan.dataauth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据密级枚举
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum SecurityLevel {

    L1("L1", "公开", "所有登录用户可见"),
    L2("L2", "内部", "指定角色成员可见"),
    L3("L3", "敏感", "指定角色可见，需审批"),
    L4("L4", "机密", "最小范围可见，需审批");

    private final String code;
    private final String name;
    private final String desc;

    public static SecurityLevel of(String code) {
        if (code == null) {
            return null;
        }
        for (SecurityLevel value : values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 判断当前密级是否允许访问目标密级
     * L1 <= L2 <= L3 <= L4
     */
    public boolean permits(SecurityLevel target) {
        if (target == null) {
            return true;
        }
        return this.ordinal() >= target.ordinal();
    }
}
