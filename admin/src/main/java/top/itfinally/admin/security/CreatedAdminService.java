package top.itfinally.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.po.UserDetailsEntity;
import top.itfinally.security.repository.po.AbstractUserDetailsEntity;
import top.itfinally.security.service.AbstractCreatedAdminService;

@Service
@Primary
public class CreatedAdminService implements AbstractCreatedAdminService<UserDetailsEntity> {
  private PasswordEncoder passwordEncoder;

  @Autowired
  public CreatedAdminService setPasswordEncoder( PasswordEncoder passwordEncoder ) {
    this.passwordEncoder = passwordEncoder;
    return this;
  }

  @Override
  public AbstractUserDetailsEntity<UserDetailsEntity> getAdmin() {
    return new UserDetailsEntity().setAccount( "admin" )
        .setPassword( passwordEncoder.encode( "admin" ) )
        .setNickname( "超级管理员" );
  }
}
