package com.cyan.dataauth.domain.userrole.repository;

import java.util.List;

/**
 * 用户角色关联仓储
 *
 * @author cy.Y
 * @since 1.0.0
 */
public interface AuthUserRoleRepository {

    /**
     * 根据护照查询角色ID列表
     */
    List<Long> listRoleIdsByPassport(String passport);

    /**
     * 根据角色ID查询护照列表
     */
    List<String> listPassportsByRoleId(Long roleId);

    /**
     * 查询所有用户角色关联
     */
    List<AuthUserRoleEntry> listAll();

    /**
     * 根据角色ID和护照统计数量
     */
    long countByRoleIdAndPassport(Long roleId, String passport);

    /**
     * 保存用户角色关联
     */
    void save(String passport, Long roleId);

    /**
     * 根据角色ID和护照删除关联
     */
    void removeByRoleIdAndPassport(Long roleId, String passport);

    /**
     * 根据护照删除所有关联
     */
    void removeByPassport(String passport);

    /**
     * 用户角色关联条目
     */
    class AuthUserRoleEntry {
        private String passport;
        private Long roleId;

        public AuthUserRoleEntry(String passport, Long roleId) {
            this.passport = passport;
            this.roleId = roleId;
        }

        public String getPassport() {
            return passport;
        }

        public void setPassport(String passport) {
            this.passport = passport;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }
    }
}
