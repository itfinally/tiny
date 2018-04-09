package top.itfinally.console.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table( name = "v1_menu_item" )
public class MenuItemEntity extends BasicEntity<MenuItemEntity> {
  private String name;
  private String path = "";
  private boolean isRoot;
  private boolean isLeaf;

  public MenuItemEntity() {
  }

  public MenuItemEntity( String id ) {
    super( id );
  }

  @Column( columnDefinition = "varchar(32) not null" )
  public String getName() {
    return name;
  }

  public MenuItemEntity setName( String name ) {
    this.name = name;
    return this;
  }

  @Column( columnDefinition = "varchar(256) default ''" )
  public String getPath() {
    return path;
  }

  public MenuItemEntity setPath( String path ) {
    this.path = path;
    return this;
  }

  @Column( name = "is_root", columnDefinition = "int(3) not null" )
  public boolean isRoot() {
    return isRoot;
  }

  public MenuItemEntity setRoot( boolean root ) {
    isRoot = root;
    return this;
  }

  @Column( name = "is_leaf", columnDefinition = "int(3) not null" )
  public boolean isLeaf() {
    return isLeaf;
  }

  public MenuItemEntity setLeaf( boolean leaf ) {
    isLeaf = leaf;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    MenuItemEntity that = ( MenuItemEntity ) o;
    return isRoot == that.isRoot &&
        isLeaf == that.isLeaf &&
        Objects.equals( name, that.name ) &&
        Objects.equals( path, that.path );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), name, path, isRoot, isLeaf );
  }

  @Override
  public String toString() {
    return "MenuItemEntity{" +
        "name='" + name + '\'' +
        ", path='" + path + '\'' +
        ", isRoot=" + isRoot +
        ", isLeaf=" + isLeaf +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
