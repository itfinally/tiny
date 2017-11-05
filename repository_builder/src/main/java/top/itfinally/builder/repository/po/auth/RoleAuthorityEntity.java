package top.itfinally.builder.repository.po.auth;


import top.itfinally.builder.annotation.Association;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.repository.po.base.BaseEntity;

@Table( name = "security_role_authority" )
public class  RoleAuthorityEntity extends BaseEntity<RoleAuthorityEntity> {
    private RoleEntity role;
    private AuthorityEntity authority;

    @Association( join = RoleEntity.class )
    public RoleEntity getRole() {
        return role;
    }

    public RoleAuthorityEntity setRole(RoleEntity role) {
        this.role = role;
        return this;
    }

    @Association( join = AuthorityEntity.class )
    public AuthorityEntity getAuthority() {
        return authority;
    }

    public RoleAuthorityEntity setAuthority(AuthorityEntity authority) {
        this.authority = authority;
        return this;
    }

    @Override
    public String toString() {
        return "RoleAuthorityEntity{" +
                "role=" + role +
                ", authority=" + authority +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
