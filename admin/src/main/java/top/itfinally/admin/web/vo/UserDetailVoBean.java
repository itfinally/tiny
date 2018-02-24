package top.itfinally.admin.web.vo;

import top.itfinally.core.vo.BaseVoBean;
import top.itfinally.security.repository.po.AbstractUserDetailsEntity;

public class UserDetailVoBean extends BaseVoBean<UserDetailVoBean> {

  private String account;
  private String nickname;
  private String authorityId;

  public UserDetailVoBean() {
  }

  public UserDetailVoBean( AbstractUserDetailsEntity entity ) {
    super( entity );
    this.account = entity.getAccount();
    this.nickname = entity.getNickname();
    this.authorityId = entity.getAuthorityId();
  }

  public String getAccount() {
    return account;
  }

  public UserDetailVoBean setAccount( String account ) {
    this.account = account;
    return this;
  }

  public String getNickname() {
    return nickname;
  }

  public UserDetailVoBean setNickname( String nickname ) {
    this.nickname = nickname;
    return this;
  }

  public String getAuthorityId() {
    return authorityId;
  }

  public UserDetailVoBean setAuthorityId( String authorityId ) {
    this.authorityId = authorityId;
    return this;
  }
}
