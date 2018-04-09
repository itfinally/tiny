package top.itfinally.console.testing;

import top.itfinally.security.repository.entity.AbstractUserDetail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "v1_user" )
public class UserDetailEntity extends AbstractUserDetail<UserDetailEntity> {
  private String username;
  private String password;
  private String userSecurityId;

  @Column
  @Override
  public String getUsername() {
    return username;
  }

  public UserDetailEntity setUsername( String username ) {
    this.username = username;
    return this;
  }

  @Column
  @Override
  public String getPassword() {
    return password;
  }

  public UserDetailEntity setPassword( String password ) {
    this.password = password;
    return this;
  }

  @Column
  @Override
  public String getUserSecurityId() {
    return userSecurityId;
  }

  public UserDetailEntity setUserSecurityId( String userSecurityId ) {
    this.userSecurityId = userSecurityId;
    return this;
  }
}
