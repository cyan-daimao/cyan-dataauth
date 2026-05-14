-- cyan-dataauth 数据库初始化脚本
-- 数据库: cyan_dataauth

-- 角色表
CREATE TABLE IF NOT EXISTS auth_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '角色名称',
    code VARCHAR(100) NOT NULL COMMENT '角色编码',
    description VARCHAR(500) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    max_security_level VARCHAR(8) DEFAULT 'L1' COMMENT '最高可访问密级: L1/L2/L3/L4',
    created_by VARCHAR(100) COMMENT '创建人',
    updated_by VARCHAR(100) COMMENT '更新人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间（逻辑删除）',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限项表（功能权限 + 数据权限 + 指标权限统一存储）
CREATE TABLE IF NOT EXISTS auth_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型: MENU/BUTTON/API/DATASOURCE/DB/TABLE/ROW/COLUMN/SUBJECT/METRIC/DIMENSION/MODIFIER',
    resource_id VARCHAR(500) NOT NULL COMMENT '资源标识',
    action VARCHAR(50) NOT NULL COMMENT '操作: VIEW/USE/EDIT/EXECUTE/EXPORT/SELECT/INSERT/UPDATE/DELETE/ALL',
    description VARCHAR(500) COMMENT '权限描述',
    created_by VARCHAR(100) COMMENT '创建人',
    updated_by VARCHAR(100) COMMENT '更新人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间（逻辑删除）',
    UNIQUE KEY uk_resource (resource_type, resource_id, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限项表';

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS auth_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限项ID',
    created_by VARCHAR(100) COMMENT '创建人',
    updated_by VARCHAR(100) COMMENT '更新人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间（逻辑删除）',
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS auth_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    passport VARCHAR(100) NOT NULL COMMENT '用户账号',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_by VARCHAR(100) COMMENT '创建人',
    updated_by VARCHAR(100) COMMENT '更新人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间（逻辑删除）',
    UNIQUE KEY uk_user_role (passport, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- 审批单表
CREATE TABLE IF NOT EXISTS auth_approval (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    approval_id VARCHAR(100) NOT NULL COMMENT '审批单号',
    applicant_passport VARCHAR(100) NOT NULL COMMENT '申请人账号',
    approval_type VARCHAR(50) NOT NULL COMMENT '审批类型: METRIC_PERMISSION/DATA_PERMISSION/ROLE_CHANGE',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型',
    resource_id VARCHAR(500) NOT NULL COMMENT '资源标识',
    action VARCHAR(50) NOT NULL COMMENT '申请的操作权限',
    reason VARCHAR(2000) NOT NULL COMMENT '申请理由',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED',
    current_node VARCHAR(200) COMMENT '当前审批节点',
    expire_days INT COMMENT '权限有效期（天），空表示永久',
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    handled_at DATETIME COMMENT '处理时间',
    operator_passport VARCHAR(100) COMMENT '审批人账号',
    comment VARCHAR(2000) COMMENT '审批意见',
    created_by VARCHAR(100) COMMENT '创建人',
    updated_by VARCHAR(100) COMMENT '更新人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间（逻辑删除）',
    UNIQUE KEY uk_approval_id (approval_id),
    INDEX idx_applicant (applicant_passport),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批单表';

-- 审计日志表
CREATE TABLE IF NOT EXISTS auth_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    log_id VARCHAR(100) NOT NULL COMMENT '日志ID',
    user_id VARCHAR(100) NOT NULL COMMENT '用户账号',
    action VARCHAR(50) NOT NULL COMMENT '操作类型: LOGIN/LOGOUT/SQL_EXECUTE/PERMISSION_CHANGE/APPROVAL',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id VARCHAR(500) COMMENT '资源标识',
    original_sql TEXT COMMENT '原始SQL',
    rewritten_sql TEXT COMMENT '改写后SQL',
    ip VARCHAR(64) COMMENT 'IP地址',
    cost_time_ms BIGINT COMMENT '耗时(ms)',
    risk_level VARCHAR(20) DEFAULT 'LOW' COMMENT '风险等级: LOW/MEDIUM/HIGH',
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    created_by VARCHAR(100) COMMENT '创建人',
    updated_by VARCHAR(100) COMMENT '更新人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间（逻辑删除）',
    UNIQUE KEY uk_log_id (log_id),
    INDEX idx_user (user_id),
    INDEX idx_action (action),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';
