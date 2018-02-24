-- default base entity field
-- id varchar( 64 ) primary key,
--   create_time long not null,
--   update_time long not null,
--   delete_time long,
--   status int( 3 ) default 1,

-- 通过 security_permission 绑定资源 ( 通过 security 的注解进行绑定, 只能通过修改代码修改访问权限限制 )
-- 然后提供 authority - permission 的绑定
-- 以及 user - authority 的绑定

-- security_user 作为 user 在 security 方面的一个补集
-- 与 user 是一对一关系

CREATE TABLE v1_security_user_details (
  id                         VARCHAR(64) PRIMARY KEY,
  create_time                LONG NOT NULL,
  update_time                LONG NOT NULL,
  delete_time                LONG,
  status                     INT(3)     DEFAULT 1,

  is_non_expired             TINYINT(1) DEFAULT 0,
  is_non_locked              TINYINT(1) DEFAULT 0,
  is_credentials_non_expired TINYINT(1) DEFAULT 0,
  is_enable                  TINYINT(1) DEFAULT 0
)
  DEFAULT CHARSET utf8mb4;

-- user 与 role 的中间表, 记录 user 与 role 的关系
CREATE TABLE v1_security_user_role (
  id           VARCHAR(64) PRIMARY KEY,
  create_time  LONG        NOT NULL,
  update_time  LONG        NOT NULL,
  delete_time  LONG,
  status       INT(3) DEFAULT 1,

  -- foreign key for user id
  authority_id VARCHAR(64) NOT NULL,

  -- foreign key for authority id
  role_id      VARCHAR(64) NOT NULL,

  UNIQUE KEY user_authority(authority_id, role_id)
)
  DEFAULT CHARSET utf8mb4;

-- role 表, 记录角色的元数据
CREATE TABLE v1_security_role (
  id          VARCHAR(64) PRIMARY KEY,
  create_time LONG                NOT NULL,
  update_time LONG                NOT NULL,
  delete_time LONG,
  status      INT(3) DEFAULT 1,

  name        VARCHAR(128) UNIQUE NOT NULL,
  description VARCHAR(256) UNIQUE NOT NULL,
  priority    INT(11)             NOT NULL
)
  DEFAULT CHARSET utf8mb4;

-- role 与 permission 的中间表, 记录 role 拥有的权限
CREATE TABLE v1_security_role_permission (
  id            VARCHAR(64) PRIMARY KEY,
  create_time   LONG        NOT NULL,
  update_time   LONG        NOT NULL,
  delete_time   LONG,
  status        INT(3) DEFAULT 1,

  role_id       VARCHAR(64) NOT NULL,
  permission_id VARCHAR(64) NOT NULL,

  UNIQUE KEY authority_permission(role_id, permission_id)
)
  DEFAULT CHARSET utf8mb4;

-- permission 表, 记录权限的元数据
CREATE TABLE v1_security_permission (
  id          VARCHAR(64) PRIMARY KEY,
  create_time LONG                NOT NULL,
  update_time LONG                NOT NULL,
  delete_time LONG,
  status      INT(3) DEFAULT 1,

  name        VARCHAR(128) UNIQUE NOT NULL,
  description VARCHAR(256) UNIQUE NOT NULL
)
  DEFAULT CHARSET utf8mb4;