package top.itfinally.security.web.vo;

import top.itfinally.core.web.BasicVoBean;
import top.itfinally.security.repository.entity.DepartmentEntity;

public class DepartmentVoBean extends BasicVoBean<DepartmentVoBean, DepartmentEntity> {
  private String name;
  private String description;

  public DepartmentVoBean() {
  }

  public DepartmentVoBean( DepartmentEntity entity ) {
    super( entity );

    name = entity.getName();
    description = entity.getDescription();
  }

  public String getName() {
    return name;
  }

  public DepartmentVoBean setName( String name ) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public DepartmentVoBean setDescription( String description ) {
    this.description = description;
    return this;
  }
}
