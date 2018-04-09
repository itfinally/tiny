package top.itfinally.security.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table( name = "v1_security_user_department", uniqueConstraints =
@UniqueConstraint( name = "user_department", columnNames = { "user_security_id", "department_id" } ) )
public class UserDepartmentEntity extends BasicEntity<UserDepartmentEntity> {
  private UserSecurityEntity userSecurity;
  private DepartmentEntity department;

  @JoinColumn( name = "user_security_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public UserSecurityEntity getUserSecurity() {
    return userSecurity;
  }

  public UserDepartmentEntity setUserSecurity( UserSecurityEntity userSecurity ) {
    this.userSecurity = userSecurity;
    return this;
  }

  @JoinColumn( name = "department_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public DepartmentEntity getDepartment() {
    return department;
  }

  public UserDepartmentEntity setDepartment( DepartmentEntity department ) {
    this.department = department;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    UserDepartmentEntity that = ( UserDepartmentEntity ) o;
    return Objects.equals( userSecurity, that.userSecurity ) &&
        Objects.equals( department, that.department );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), userSecurity, department );
  }

  @Override
  public String toString() {
    return "UserDepartmentEntity{" +
        "userSecurity=" + userSecurity +
        ", department=" + department +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
