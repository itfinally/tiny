package top.itfinally.security.web.vo;

import top.itfinally.core.web.BasicVoBean;
import top.itfinally.security.repository.entity.RoleEntity;

public class RoleVoBean extends BasicVoBean<RoleVoBean, RoleEntity> {
  private String name;
  private String description;

  private int priority;

  public RoleVoBean() {
  }

  public RoleVoBean( RoleEntity entity ) {
    super( entity );

    name = entity.getName();
    priority = entity.getPriority();
    description = entity.getDescription();
  }

  public String getName() {
    return name;
  }

  public RoleVoBean setName( String name ) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public RoleVoBean setDescription( String description ) {
    this.description = description;
    return this;
  }

  public int getPriority() {
    return priority;
  }

  public RoleVoBean setPriority( int priority ) {
    this.priority = priority;
    return this;
  }
}
