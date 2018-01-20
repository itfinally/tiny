package top.itfinally.admin.repository.po;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.core.repository.po.BaseEntity;

@Table( name = "v1_menu_item" )
public class MenuItemEntity extends BaseEntity<MenuItemEntity> {
    private String name;
    private String path;
    private boolean isRoot;
    private boolean isLeaf;

    public MenuItemEntity() {
    }

    public MenuItemEntity( String id ) {
        super( id );
    }

    @Column
    public String getName() {
        return name;
    }

    public MenuItemEntity setName( String name ) {
        this.name = name;
        return this;
    }

    @Column
    public String getPath() {
        return path;
    }

    public MenuItemEntity setPath( String path ) {
        this.path = path;
        return this;
    }

    @Column
    public boolean isRoot() {
        return isRoot;
    }

    public MenuItemEntity setRoot( boolean root ) {
        isRoot = root;
        return this;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    @Column
    public MenuItemEntity setLeaf( boolean leaf ) {
        isLeaf = leaf;
        return this;
    }
}
