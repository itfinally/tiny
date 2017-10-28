package top.itfinally.security.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.core.exception.SqlOperationException;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.repository.po.UserRoleEntity;
import top.itfinally.security.repository.mapper.RoleMapper;
import top.itfinally.security.repository.mapper.UserAuthorityMapper;
import top.itfinally.security.repository.mapper.UserRoleMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Set<String> userIds = new HashSet<>();
        Set<String> roleIds = new HashSet<>();

        userRoleEntities.forEach( entity -> {
            userIds.add( entity.getUserAuthority().getId() );
            roleIds.add( entity.getRole().getId() );
        } );

        List<UserAuthorityEntity> userAuthorises = userAuthorityMapper.queryBySpecificId( userIds,
                NOT_PAGING.getVal(),
                NOT_PAGING.getVal(),
                NOT_STATUS_FLAG.getVal()
        );

        if ( userIds.size() != userAuthorises.size() ) {
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

    public List<UserRoleEntity> queryByAuthorityId( String authorityId ) {
        return userRoleMapper.queryByAuthorityId( authorityId );
    }
}
