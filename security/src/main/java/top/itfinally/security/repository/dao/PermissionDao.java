package top.itfinally.security.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.core.exception.SqlOperationException;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.PermissionEntity;
import top.itfinally.security.repository.mapper.PermissionMapper;
import top.itfinally.security.repository.mapper.RolePermissionMapper;

import java.util.Collection;
import java.util.List;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;

@Repository
public class PermissionDao extends AbstractDao<PermissionEntity, PermissionMapper> {
  private PermissionMapper permissionMapper;
  private RolePermissionMapper rolePermissionMapper;

  @Override
  @Autowired
  protected void setBaseMapper( PermissionMapper baseMapper ) {
    this.permissionMapper = baseMapper;
    super.setBaseMapper( baseMapper );
  }

  @Autowired
  public PermissionDao setRolePermissionMapper( RolePermissionMapper rolePermissionMapper ) {
    this.rolePermissionMapper = rolePermissionMapper;
    return this;
  }

  public List<PermissionEntity> queryByRoleId( String roleId ) {
    return permissionMapper.queryByRoleId( roleId );
  }

  @Override
  public int remove( String id, long deleteTime ) {
    if ( rolePermissionMapper.hasPermission( id, NORMAL.getStatus() ) ) {
      throw new SqlOperationException( "Cannot remove this permission before all role-permission record is removed." );
    }

    return super.remove( id, deleteTime );
  }

  @Override
  public int removeAll( Collection<String> ids, long deleteTime ) {
    if ( rolePermissionMapper.hasAllPermission( ids, NORMAL.getStatus() ) ) {
      throw new SqlOperationException( "Cannot remove permissions before all role-permission record is removed." );
    }

    return super.removeAll( ids, deleteTime );
  }
}
