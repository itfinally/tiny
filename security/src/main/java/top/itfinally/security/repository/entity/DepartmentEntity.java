package top.itfinally.security.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table( name = "v1_security_department", uniqueConstraints =
@UniqueConstraint( name = "department_name", columnNames = "name" ) )
public class DepartmentEntity extends BasicEntity<DepartmentEntity> {
  private String name;
  private String description = "";

  public DepartmentEntity() {
  }

  public DepartmentEntity( String id ) {
    super( id );
  }

  @Column( columnDefinition = "varchar(32) not null" )
  public String getName() {
    return name;
  }

  public DepartmentEntity setName( String name ) {
    this.name = name;
    return this;
  }

  @Column( columnDefinition = "varchar(512) default ''" )
  public String getDescription() {
    return description;
  }

  public DepartmentEntity setDescription( String description ) {
    this.description = description;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    DepartmentEntity that = ( DepartmentEntity ) o;
    return Objects.equals( name, that.name ) &&
        Objects.equals( description, that.description );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), name, description );
  }

  @Override
  public String toString() {
    return "DepartmentEntity{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
