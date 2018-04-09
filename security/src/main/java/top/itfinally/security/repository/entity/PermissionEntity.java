package top.itfinally.security.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

@Entity
@Table( name = "v1_security_permission", uniqueConstraints =
@UniqueConstraint( name = "permission_name", columnNames = "name" ) )
public class PermissionEntity extends BasicEntity<PermissionEntity> {
  private String name;
  private String description = "";

  public PermissionEntity() {
  }

  public PermissionEntity( String id ) {
    super( id );
  }

  @Column( columnDefinition = "varchar(32) not null" )
  public String getName() {
    return name;
  }

  public PermissionEntity setName( String name ) {
    this.name = name;
    return this;
  }

  @Column( columnDefinition = "varchar(512) default ''" )
  public String getDescription() {
    return description;
  }

  public PermissionEntity setDescription( String description ) {
    this.description = description;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    PermissionEntity that = ( PermissionEntity ) o;
    return Objects.equals( name, that.name ) &&
        Objects.equals( description, that.description );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), name, description );
  }

  @Override
  public String toString() {
    return "PermissionEntity{" +
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
