package top.itfinally.security.repository.po;

import org.springframework.security.core.GrantedAuthority;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.core.repository.po.BaseEntity;

import java.util.Objects;

@Table( name = "security_role" )
public class RoleEntity extends BaseEntity<RoleEntity> implements GrantedAuthority {
    private String name;
    private String description;

    public RoleEntity() {
    }

    public RoleEntity( String id ) {
        super( id );
    }

    @Column
    public String getName() {
        return name;
    }

    @Override

    // Use default prefix for security, and ignore serializable
    // Only spring security use it.
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
                "id='" + id + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleteTime=" + deleteTime +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;
        RoleEntity that = ( RoleEntity ) o;
        return Objects.equals( name, that.name ) &&
                Objects.equals( description, that.description );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), name, description );
    }
}
