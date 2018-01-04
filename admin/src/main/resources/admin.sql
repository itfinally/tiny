-- default base entity field
# id varchar( 64 ) primary key,
#   create_time long not null,
#   update_time long not null,
#   delete_time long,
#   status int( 3 ) default 1,

CREATE TABLE v1_user_details (
  id           VARCHAR(64) PRIMARY KEY,
  create_time  LONG                    NOT NULL,
  update_time  LONG                    NOT NULL,
  delete_time  LONG,
  status       INT(3)      DEFAULT 1,

  account      VARCHAR(128) UNIQUE KEY NOT NULL,
  password     VARCHAR(128)            NOT NULL,
  nickname     VARCHAR(256)            NOT NULL,
  authority_id VARCHAR(64) DEFAULT ''
)
  DEFAULT CHARSET utf8mb4;

CREATE TABLE menu_item (
  id          VARCHAR(64) PRIMARY KEY,
  create_time LONG       NOT NULL,
  update_time LONG       NOT NULL,
  delete_time LONG,
  status      INT(3) DEFAULT 1,

  name        VARCHAR(64) UNIQUE KEY,
  is_root     TINYINT(1) NOT NULL,
  is_leaf     TINYINT(1) NOT NULL
)
  DEFAULT CHARSET utf8mb4;

CREATE TABLE menu_relationship (
  id          VARCHAR(64) PRIMARY KEY,
  create_time LONG        NOT NULL,
  update_time LONG        NOT NULL,
  delete_time LONG,
  status      INT(3) DEFAULT 1,

  parent_id   VARCHAR(64) NOT NULL,
  child_id    VARCHAR(64) NOT NULL,
  gap         INT(11)     NOT NULL,

  UNIQUE KEY relationship(parent_id, child_id, gap)
)
  DEFAULT CHARSET utf8mb4;

CREATE TABLE menu_scope (
  id           VARCHAR(64) PRIMARY KEY,
  create_time  LONG        NOT NULL,
  update_time  LONG        NOT NULL,
  delete_time  LONG,
  status       INT(3) DEFAULT 1,

  menu_item_id VARCHAR(64) NOT NULL,
  role_id      VARCHAR(64) NOT NULL,

  UNIQUE KEY role_menu_item(menu_item_id, role_id)
)
  DEFAULT CHARSET utf8mb4;