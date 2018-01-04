package top.itfinally.security.repository.po;

import org.springframework.security.core.GrantedAuthority;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.core.repository.po.BaseEntity;

import java.util.Objects;

@Table( name = "v1_security_role" )
public class RoleEntity extends BaseEntity<RoleEntity> implements GrantedAuthority {
    private int priority;
    private String name;
    private String description;

    public RoleEntity() {
    }

    public RoleEntity( String id ) {
        super( id );
    }

    @Column
    public int getPriority() {
        return priority;
    }

    public RoleEntity setPriority( int priority ) {
        this.priority = priority;
        return this;
    }

    @Column
    public String getName() {
        return name;
    }

    // Use default prefix for security, and ignore serializable
    // Only spring security use it.
    @Override
    public String getAuthority() {
        return "ROLE_" + name.toUpperCase();
    }

    public RoleEntity setName( String name ) {
        this.name = name.toUpperCase();
        return this;
    }

    @Column
    public String getDescription() {
        return description;
    }

    public RoleEntity setDescription( String description ) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "RoleEntity{" +
                "priority=" + priority +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
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
        RoleEntity that = ( RoleEntity ) o;
        return priority == that.priority &&
                Objects.equals( name, that.name ) &&
                Objects.equals( description, that.description );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), priority, name, description );
    }
}
