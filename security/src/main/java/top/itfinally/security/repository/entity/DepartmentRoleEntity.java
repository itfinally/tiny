package top.itfinally.security.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.*;

@Entity
@Table( name = "v1_security_department_role", uniqueConstraints =
@UniqueConstraint( name = "department_role", columnNames = { "department_id", "role_id" } ) )
public class DepartmentRoleEntity extends BasicEntity<DepartmentRoleEntity> {
  private DepartmentEntity department;
  private RoleEntity role;

  @JoinColumn( name = "department_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public DepartmentEntity getDepartment() {
    return department;
  }

  public DepartmentRoleEntity setDepartment( DepartmentEntity department ) {
    this.department = department;
    return this;
  }

  @JoinColumn( name = "role_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public RoleEntity getRole() {
    return role;
  }

  public DepartmentRoleEntity setRole( RoleEntity role ) {
    this.role = role;
    return this;
  }
}
