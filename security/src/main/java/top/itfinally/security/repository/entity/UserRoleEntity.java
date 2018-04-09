package top.itfinally.security.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table( name = "v1_security_user_role", uniqueConstraints =
@UniqueConstraint( name = "user_role", columnNames = { "user_security_id", "role_id" } ) )
public class UserRoleEntity extends BasicEntity<UserRoleEntity> {
  private UserSecurityEntity userSecurity;
  private RoleEntity role;

  @JoinColumn( name = "user_security_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public UserSecurityEntity getUserSecurity() {
    return userSecurity;
  }

  public UserRoleEntity setUserSecurity( UserSecurityEntity userSecurity ) {
    this.userSecurity = userSecurity;
    return this;
  }

  @JoinColumn( name = "role_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public RoleEntity getRole() {
    return role;
  }

  public UserRoleEntity setRole( RoleEntity role ) {
    this.role = role;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    UserRoleEntity that = ( UserRoleEntity ) o;
    return Objects.equals( userSecurity, that.userSecurity ) &&
        Objects.equals( role, that.role );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), userSecurity, role );
  }

  @Override
  public String toString() {
    return "UserRoleEntity{" +
        "userSecurity=" + userSecurity +
        ", role=" + role +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
