package top.itfinally.security.repository.po;

import top.itfinally.builder.annotation.Association;
import top.itfinally.builder.annotation.Table;
import top.itfinally.core.repository.po.BaseEntity;

import java.util.Objects;

@Table( name = "v1_security_user_role" )
public class UserRoleEntity extends BaseEntity<UserRoleEntity> {
    private UserAuthorityEntity userAuthority;
    private RoleEntity role;

    public UserRoleEntity() {
    }

    public UserRoleEntity( String id ) {
        super( id );
    }

    @Association( property = "userAuthority", column = "authority_id", join = UserAuthorityEntity.class )
    public UserAuthorityEntity getUserAuthority() {
        return userAuthority;
    }

    public UserRoleEntity setUserAuthority( UserAuthorityEntity userAuthority ) {
        this.userAuthority = userAuthority;
        return this;
    }

    @Association( property = "role", column = "role_id", join = RoleEntity.class )
    public RoleEntity getRole() {
        return role;
    }

    public UserRoleEntity setRole( RoleEntity role ) {
        this.role = role;
        return this;
    }

    @Override
    public String toString() {
        return "UserRoleEntity{" +
                "userAuthority=" + userAuthority +
                ", id='" + id + '\'' +
                ", role=" + role +
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
        UserRoleEntity that = ( UserRoleEntity ) o;
        return Objects.equals( userAuthority, that.userAuthority ) &&
                Objects.equals( role, that.role );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), userAuthority, role );
    }
}
