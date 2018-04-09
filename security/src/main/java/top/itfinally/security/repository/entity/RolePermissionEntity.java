package top.itfinally.security.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table( name = "v1_security_role_permission", uniqueConstraints =
@UniqueConstraint( name = "role_permission", columnNames = { "role_id", "permission_id" } ) )
public class RolePermissionEntity extends BasicEntity<RolePermissionEntity> {
  private RoleEntity role;
  private PermissionEntity permission;

  @JoinColumn( name = "role_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public RoleEntity getRole() {
    return role;
  }

  public RolePermissionEntity setRole( RoleEntity role ) {
    this.role = role;
    return this;
  }

  @JoinColumn( name = "permission_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public PermissionEntity getPermission() {
    return permission;
  }

  public RolePermissionEntity setPermission( PermissionEntity permission ) {
    this.permission = permission;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    RolePermissionEntity that = ( RolePermissionEntity ) o;
    return Objects.equals( role, that.role ) &&
        Objects.equals( permission, that.permission );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), role, permission );
  }

  @Override
  public String toString() {
    return "RolePermissionEntity{" +
        "role=" + role +
        ", permission=" + permission +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
