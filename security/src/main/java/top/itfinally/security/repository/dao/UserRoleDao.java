package top.itfinally.security.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.core.exception.SqlOperationException;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.core.util.CollectionUtils;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.repository.po.UserRoleEntity;
import top.itfinally.security.repository.mapper.RoleMapper;
import top.itfinally.security.repository.mapper.UserAuthorityMapper;
import top.itfinally.security.repository.mapper.UserRoleMapper;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Repository
public class UserRoleDao extends AbstractDao<UserRoleEntity, UserRoleMapper> {
  private RoleMapper roleMapper;
  private UserRoleMapper userRoleMapper;
  private UserAuthorityMapper userAuthorityMapper;

  @Autowired
  public UserRoleDao setRoleMapper( RoleMapper roleMapper ) {
    this.roleMapper = roleMapper;
    return this;
  }

  @Autowired
  public UserRoleDao setUserAuthorityMapper( UserAuthorityMapper userAuthorityMapper ) {
    this.userAuthorityMapper = userAuthorityMapper;
    return this;
  }

  @Override
  @Autowired
  protected void setBaseMapper( UserRoleMapper baseMapper ) {
    this.userRoleMapper = baseMapper;
    super.setBaseMapper( baseMapper );
  }

  @Override
  public int save( UserRoleEntity entity ) {
    UserAuthorityEntity user = userAuthorityMapper.query( entity.getUserAuthority().getId() );
    RoleEntity role = roleMapper.query( entity.getRole().getId() );

    if ( null == user || null == role ) {
      throw new SqlOperationException( "Cannot insert with non-existent value.( check your userAuthority and role )" );
    }

    if ( !NORMAL.expect( user.getStatus(), role.getStatus() ) ) {
      throw new SqlOperationException( "Cannot insert within deleted value." );
    }

    return super.save( entity );
  }

  @Override
  public int saveAll( Collection<UserRoleEntity> userRoleEntities ) {
    Set<String> userAuthorityIds = new HashSet<>();
    Set<String> roleIds = new HashSet<>();

    userRoleEntities.forEach( entity -> {
      userAuthorityIds.add( entity.getUserAuthority().getId() );
      roleIds.add( entity.getRole().getId() );
    } );

    List<UserAuthorityEntity> userAuthorises = userAuthorityMapper.queryBySpecificId( userAuthorityIds,
        NOT_PAGING.getVal(),
        NOT_PAGING.getVal(),
        NOT_STATUS_FLAG.getVal()
    );

    if ( userAuthorityIds.size() != userAuthorises.size() ) {
      throw new SqlOperationException( "Cannot insert with non-existent user." );
    }

    for ( UserAuthorityEntity userAuthority : userAuthorises ) {
      if ( userAuthority.getStatus() != NORMAL.getStatus() ) {
        throw new SqlOperationException( "Cannot insert within deleted user's authorities detail." );
      }
    }

    List<RoleEntity> roles = roleMapper.queryBySpecificId( roleIds,
        NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal()
    );

    if ( roleIds.size() != roles.size() ) {
      throw new SqlOperationException( "Cannot insert with non-existent role" );
    }

    for ( RoleEntity role : roles ) {
      if ( role.getStatus() != NORMAL.getStatus() ) {
        throw new SqlOperationException( "Cannot insert within deleted role." );
      }
    }

    return super.saveAll( userRoleEntities );
  }

  public List<UserRoleEntity> queryByAuthorityId( String authorityId, int status ) {
    return userRoleMapper.queryByAuthorityId( authorityId, status );
  }

  @Transactional
  public int grantRolesTo( String authorityId, List<String> roleIds ) {
    UserAuthorityEntity userAuthority = new UserAuthorityEntity( authorityId );

    Map<String, String> mapping = new HashMap<>();
    List<String> existRoles = new ArrayList<>();
    List<String> normalRoles = new ArrayList<>();
    List<UserRoleEntity> notExistRole = new ArrayList<>();
    List<UserRoleEntity> userRoleEntities = queryByAuthorityId( authorityId, NOT_STATUS_FLAG.getVal() );

    userRoleEntities.forEach( entity -> {
      String roleId = entity.getRole().getId(),
          entityId = entity.getId();

      existRoles.add( roleId );
      mapping.put( roleId, entityId );

      if ( entity.getStatus() == NORMAL.getStatus() ) {
        normalRoles.add( entityId );
      }
    } );

    List<String> updates = roleIds.stream().filter( id -> {
      boolean isExist = existRoles.contains( id );

      if ( !isExist ) {
        notExistRole.add( new UserRoleEntity()
            .setUserAuthority( userAuthority )
            .setRole( new RoleEntity( id ) )
        );
      }

      return isExist;
    } ).map( mapping::get ).collect( toList() );

    List<String> deletes = CollectionUtils.complement(
        updates, CollectionUtils.union( normalRoles, updates )
    );

    int[] effectRow = new int[]{ 0 };
    if ( !notExistRole.isEmpty() ) {
      effectRow[ 0 ] += saveAll( notExistRole );
    }

    if ( !updates.isEmpty() ) {
      userRoleEntities.stream()
          .filter( entity -> updates.contains( entity.getId() ) )
          .forEach( entity -> effectRow[ 0 ] += update( entity.setStatus( NORMAL.getStatus() ).setDeleteTime( -1 ) ) );
    }

    if ( !deletes.isEmpty() ) {
      effectRow[ 0 ] += removeAll( deletes, System.currentTimeMillis() );
    }

    return effectRow[ 0 ];
  }
}
