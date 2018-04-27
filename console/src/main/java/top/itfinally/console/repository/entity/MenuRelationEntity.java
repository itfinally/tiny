package top.itfinally.console.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table( name = "v1_console_menu_relation", uniqueConstraints =
@UniqueConstraint( name = "parent_child", columnNames = { "parent_id", "child_id" } ) )
public class MenuRelationEntity extends BasicEntity<MenuRelationEntity> {
  private int gap;
  private MenuItemEntity child;
  private MenuItemEntity parent;

  @Column
  public int getGap() {
    return gap;
  }

  public MenuRelationEntity setGap( int gap ) {
    this.gap = gap;
    return this;
  }

  @JoinColumn( name = "child_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public MenuItemEntity getChild() {
    return child;
  }

  public MenuRelationEntity setChild( MenuItemEntity child ) {
    this.child = child;
    return this;
  }

  @JoinColumn( name = "parent_id", columnDefinition = "varchar(64) not null",
      foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT ) )
  @OneToOne
  public MenuItemEntity getParent() {
    return parent;
  }

  public MenuRelationEntity setParent( MenuItemEntity parent ) {
    this.parent = parent;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    MenuRelationEntity that = ( MenuRelationEntity ) o;
    return gap == that.gap &&
        Objects.equals( child, that.child ) &&
        Objects.equals( parent, that.parent );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), gap, child, parent );
  }

  @Override
  public String toString() {
    return "MenuRelationEntity{" +
        "gap=" + gap +
        ", child=" + child +
        ", parent=" + parent +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
