package top.itfinally.security.repository.po;

import top.itfinally.core.repository.po.BaseEntity;

import java.util.Objects;

public class PermissionEntity extends BaseEntity<PermissionEntity> {
    private String name;
    private String description;

    public PermissionEntity() {
    }

    public PermissionEntity( String id ) {
        super( id );
    }

    public String getName() {
        return name;
    }

    public PermissionEntity setName( String name ) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PermissionEntity setDescription( String description ) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "PermissionEntity{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
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
        PermissionEntity that = ( PermissionEntity ) o;
        return Objects.equals( name, that.name ) &&
                Objects.equals( description, that.description );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), name, description );
    }
}
