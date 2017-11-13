package top.itfinally.admin.repository.po;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.core.repository.po.BaseEntity;

@Table( name = "menu_relationship" )
public class MenuRelationEntity extends BaseEntity<MenuRelationEntity> {
    private String parentId;
    private String childId;
    private int gap;

    @Column
    public String getParentId() {
        return parentId;
    }

    public MenuRelationEntity setParentId( String parentId ) {
        this.parentId = parentId;
        return this;
    }

    @Column
    public String getChildId() {
        return childId;
    }

    public MenuRelationEntity setChildId( String childId ) {
        this.childId = childId;
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
