package top.itfinally.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.PermissionEnhancedDao;
import top.itfinally.admin.support.SecurityUtils;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.dao.PermissionDao;
import top.itfinally.security.repository.dao.RolePermissionDao;
import top.itfinally.security.repository.po.PermissionEntity;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.RolePermissionEntity;
import top.itfinally.security.web.vo.PermissionVoBean;

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
public class PermissionService {

  private PermissionEnhancedDao permissionEnhancedDao;
  private RolePermissionDao rolePermissionDao;
  private PermissionDao permissionDao;

  @Autowired
  public PermissionService setPermissionEnhancedDao( PermissionEnhancedDao permissionEnhancedDao ) {
    this.permissionEnhancedDao = permissionEnhancedDao;
    return this;
  }

  @Autowired
  public PermissionService setRolePermissionDao( RolePermissionDao rolePermissionDao ) {
    this.rolePermissionDao = rolePermissionDao;
    return this;
  }

  @Autowired
  public PermissionService setPermissionDao( PermissionDao permissionDao ) {
    this.permissionDao = permissionDao;
    return this;
  }

  public CollectionResponseVoBean<PermissionVoBean> queryByMultiCondition( Map<String, Object> condition, int beginRow, int row ) {
    List<PermissionVoBean> result = permissionEnhancedDao.queryByMultiCondition( condition, beginRow, row )
        .stream().map( PermissionVoBean::new ).collect( toList() );

    ResponseStatusEnum status = result.isEmpty() ? EMPTY_RESULT : SUCCESS;

    return new CollectionResponseVoBean<PermissionVoBean>( status ).setResult( result );
  }

  public SingleResponseVoBean<Integer> countByMultiCondition( Map<String, Object> condition ) {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( permissionEnhancedDao.countByMultiCondition( condition ) );
  }

  public SingleResponseVoBean<Integer> updatePermissionDetail( String id, String name, String description, int status ) {
    PermissionEntity permission = permissionDao.query( id );
    if ( null == permission ) {
      return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST ).setMessage( String.format( "Permission '%s' not found.", id ) );
    }

    permission.setName( name ).setStatus( status ).setDescription( description )
        .setDeleteTime( DELETE.getStatus() == status ? System.currentTimeMillis() : -1 );

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( permissionDao.update( permission ) );
  }

  public SingleResponseVoBean<Integer> updatePermissionStatus( List<String> ids, int status ) {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( permissionEnhancedDao.updatePermissionStatus( ids, status ) );
  }

  public CollectionResponseVoBean<PermissionVoBean> getPermissions() {
    RoleEntity role = SecurityUtils.getMaxRoleWithGrant( roleId -> rolePermissionDao.queryByRoleId( roleId, NORMAL.getStatus() ) );
    if ( null == role ) {
      return new CollectionResponseVoBean<>( EMPTY_RESULT );
    }

    List<PermissionEntity> permissionEntities = "ROLE_ADMIN".equals( role.getAuthority() )

        ? permissionDao.queryAll( NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal() )

        : rolePermissionDao.queryByRoleId( role.getId(), NORMAL.getStatus() ).stream()
        .map( RolePermissionEntity::getPermission )
        .filter( item -> item.getStatus() == NORMAL.getStatus() )
        .collect( toList() );

    ResponseStatusEnum status = permissionEntities.isEmpty() ? EMPTY_RESULT : SUCCESS;

    return new CollectionResponseVoBean<PermissionVoBean>( status ).setResult( permissionEntities.stream()
        .map( PermissionVoBean::new ).collect( toList() ) );
  }

  public CollectionResponseVoBean<PermissionVoBean> getSpecificRolePermissions( String roleId ) {
    List<RolePermissionEntity> result = rolePermissionDao.queryByRoleId( roleId, NORMAL.getStatus() );
    ResponseStatusEnum status = result.isEmpty() ? EMPTY_RESULT : SUCCESS;

    return new CollectionResponseVoBean<PermissionVoBean>( status ).setResult( result.stream()
        .map( item -> new PermissionVoBean( item.getPermission() ) ).collect( toList() ) );
  }

  public SingleResponseVoBean<Integer> permissionInitializing() {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( permissionEnhancedDao.permissionInitializing() );
  }
}
