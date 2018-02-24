package top.itfinally.security.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.core.exception.SqlOperationException;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.repository.mapper.UserAuthorityMapper;
import top.itfinally.security.repository.mapper.UserRoleMapper;

import java.util.Collection;
import java.util.List;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;

@Repository
public class UserAuthorityDao extends AbstractDao<UserAuthorityEntity, UserAuthorityMapper> {
  private UserRoleMapper userRoleMapper;

  @Autowired
  public UserAuthorityDao setUserRoleMapper( UserRoleMapper userRoleMapper ) {
    this.userRoleMapper = userRoleMapper;
    return this;
  }

  @Override
  @Autowired
  protected void setBaseMapper( UserAuthorityMapper baseMapper ) {
    super.setBaseMapper( baseMapper );
  }

  @Override
  public int remove( String id, long deleteTime ) {
    if ( userRoleMapper.hasUserAuthority( id, NORMAL.getStatus() ) ) {
      throw new SqlOperationException( "Cannot remove this user's authority detail before all user-role record is removed." );
    }

    return super.remove( id, deleteTime );
  }

  @Override
  public int removeAll( Collection<String> ids, long deleteTime ) {
    if ( userRoleMapper.hasAllUserAuthority( ids, NORMAL.getStatus() ) ) {
      throw new SqlOperationException( "Cannot remove all user's authority detail before all user-role record is removed." );
    }

    return super.removeAll( ids, deleteTime );
  }
}
