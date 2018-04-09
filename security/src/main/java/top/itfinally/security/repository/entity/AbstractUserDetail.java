package top.itfinally.security.repository.entity;

import top.itfinally.core.repository.BasicEntity;

public abstract class AbstractUserDetail<Entity extends BasicEntity<Entity>> extends BasicEntity<Entity> {
  public abstract String getPassword();

  public abstract String getUsername();

  public abstract String getUserSecurityId();
}
