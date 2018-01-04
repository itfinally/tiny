package top.itfinally.admin.repository.po;

import top.itfinally.builder.annotation.Association;
import top.itfinally.builder.annotation.Table;
import top.itfinally.core.repository.po.BaseEntity;
import top.itfinally.security.repository.mapper.RoleMapper;
import top.itfinally.security.repository.po.RoleEntity;

import java.util.Objects;

@Table( name = "v1_menu_scope" )
public class RoleMenuItemEntity extends BaseEntity<RoleMenuItemEntity> {
    private RoleEntity role;
    private MenuItemEntity menuItem;

    @Association( property = "role", column = "role_id",
            join = RoleEntity.class, mapper = RoleMapper.class )
    public RoleEntity getRole() {
        return role;
    }

    public RoleMenuItemEntity setRole( RoleEntity role ) {
        this.role = role;
        return this;
    }

    @Association( property = "menuItem", column = "menu_item_id", join = MenuItemEntity.class )
    public MenuItemEntity getMenuItem() {
        return menuItem;
    }

    public RoleMenuItemEntity setMenuItem( MenuItemEntity menuItem ) {
        this.menuItem = menuItem;
        return this;
    }

    @Override
    public String toString() {
        return "RoleMenuItemEntity{" +
                "role=" + role +
                ", menuItem=" + menuItem +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleteTime=" + deleteTime +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;
        RoleMenuItemEntity that = ( RoleMenuItemEntity ) o;
        return Objects.equals( role, that.role ) &&
                Objects.equals( menuItem, that.menuItem );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), role, menuItem );
    }
}
