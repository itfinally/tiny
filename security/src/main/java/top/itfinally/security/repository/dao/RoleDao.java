package top.itfinally.security.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.core.exception.SqlOperationException;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.mapper.RoleMapper;
import top.itfinally.security.repository.mapper.RolePermissionMapper;
import top.itfinally.security.repository.mapper.UserRoleMapper;

import java.util.Collection;
import java.util.List;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;

@Repository
public class RoleDao extends AbstractDao<RoleEntity, RoleMapper> {
  private RoleMapper roleMapper;
  private UserRoleMapper userRoleMapper;
  private RolePermissionMapper rolePermissionMapper;

  @Override
  @Autowired
  protected void setBaseMapper( RoleMapper baseMapper ) {
    this.roleMapper = baseMapper;
    super.setBaseMapper( baseMapper );
  }

  @Autowired
  public RoleDao setUserRoleMapper( UserRoleMapper userRoleMapper ) {
    this.userRoleMapper = userRoleMapper;
    return this;
  }

  @Autowired
  public RoleDao setRolePermissionMapper( RolePermissionMapper rolePermissionMapper ) {
    this.rolePermissionMapper = rolePermissionMapper;
    return this;
  }

  public List<RoleEntity> queryRoleByAuthorityId( String authorityId, int status ) {
    return roleMapper.queryRoleByAuthorityId( authorityId, status );
  }

  public RoleEntity queryByName( String name, int status ) {
    return roleMapper.queryByName( name, status );
  }

  @Override
  public int remove( String id, long deleteTime ) {
    if ( userRoleMapper.hasRole( id, NORMAL.getStatus() ) ) {
      throw new SqlOperationException( "Cannot remove this role before all user-role record is removed." );
    }

    if ( rolePermissionMapper.hasRole( id, NORMAL.getStatus() ) ) {
      throw new SqlOperationException( "Cannot remove this role before all role-permission record is removed." );
    }

    return super.remove( id, deleteTime );
  }

  @Override
  public int removeAll( Collection<String> ids, long deleteTime ) {
    if ( userRoleMapper.hasAllRole( ids, NORMAL.getStatus() ) ) {
      throw new SqlOperationException( "Cannot remove roles before all user-role record is removed." );
    }

    if ( rolePermissionMapper.hasAllRole( ids, NORMAL.getStatus() ) ) {
      throw new SqlOperationException( "Cannot remove roles before all role-permission record is removed." );
    }

    return super.removeAll( ids, deleteTime );
  }

  public List<RoleEntity> queryByPriority( int priority, int status ) {
    return roleMapper.queryByPriority( priority, status );
  }
}
