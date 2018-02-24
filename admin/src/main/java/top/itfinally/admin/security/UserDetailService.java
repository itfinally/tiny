package top.itfinally.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.UserDetailsDao;
import top.itfinally.admin.repository.po.UserDetailsEntity;
import top.itfinally.admin.web.vo.UserDetailVoBean;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.exception.PermissionDeniedException;
import top.itfinally.security.repository.dao.PermissionDao;
import top.itfinally.security.repository.dao.RoleDao;
import top.itfinally.security.repository.dao.RolePermissionDao;
import top.itfinally.security.repository.po.*;
import top.itfinally.security.service.AbstractUserDetailService;
import top.itfinally.security.web.vo.PermissionVoBean;
import top.itfinally.security.web.vo.RoleVoBean;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Service
public class UserDetailService extends AbstractUserDetailService<UserDetailsEntity> {
  private RoleDao roleDao;
  private PermissionDao permissionDao;
  private UserDetailsDao userDetailsDao;
  private RolePermissionDao rolePermissionDao;

  @Override
  @Autowired
  public UserDetailService setRoleDao( RoleDao roleDao ) {
    super.setRoleDao( roleDao );
    this.roleDao = roleDao;
    return this;
  }

  @Autowired
  public UserDetailService setPermissionDao( PermissionDao permissionDao ) {
    this.permissionDao = permissionDao;
    return this;
  }

  @Autowired
  public UserDetailService setUserDetailsDao( UserDetailsDao userDetailsDao ) {
    this.userDetailsDao = userDetailsDao;
    return this;
  }

  @Autowired
  public UserDetailService setRolePermissionDao( RolePermissionDao rolePermissionDao ) {
    this.rolePermissionDao = rolePermissionDao;
    return this;
  }

  public CollectionResponseVoBean<UserDetailVoBean> queryByMultiCondition( Map<String, Object> condition, int beginRow, int row ) {
    List<UserDetailsEntity> result = userDetailsDao.queryByMultiCondition( condition, beginRow, row );
    ResponseStatusEnum status = result.isEmpty() ? EMPTY_RESULT : SUCCESS;

    return new CollectionResponseVoBean<UserDetailVoBean>( status ).setResult( result.stream()
        .filter( item -> !"admin".equalsIgnoreCase( item.getAccount() ) )
        .map( UserDetailVoBean::new )
        .collect( toList() ) );
  }

  public SingleResponseVoBean<Integer> countByMultiCondition( Map<String, Object> condition ) {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userDetailsDao.countByMultiCondition( condition ) );
  }

  public SingleResponseVoBean<Integer> updateUserDetail( String userId, String nickname, int status ) {
    UserDetailsEntity user = userDetailsDao.query( userId );
    if ( null == user ) {
      return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST ).setMessage( "User is not exists." );
    }

    if ( "admin".equals( user.getAccount() ) ) {
      throw new PermissionDeniedException( "Cannot modify admin by web interface." );
    }

    user.setStatus( status ).setNickname( nickname ).setDeleteTime( DELETE.getStatus() == status ? System.currentTimeMillis() : -1 );

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userDetailsDao.update( user ) );
  }

  public SingleResponseVoBean<Integer> updateUserStatus( int status, List<String> userIds ) {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userDetailsDao.updateUserStatus( status, userIds ) );
  }

  public SingleResponseVoBean<Integer> register( String account, String nickname, String password ) {
    if ( userDetailsDao.queryByAccount( account ) != null ) {
      return new SingleResponseVoBean<Integer>( EMPTY_RESULT ).setMessage( String.format( "Account '%s' already exist.", account ) );
    }

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userDetailsDao.register( account, nickname, password ) );
  }

  public SingleResponseVoBean<UserDetailVoBean> getOwnDetails() {
    UserAuthorityEntity authorityEntity = ( UserAuthorityEntity ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    return new SingleResponseVoBean<UserDetailVoBean>( SUCCESS ).setResult( new UserDetailVoBean( authorityEntity.getUser() ) );
  }

  public CollectionResponseVoBean<RoleVoBean> getOwnRoles() {
    UserAuthorityEntity authorityEntity = ( UserAuthorityEntity ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    List<RoleVoBean> result;
    if ( authorityEntity.getAuthorities().stream().anyMatch( item -> "ROLE_ADMIN".equalsIgnoreCase( item.getAuthority() ) ) ) {
      result = roleDao.queryAll( NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal() ).stream()
          .map( RoleVoBean::new ).collect( toList() );

    } else {
      result = authorityEntity.getAuthorities().stream()
          .map( item -> new RoleVoBean( ( RoleEntity ) item ) )
          .collect( toList() );
    }

    ResponseStatusEnum status = result.isEmpty() ? EMPTY_RESULT : SUCCESS;

    return new CollectionResponseVoBean<RoleVoBean>( status ).setResult( result );
  }

  @PreAuthorize( "!isAnonymous()" )
  public CollectionResponseVoBean<PermissionVoBean> getOwnPermissions() {
    UserAuthorityEntity authorityEntity = ( UserAuthorityEntity ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    List<PermissionVoBean> result;
    if ( authorityEntity.getAuthorities().stream().anyMatch( item -> "ROLE_ADMIN".equalsIgnoreCase( item.getAuthority() ) ) ) {
      result = permissionDao.queryAll( NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal() ).stream()
          .map( PermissionVoBean::new ).collect( toList() );

    } else {
      result = authorityEntity.getAuthorities().stream()
          .map( item -> rolePermissionDao.queryByRoleId( ( ( RoleEntity ) item ).getId(), NORMAL.getStatus() ).stream()
              .map( RolePermissionEntity::getPermission )
              .collect( toList() ) )

          // flatMap 即降维操作
          .flatMap( Collection::stream )
          .distinct()

          .filter( item -> item.getStatus() == NORMAL.getStatus() )
          .map( PermissionVoBean::new )
          .collect( toList() );
    }

    ResponseStatusEnum status = result.isEmpty() ? EMPTY_RESULT : SUCCESS;

    return new CollectionResponseVoBean<PermissionVoBean>( status ).setResult( result );
  }

  @Override
  protected UserDetailsEntity loadUserByAccount( String account ) {
    return userDetailsDao.queryByAccount( account );
  }

  @Override
  public int save( AbstractUserDetailsEntity user ) {
    return userDetailsDao.save( ( UserDetailsEntity ) user );
  }

  @Override
  public int update( AbstractUserDetailsEntity user ) {
    return userDetailsDao.update( ( UserDetailsEntity ) user );
  }
}
