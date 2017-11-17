package top.itfinally.security.repository.po;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.core.repository.po.BaseEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Table( name = "security_user_details" )
public class UserAuthorityEntity extends BaseEntity<UserAuthorityEntity> implements UserDetails {
    // Indicates whether the user's account has expired.
    @Column
    private boolean isNonExpired = true;

    // Indicates whether the user is locked or unlocked.
    @Column
    private boolean isNonLocked = true;

    // Indicates whether the user's credentials (password) has expired.
    @Column
    private boolean isCredentialsNonExpired = true;

    // Indicates whether the user is enabled or disabled.
    @Column
    private boolean isEnable = true;

    // user 通过后期查询注入, 不再关注用户的具体信息
    // 在实现上 mybatis 难以做到抽象继承
    private UserDetailsEntity user;

    // 不参与数据库查询, security_user 表不包含这个字段
    // 后期查询直接调用 setter 注入
    private List<RoleEntity> authorities;

    public UserAuthorityEntity() {
    }

    public UserAuthorityEntity( String id ) {
        super( id );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public List<RoleEntity> getRoles() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getAccount();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnable;
    }

    public String getNickname() {
        return user.getNickname();
    }

    public UserAuthorityEntity setAuthorities( List<RoleEntity> authorities ) {
        this.authorities = authorities;
        return this;
    }

    public UserAuthorityEntity setNonExpired( boolean nonExpired ) {
        isNonExpired = nonExpired;
        return this;
    }

    public UserAuthorityEntity setNonLocked( boolean nonLocked ) {
        isNonLocked = nonLocked;
        return this;
    }

    public UserAuthorityEntity setCredentialsNonExpired( boolean credentialsNonExpired ) {
        isCredentialsNonExpired = credentialsNonExpired;
        return this;
    }

    public UserAuthorityEntity setEnable( boolean enable ) {
        isEnable = enable;
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public <User extends UserDetailsEntity> User getUser() {
        return ( User ) user;
    }

    public UserAuthorityEntity setUser( UserDetailsEntity user ) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return "UserAuthorityEntity{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleteTime=" + deleteTime +
                ", isNonExpired=" + isNonExpired +
                ", isNonLocked=" + isNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", isEnable=" + isEnable +
                ", user=" + user +
                ", authorities=" + authorities +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;
        UserAuthorityEntity that = ( UserAuthorityEntity ) o;
        return isNonExpired == that.isNonExpired &&
                isNonLocked == that.isNonLocked &&
                isCredentialsNonExpired == that.isCredentialsNonExpired &&
                isEnable == that.isEnable &&
                Objects.equals( user, that.user ) &&
                Objects.equals( authorities, that.authorities );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), isNonExpired, isNonLocked, isCredentialsNonExpired, isEnable, user, authorities );
    }
}
