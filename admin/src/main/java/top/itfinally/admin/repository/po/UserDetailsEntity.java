package top.itfinally.admin.repository.po;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.security.repository.po.AbstractUserDetailsEntity;

import java.util.Objects;

@Table( name = "v1_user_details" )
public class UserDetailsEntity extends AbstractUserDetailsEntity<UserDetailsEntity> {
  private String account;
  private String password;
  private String nickname;
  private String authorityId;

  @Column
  @Override
  public String getAccount() {
    return account;
  }

  @Column
  @Override
  public String getPassword() {
    return password;
  }

  @Column
  @Override
  public String getNickname() {
    return nickname;
  }

  @Column
  @Override
  public String getAuthorityId() {
    return authorityId;
  }

  @Override
  public UserDetailsEntity setAuthorityId( String authorityId ) {
    this.authorityId = authorityId;
    return this;
  }

  public UserDetailsEntity setAccount( String account ) {
    this.account = account;
    return this;
  }

  public UserDetailsEntity setPassword( String password ) {
    this.password = password;
    return this;
  }

  public UserDetailsEntity setNickname( String nickname ) {
    this.nickname = nickname;
    return this;
  }

  @Override
  public String toString() {
    return "UserDetailsEntity{" +
        "account='" + account + '\'' +
        ", password='" + password + '\'' +
        ", nickname='" + nickname + '\'' +
        ", authorityId='" + authorityId + '\'' +
        ", id='" + id + '\'' +
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
    UserDetailsEntity that = ( UserDetailsEntity ) o;
    return Objects.equals( account, that.account ) &&
        Objects.equals( password, that.password ) &&
        Objects.equals( nickname, that.nickname ) &&
        Objects.equals( authorityId, that.authorityId );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), account, password, nickname, authorityId );
  }
}
