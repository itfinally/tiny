package top.itfinally.security.web.vo;

import top.itfinally.core.vo.BaseVoBean;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.UserAuthorityEntity;

import java.util.List;

public class UserAuthorityVoBean extends BaseVoBean<UserAuthorityVoBean> {
  private boolean isNonExpired;
  private boolean isNonLocked;
  private boolean isCredentialsNonExpired;
  private boolean isEnable;
  private List<RoleEntity> authorities;

  public UserAuthorityVoBean() {
  }

  @SuppressWarnings( "unchecked" )
  public UserAuthorityVoBean( UserAuthorityEntity userAuthority ) {
    super( userAuthority );
    this.isCredentialsNonExpired = userAuthority.isCredentialsNonExpired();
    this.isNonExpired = userAuthority.isAccountNonExpired();
    this.isNonLocked = userAuthority.isAccountNonLocked();
    this.isEnable = userAuthority.isEnabled();
    this.authorities = ( List<RoleEntity> ) userAuthority.getAuthorities();
  }

  public boolean isNonExpired() {
    return isNonExpired;
  }

  public UserAuthorityVoBean setNonExpired( boolean nonExpired ) {
    isNonExpired = nonExpired;
    return this;
  }

  public boolean isNonLocked() {
    return isNonLocked;
  }

  public UserAuthorityVoBean setNonLocked( boolean nonLocked ) {
    isNonLocked = nonLocked;
    return this;
  }

  public boolean isCredentialsNonExpired() {
    return isCredentialsNonExpired;
  }

  public UserAuthorityVoBean setCredentialsNonExpired( boolean credentialsNonExpired ) {
    isCredentialsNonExpired = credentialsNonExpired;
    return this;
  }

  public boolean isEnable() {
    return isEnable;
  }

  public UserAuthorityVoBean setEnable( boolean enable ) {
    isEnable = enable;
    return this;
  }

  public List<RoleEntity> getAuthorities() {
    return authorities;
  }

  public UserAuthorityVoBean setAuthorities( List<RoleEntity> authorities ) {
    this.authorities = authorities;
    return this;
  }

  @Override
  public String toString() {
    return "UserAuthorityVoBean{" +
        "id='" + id + '\'' +
        ", status=" + status +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", isNonExpired=" + isNonExpired +
        ", isNonLocked=" + isNonLocked +
        ", isCredentialsNonExpired=" + isCredentialsNonExpired +
        ", isEnable=" + isEnable +
        ", authorities=" + authorities +
        '}';
  }
}
