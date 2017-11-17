package top.itfinally.admin.repository.po;

import top.itfinally.builder.annotation.Association;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.core.repository.po.BaseEntity;

@Table( name = "menu_relationship" )
public class MenuRelationEntity extends BaseEntity<MenuRelationEntity> {
    private int gap;
    private MenuItemEntity parent;
    private MenuItemEntity child;

    @Association( property = "parent", column = "parent_id", join = MenuItemEntity.class )
    public MenuItemEntity getParent() {
        return parent;
    }

    public MenuRelationEntity setParent( MenuItemEntity parent ) {
        this.parent = parent;
        return this;
    }

    @Association( property = "child", column = "child_id", join = MenuItemEntity.class )
    public MenuItemEntity getChild() {
        return child;
    }

    public MenuRelationEntity setChild( MenuItemEntity child ) {
        this.child = child;
        return this;
    }

    @Column
    public int getGap() {
        return gap;
    }

    public MenuRelationEntity setGap( int gap ) {
        this.gap = gap;
        return this;
    }
}
