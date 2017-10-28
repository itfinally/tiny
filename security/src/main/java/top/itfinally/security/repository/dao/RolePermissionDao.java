package top.itfinally.security.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.core.exception.SqlOperationException;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.PermissionEntity;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.RolePermissionEntity;
import top.itfinally.security.repository.mapper.PermissionMapper;
import top.itfinally.security.repository.mapper.RoleMapper;
import top.itfinally.security.repository.mapper.RolePermissionMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        for ( RoleEntity role : roles ) {
            if ( role.getStatus() != NORMAL.getStatus() ) {
                throw new SqlOperationException( "Cannot insert within deleted role." );
            }
        }

        List<PermissionEntity> permissions = permissionMapper.queryBySpecificId( permissionIds,
                NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal()
        );

        if ( permissionIds.size() != permissions.size() ) {
            throw new SqlOperationException( "Cannot insert with non-existent permission." );
        }

        for ( PermissionEntity permission : permissions ) {
            if ( permission.getStatus() != NORMAL.getStatus() ) {
                throw new SqlOperationException( "Cannot insert within deleted permission." );
            }
        }

        return super.saveAll( rolePermissionEntities );
    }


    public List<RolePermissionEntity> queryByRoleId( String roleId ) {
        return rolePermissionMapper.queryByRoleId( roleId );
    }
}
