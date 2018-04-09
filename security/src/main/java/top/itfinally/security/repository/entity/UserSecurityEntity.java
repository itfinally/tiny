package top.itfinally.security.repository.entity;

import org.springframework.security.core.userdetails.UserDetails;
import top.itfinally.core.repository.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Entity
@Table( name = "v1_security_user_security" )
public class UserSecurityEntity extends BasicEntity<UserSecurityEntity> {
  // Indicates whether the user's account has expired.
  private boolean isNonExpired = true;

  // Indicates whether the user is locked or unlocked.
  private boolean isNonLocked = true;

  // Indicates whether the user's credentials (password) has expired.
  private boolean isCredentialsNonExpired = true;

  // Indicates whether the user is enabled or disabled.
  private boolean isEnable = true;

  public UserSecurityEntity() {
  }

  public UserSecurityEntity( String id ) {
    super( id );
  }

  @Column( name = "is_non_expired" )
  public boolean isNonExpired() {
    return isNonExpired;
  }

  public UserSecurityEntity setNonExpired( boolean nonExpired ) {
    isNonExpired = nonExpired;
    return this;
  }

  @Column( name = "is_non_locked" )
  public boolean isNonLocked() {
    return isNonLocked;
  }

  public UserSecurityEntity setNonLocked( boolean nonLocked ) {
    isNonLocked = nonLocked;
    return this;
  }

  @Column( name = "is_credentials_non_expired" )
  public boolean isCredentialsNonExpired() {
    return isCredentialsNonExpired;
  }

  public UserSecurityEntity setCredentialsNonExpired( boolean credentialsNonExpired ) {
    isCredentialsNonExpired = credentialsNonExpired;
    return this;
  }

  @Column( name = "is_enable" )
  public boolean isEnable() {
    return isEnable;
  }

  public UserSecurityEntity setEnable( boolean enable ) {
    isEnable = enable;
    return this;
  }

  public class UserSecurityDelegateEntity<E extends AbstractUserDetail<E>> implements UserDetails {
    private E user;
    private List<RoleEntity> roleEntities;
    private List<RoleEntity.RoleDelegateEntity> authorities;

    public UserSecurityDelegateEntity( E user, List<RoleEntity> authorities ) {
      this.user = user;
      this.roleEntities = authorities;
      this.authorities = authorities.stream().map( it -> it.new RoleDelegateEntity() ).collect( toList() );
    }

    public String getId() {
      return id;
    }

    public long getCreateTime() {
      return createTime;
    }

    public long getUpdateTime() {
      return updateTime;
    }

    public long getDeleteTime() {
      return deleteTime;
    }

    public int getStatus() {
      return status;
    }

    public E getUser() {
      return user;
    }

    public List<RoleEntity> getRoleEntities() {
      return roleEntities;
    }

    @Override
    public List<RoleEntity.RoleDelegateEntity> getAuthorities() {
      return authorities;
    }

    @Override
    public String getPassword() {
      return user.getPassword();
    }

    @Override
    public String getUsername() {
      return user.getUsername();
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
  }
}
