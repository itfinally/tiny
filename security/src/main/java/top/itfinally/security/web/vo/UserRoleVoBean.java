package top.itfinally.security.web.vo;

import top.itfinally.core.vo.BaseVoBean;
import top.itfinally.security.repository.po.UserRoleEntity;

public class UserRoleVoBean extends BaseVoBean<UserRoleVoBean> {
  private RoleVoBean role;
  private UserAuthorityVoBean userAuthority;

  public UserRoleVoBean() {
  }

  public UserRoleVoBean( UserRoleEntity entity ) {
    super( entity );

    this.role = new RoleVoBean( entity.getRole() );
    this.userAuthority = new UserAuthorityVoBean( entity.getUserAuthority() );
  }

  public RoleVoBean getRole() {
    return role;
  }

  public UserRoleVoBean setRole( RoleVoBean role ) {
    this.role = role;
    return this;
  }

  public UserAuthorityVoBean getUserAuthority() {
    return userAuthority;
  }

  public UserRoleVoBean setUserAuthority( UserAuthorityVoBean userAuthority ) {
    this.userAuthority = userAuthority;
    return this;
  }

  @Override
  public String toString() {
    return "UserRoleVoBean{" +
        "id='" + id + '\'' +
        ", status=" + status +
        ", role=" + role +
        ", createTime=" + createTime +
        ", userAuthority=" + userAuthority +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        '}';
  }
}
