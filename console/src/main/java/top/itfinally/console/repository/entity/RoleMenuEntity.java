package top.itfinally.console.repository.entity;

import top.itfinally.core.repository.BasicEntity;
import top.itfinally.security.repository.entity.RoleEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table( name = "v1_role_menu", uniqueConstraints =
@UniqueConstraint( name = "role_menu", columnNames = { "role_id", "menu_id" } ) )
public class RoleMenuEntity extends BasicEntity<RoleMenuEntity> {
  private RoleEntity role;
  private MenuItemEntity menuItem;

  @JoinColumn( name = "role_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public RoleEntity getRole() {
    return role;
  }

  public RoleMenuEntity setRole( RoleEntity role ) {
    this.role = role;
    return this;
  }

  @JoinColumn( name = "menu_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public MenuItemEntity getMenuItem() {
    return menuItem;
  }

  public RoleMenuEntity setMenuItem( MenuItemEntity menuItem ) {
    this.menuItem = menuItem;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    RoleMenuEntity that = ( RoleMenuEntity ) o;
    return Objects.equals( role, that.role ) &&
        Objects.equals( menuItem, that.menuItem );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), role, menuItem );
  }

  @Override
  public String toString() {
    return "RoleMenuEntity{" +
        "role=" + role +
        ", menuItem=" + menuItem +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
