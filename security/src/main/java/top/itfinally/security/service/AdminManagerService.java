package top.itfinally.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.*;
import top.itfinally.security.repository.dao.PermissionDao;
import top.itfinally.security.repository.dao.RoleDao;
import top.itfinally.security.repository.dao.UserAuthorityDao;
import top.itfinally.security.repository.dao.UserRoleDao;

import java.util.ArrayList;
import java.util.List;

import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Service
public class AdminManagerService {
  private RoleDao roleDao;
  private UserRoleDao userRoleDao;
  private PermissionDao permissionDao;
  private UserAuthorityDao userAuthorityDao;
  private AbstractUserDetailService userDetailService;
  private AbstractCreatedAdminService abstractCreatedAdminService;

  @Autowired
  public AdminManagerService setRoleDao( RoleDao roleDao ) {
    this.roleDao = roleDao;
    return this;
  }

  @Autowired
  public AdminManagerService setUserRoleDao( UserRoleDao userRoleDao ) {
    this.userRoleDao = userRoleDao;
    return this;
  }

  @Autowired
  public AdminManagerService setPermissionDao( PermissionDao permissionDao ) {
    this.permissionDao = permissionDao;
    return this;
  }

  @Autowired
  public AdminManagerService setUserAuthorityDao( UserAuthorityDao userAuthorityDao ) {
    this.userAuthorityDao = userAuthorityDao;
    return this;
  }

  @Autowired
  public AdminManagerService setUserDetailService( AbstractUserDetailService userDetailService ) {
    this.userDetailService = userDetailService;
    return this;
  }

  @Autowired
  public AdminManagerService setAbstractCreatedAdminService( AbstractCreatedAdminService abstractCreatedAdminService ) {
    this.abstractCreatedAdminService = abstractCreatedAdminService;
    return this;
  }

  @Transactional
  public SingleResponseVoBean<Integer> createAdminAccount() {
    AbstractUserDetailsEntity admin = abstractCreatedAdminService.getAdmin();

    try {
      UserDetails user = userDetailService.loadUserByUsername( admin.getAccount() );

      if ( user != null ) {
        for ( GrantedAuthority authority : user.getAuthorities() ) {
          if ( "ADMIN".equals( authority.getAuthority() ) ) {
            return new SingleResponseVoBean<>( EMPTY_RESULT );
          }
        }
      }

    } catch ( AuthenticationException ignored ) {
    }

    // create admin
    int effectRow = 0;
    RoleEntity role = roleDao.queryByName( "ADMIN", NOT_STATUS_FLAG.getVal() );
    UserAuthorityEntity authority = new UserAuthorityEntity();

    if ( null == role ) {
      throw new IllegalStateException( "Should be run the '/admin/initialization' endpoint before create admin." );
    }

    effectRow += userAuthorityDao.save( authority );
    effectRow += userDetailService.save( admin.setAuthorityId( authority.getId() ) );
    effectRow += userRoleDao.save( new UserRoleEntity().setUserAuthority( authority ).setRole( role ) );

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
  }

  public SingleResponseVoBean<Integer> lockAdminAccount() {
    AbstractUserDetailsEntity admin = abstractCreatedAdminService.getAdmin();
    UserAuthorityEntity user = ( UserAuthorityEntity ) userDetailService.loadUserByUsername( admin.getAccount() );

    if ( null == user ) {
      return new SingleResponseVoBean<Integer>( ResponseStatusEnum.ILLEGAL_REQUEST ).setMessage( "Lock is failed, admin not found." );
    }

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userAuthorityDao.update( user.setNonLocked( false ) ) );
  }

  @Transactional
  public SingleResponseVoBean<Integer> initialization() {
    // 创建超级角色
    List<RoleEntity> roles = new ArrayList<>();
    roles.add( new RoleEntity().setName( "ADMIN" ).setDescription( "管理员" ).setPriority( 0 ) );

    // 创建基础权限
    List<PermissionEntity> permissions = new ArrayList<>();
    permissions.add( new PermissionEntity().setName( "permission_write" ).setDescription( "权限写权限" ) );
    permissions.add( new PermissionEntity().setName( "permission_read" ).setDescription( "权限读权限" ) );
    permissions.add( new PermissionEntity().setName( "user_write" ).setDescription( "用户写权限" ) );
    permissions.add( new PermissionEntity().setName( "user_read" ).setDescription( "用户读权限" ) );
    permissions.add( new PermissionEntity().setName( "role_write" ).setDescription( "角色写权限" ) );
    permissions.add( new PermissionEntity().setName( "role_read" ).setDescription( "角色读权限" ) );
    permissions.add( new PermissionEntity().setName( "grant" ).setDescription( "授权权限" ) );

    int effectRow = 0;
    effectRow += roleDao.saveAll( roles );
    effectRow += permissionDao.saveAll( permissions );

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
  }
}
