package top.itfinally.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import top.itfinally.core.repository.QueryEnum;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.*;
import top.itfinally.security.repository.dao.*;

import java.util.*;

import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Service
public class AuthorizationService {

  private PermissionValidService permissionValidService;
  private RolePermissionDao rolePermissionDao;
  private PermissionDao permissionDao;
  private UserRoleDao userRoleDao;
  private RoleDao roleDao;

  private UserDetailCachingService userDetailCachingService;

  @Autowired
  public AuthorizationService setPermissionValidService( PermissionValidService permissionValidService ) {
    this.permissionValidService = permissionValidService;
    return this;
  }

  @Autowired
  public AuthorizationService setRoleDao( RoleDao roleDao ) {
    this.roleDao = roleDao;
    return this;
  }

  @Autowired
  public AuthorizationService setPermissionDao( PermissionDao permissionDao ) {
    this.permissionDao = permissionDao;
    return this;
  }

  @Autowired
  public AuthorizationService setRolePermissionDao( RolePermissionDao rolePermissionDao ) {
    this.rolePermissionDao = rolePermissionDao;
    return this;
  }

  @Autowired
  public AuthorizationService setUserRoleDao( UserRoleDao userRoleDao ) {
    this.userRoleDao = userRoleDao;
    return this;
  }

  @Autowired
  public AuthorizationService setUserDetailCachingService( UserDetailCachingService userDetailCachingService ) {
    this.userDetailCachingService = userDetailCachingService;
    return this;
  }

  public SingleResponseVoBean<Integer> addRole( RoleEntity role ) {
    RoleEntity admin = roleDao.queryByName( "ADMIN", NOT_STATUS_FLAG.getVal() );

    // 优先级 0 只有 admin 角色可用
    if ( ( admin != null && role.getPriority() == 0 ) || ( role.getPriority() == 0 && !"ADMIN".equalsIgnoreCase( role.getName() ) ) ) {
      return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST ).setMessage( "Can not create duplicate zero priority." );
    }

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleDao.save( role ) );
  }

  public SingleResponseVoBean<Integer> addPermission( PermissionEntity permission ) {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( permissionDao.save( permission ) );
  }

  public SingleResponseVoBean<Integer> grantPermissionsTo( String roleId, List<String> permissionIds ) {
    int effectRow = rolePermissionDao.grantPermissionsTo( roleId, permissionIds );
    permissionValidService.refreshRolePermission( roleId );

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
  }

  public SingleResponseVoBean<Integer> grantRolesTo( String authorityId, List<String> roleIds ) {
    int result = userRoleDao.grantRolesTo( authorityId, roleIds );
    userDetailCachingService.setAccountChange( authorityId );

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( result );
  }
}
