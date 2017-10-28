package top.itfinally.security.repository.po;

import top.itfinally.core.repository.po.BaseEntity;

import java.util.Objects;

public class RolePermissionEntity extends BaseEntity<RolePermissionEntity> {
    private RoleEntity role;
    private PermissionEntity permission;

    public RolePermissionEntity() {
    }

    public RolePermissionEntity( String id ) {
        super( id );
    }

    public RoleEntity getRole() {
        return role;
    }

    public RolePermissionEntity setRole( RoleEntity role ) {
        this.role = role;
        return this;
    }

    public PermissionEntity getPermission() {
        return permission;
    }

    public RolePermissionEntity setPermission( PermissionEntity permission ) {
        this.permission = permission;
        return this;
    }

    @Override
    public String toString() {
        return "RolePermissionEntity{" +
                "role=" + role +
                ", id='" + id + '\'' +
                ", permission=" + permission +
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
        RolePermissionEntity that = ( RolePermissionEntity ) o;
        return Objects.equals( role, that.role ) &&
                Objects.equals( permission, that.permission );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), role, permission );
    }
}
