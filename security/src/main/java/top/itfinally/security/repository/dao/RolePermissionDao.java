package top.itfinally.security.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.core.exception.SqlOperationException;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.core.util.CollectionUtils;
import top.itfinally.security.repository.po.PermissionEntity;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.RolePermissionEntity;
import top.itfinally.security.repository.mapper.PermissionMapper;
import top.itfinally.security.repository.mapper.RoleMapper;
import top.itfinally.security.repository.mapper.RolePermissionMapper;

import java.util.*;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Repository
public class RolePermissionDao extends AbstractDao<RolePermissionEntity, RolePermissionMapper> {
    private RoleMapper roleMapper;
    private PermissionMapper permissionMapper;
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    public RolePermissionDao setRoleMapper( RoleMapper roleMapper ) {
        this.roleMapper = roleMapper;
        return this;
    }

    @Autowired
    public RolePermissionDao setPermissionMapper( PermissionMapper permissionMapper ) {
        this.permissionMapper = permissionMapper;
        return this;
    }

    @Override
    @Autowired
    protected void setBaseMapper( RolePermissionMapper baseMapper ) {
        this.rolePermissionMapper = baseMapper;
        super.setBaseMapper( baseMapper );
    }

    @Override
    public int save( RolePermissionEntity entity ) {
        RoleEntity role = roleMapper.query( entity.getRole().getId() );
        PermissionEntity permission = permissionMapper.query( entity.getPermission().getId() );

        if ( null == role || null == permission ) {
            throw new SqlOperationException( "Cannot insert with non-existent value.( check your role and permission )" );
        }

        if ( !NORMAL.expect( role.getStatus(), permission.getStatus() ) ) {
            throw new SqlOperationException( "Cannot insert within deleted value." );
        }

        return super.save( entity );
    }

    @Override
    public int saveAll( Collection<RolePermissionEntity> rolePermissionEntities ) {
        Set<String> roleIds = new HashSet<>();
        Set<String> permissionIds = new HashSet<>();

        rolePermissionEntities.forEach( entity -> {
            roleIds.add( entity.getRole().getId() );
            permissionIds.add( entity.getPermission().getId() );
        } );

        List<RoleEntity> roles = roleMapper.queryBySpecificId( roleIds,
                NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal()
        );

        if ( roleIds.size() != roles.size() ) {
            throw new SqlOperationException( "Cannot insert with non-existent role." );
        }

        roles.forEach( role -> {
            if ( role.getStatus() != NORMAL.getStatus() ) {
                throw new SqlOperationException( "Cannot insert within deleted role." );
            }
        } );

        List<PermissionEntity> permissions = permissionMapper.queryBySpecificId( permissionIds,
                NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal()
        );

        if ( permissionIds.size() != permissions.size() ) {
            throw new SqlOperationException( "Cannot insert with non-existent permission." );
        }

        permissions.forEach( permission -> {
            if ( permission.getStatus() != NORMAL.getStatus() ) {
                throw new SqlOperationException( "Cannot insert within deleted permission." );
            }
        } );

        return super.saveAll( rolePermissionEntities );
    }

    public List<RolePermissionEntity> queryByRoleId( String roleId, int status ) {
        return rolePermissionMapper.queryByRoleId( roleId, status );
    }

    @Transactional
    public int grantPermissionsTo( String roleId, List<String> permissionIds ) {
        RoleEntity role = new RoleEntity( roleId );

        Map<String, String> mapping = new HashMap<>();
        Set<String> existPermissions = new HashSet<>();
        List<String> normalPermissions = new ArrayList<>();
        List<RolePermissionEntity> notExistPermission = new ArrayList<>();
        List<RolePermissionEntity> rolePermissions = queryByRoleId( roleId, NOT_STATUS_FLAG.getVal() );

        rolePermissions.forEach( entity -> {
            String permissionId = entity.getPermission().getId(), entityId = entity.getId();

            existPermissions.add( permissionId );
            mapping.put( permissionId, entityId );

            if ( entity.getStatus() == NORMAL.getStatus() ) {
                normalPermissions.add( entityId );
            }
        } );

        // This is role-permission's id list
        // The non-exist role-permission relationship has been removed.
        Set<String> updates = permissionIds.stream().filter( id -> {
            boolean isExist = existPermissions.contains( id );

            if ( !isExist ) {
                notExistPermission.add( new RolePermissionEntity()
                        .setRole( role )
                        .setPermission( new PermissionEntity( id ) )
                );
            }

            return isExist;
        } ).map( mapping::get ).collect( Collectors.toSet() );

        // has A, B, C, D, E, F | in > updates A, B, C ( recover ), F ( non-change )
        // normalPermissions --> D, E, F ( normal ) | updates --> A, B, C, F ( recover & non-change )
        // updates complement --> D, E ( delete )
        List<String> deletes = CollectionUtils.complement(
                updates, CollectionUtils.union( normalPermissions, updates )
        );

        int[] effectRow = new int[]{ 0 };
        if ( !notExistPermission.isEmpty() ) {
            effectRow[ 0 ] += saveAll( notExistPermission );
        }

        if ( !updates.isEmpty() ) {
            rolePermissions.stream()
                    .filter( entity -> updates.contains( entity.getId() ) )
                    .forEach( entity -> effectRow[ 0 ] += update( entity.setStatus( NORMAL.getStatus() ) ) );
        }

        if ( !deletes.isEmpty() ) {
            effectRow[ 0 ] += removeAll( deletes, System.currentTimeMillis() );
        }

        return effectRow[ 0 ];
    }
}
