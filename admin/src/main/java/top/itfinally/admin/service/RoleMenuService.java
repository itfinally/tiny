package top.itfinally.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.RoleMenuItemDao;
import top.itfinally.admin.support.SecurityUtils;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.repository.po.BaseEntity;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.dao.RoleDao;
import top.itfinally.security.repository.dao.RolePermissionDao;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.web.vo.RoleVoBean;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;

@Service
public class RoleMenuService {

  private RolePermissionDao rolePermissionDao;
  private RoleMenuItemDao roleMenuItemDao;
  private RoleDao roleDao;

  @Autowired
  public RoleMenuService setRolePermissionDao( RolePermissionDao rolePermissionDao ) {
    this.rolePermissionDao = rolePermissionDao;
    return this;
  }

  @Autowired
  public RoleMenuService setRoleMenuItemDao( RoleMenuItemDao roleMenuItemDao ) {
    this.roleMenuItemDao = roleMenuItemDao;
    return this;
  }

  @Autowired
  public RoleMenuService setRoleDao( RoleDao roleDao ) {
    this.roleDao = roleDao;
    return this;
  }

  public CollectionResponseVoBean<RoleVoBean> queryMenuItemRoles( String menuId ) {
    List<RoleEntity> roles = roleMenuItemDao.queryMenuItemRoles( menuId );
    ResponseStatusEnum status = roles.isEmpty() ? SUCCESS : EMPTY_RESULT;

    return new CollectionResponseVoBean<RoleVoBean>( status )
        .setResult( roles.stream().map( RoleVoBean::new ).collect( toList() ) );
  }

  public CollectionResponseVoBean<RoleVoBean> queryAvailableRole( String menuId ) {
    RoleEntity maxRole = SecurityUtils.getMaxRoleWithGrant( roleId -> rolePermissionDao.queryByRoleId( roleId, NORMAL.getStatus() ) );
    if ( null == maxRole ) {
      return new CollectionResponseVoBean<>( EMPTY_RESULT );
    }

    List<RoleEntity> roles = roleDao.queryByPriority( maxRole.getPriority(), NORMAL.getStatus() );
    Set<String> roleIds = roleMenuItemDao.queryMenuItemRoles( menuId ).stream().map( BaseEntity::getId ).collect( toSet() );

    List<RoleVoBean> availableRoles = roles.stream().filter( role -> !roleIds.contains( role.getId() ) ).map( RoleVoBean::new ).collect( toList() );

    ResponseStatusEnum status = availableRoles.isEmpty() ? EMPTY_RESULT : SUCCESS;
    return new CollectionResponseVoBean<RoleVoBean>( status ).setResult( availableRoles );
  }

  public SingleResponseVoBean<Integer> addRoleMenu( String menuItemId, List<String> roleIds ) {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleMenuItemDao.addRoleMenu( menuItemId, roleIds ) );
  }

  public SingleResponseVoBean<Integer> removeRoleMenu( String menuItemId, List<String> roleIds ) {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleMenuItemDao.removeRoleMenu( menuItemId, roleIds ) );
  }
}
