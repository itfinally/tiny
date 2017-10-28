package top.itfinally.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.core.enumerate.DataStatusEnum;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.exception.UserNotFoundException;
import top.itfinally.core.util.CollectionUtils;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.*;
import top.itfinally.security.repository.dao.*;
import top.itfinally.security.web.vo.PermissionVoBean;
import top.itfinally.security.web.vo.RoleVoBean;

import java.util.*;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Service
public class AuthorizationService {

    private UserDetailService userDetailService;
    private RolePermissionDao rolePermissionDao;
    private UserAuthorityDao userAuthorityDao;
    private PermissionDao permissionDao;
    private UserRoleDao userRoleDao;
    private RoleDao roleDao;

    @Autowired
    public AuthorizationService setUserDetailService( UserDetailService userDetailService ) {
        this.userDetailService = userDetailService;
        return this;
    }

    @Autowired
    public AuthorizationService setUserAuthorityDao( UserAuthorityDao userAuthorityDao ) {
        this.userAuthorityDao = userAuthorityDao;
        return this;
    }

    @Autowired
    public AuthorizationService setRoleDao( RoleDao roleDao ) {
        this.roleDao = roleDao;
        return this;
    }

    @Autowired
    public AuthorizationService setPermissionDao( PermissionDao permissionDao ) {
        this.permissionDao = permissionDao;
        return this;
    }

    @Autowired
    public AuthorizationService setRolePermissionDao( RolePermissionDao rolePermissionDao ) {
        this.rolePermissionDao = rolePermissionDao;
        return this;
    }

    @Autowired
    public AuthorizationService setUserRoleDao( UserRoleDao userRoleDao ) {
        this.userRoleDao = userRoleDao;
        return this;
    }

    @Transactional
    public SingleResponseVoBean<Integer> register( String userId ) {
        UserDetailsEntity user = userDetailService.loadUserById( userId );
        UserAuthorityEntity userAuthority = new UserAuthorityEntity();

        if ( null == user ) {
            throw new UserNotFoundException( String.format( "User not found, %s is not exists.", userId ) );
        }

        int effectRow = 0;
        effectRow += userAuthorityDao.save( userAuthority );
        effectRow += userDetailService.save( user.setAuthorityId( userAuthority.getId() ) );

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
    }

    public SingleResponseVoBean<Integer> addRole( RoleEntity role ) {
        return new SingleResponseVoBean<Integer>( SUCCESS )
                .setResult( roleDao.save( role ) );
    }

    public SingleResponseVoBean<Integer> addPermission( PermissionEntity permission ) {
        return new SingleResponseVoBean<Integer>( SUCCESS )
                .setResult( permissionDao.save( permission ) );
    }

    @Transactional
    public SingleResponseVoBean<Integer> grantPermissionsTo( String roleId, List<String> permissionIds ) {
        RoleEntity role = new RoleEntity( roleId );

        Map<String, String> mapping = new HashMap<>();
        Set<String> existPermissions = new HashSet<>();
        List<String> normalPermissions = new ArrayList<>();
        List<RolePermissionEntity> notExistPermission = new ArrayList<>();
        List<RolePermissionEntity> rolePermissionEntities = rolePermissionDao.queryByRoleId( roleId );

        rolePermissionEntities.forEach( entity -> {
            String permissionId = entity.getPermission().getId(),
                    entityId = entity.getId();

            existPermissions.add( permissionId );
            mapping.put( permissionId, entityId );

            if ( entity.getStatus() == DataStatusEnum.NORMAL.getStatus() ) {
                normalPermissions.add( entityId );
            }
        } );

        // This is role-permission's id list
        // The non-exist role-permission relationship has been removed.
        List<String> updates = permissionIds.stream().filter( id -> {
            boolean isExist = existPermissions.contains( id );

            if ( !isExist ) {
                notExistPermission.add( new RolePermissionEntity()
                        .setRole( role )
                        .setPermission( new PermissionEntity( id ) )
                );
            }

            return isExist;
        } ).map( mapping::get ).collect( Collectors.toList() );

        // has A, B, C, D, E, F | in > updates A, B, C ( recover ), F ( non-change )
        // normalPermissions --> D, E, F ( normal ) | updates --> A, B, C, F ( recover & non-change )
        // updates complement --> D, E ( delete )
        List<String> deletes = CollectionUtils.complement(
                updates, CollectionUtils.union( normalPermissions, updates )
        );

        int effectRow = 0;
        if ( !notExistPermission.isEmpty() ) {
            effectRow += rolePermissionDao.saveAll( notExistPermission );
        }

        if ( !updates.isEmpty() ) {
            effectRow += rolePermissionDao.updateAll(
                    rolePermissionEntities.stream()
                            .filter( entity -> updates.contains( entity.getId() ) )
                            .map( entity -> entity.setStatus( DataStatusEnum.NORMAL.getStatus() ) )
                            .collect( Collectors.toList() )
            );
        }

        if ( !deletes.isEmpty() ) {
            effectRow += rolePermissionDao.removeAll( deletes, System.currentTimeMillis() );
        }

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
    }

    @Transactional
    public SingleResponseVoBean<Integer> grantRolesTo( String authorityId, List<String> roleIds ) {
        UserAuthorityEntity userAuthority = new UserAuthorityEntity( authorityId );

        Map<String, String> mapping = new HashMap<>();
        List<String> existRoles = new ArrayList<>();
        List<String> normalRoles = new ArrayList<>();
        List<UserRoleEntity> notExistRole = new ArrayList<>();
        List<UserRoleEntity> userRoleEntities = userRoleDao.queryByAuthorityId( authorityId );

        userRoleEntities.forEach( entity -> {
            String roleId = entity.getRole().getId(),
                    entityId = entity.getId();

            existRoles.add( roleId );
            mapping.put( roleId, entityId );

            if ( entity.getStatus() == DataStatusEnum.NORMAL.getStatus() ) {
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
        } ).map( mapping::get ).collect( Collectors.toList() );

        List<String> deletes = CollectionUtils.complement(
                updates, CollectionUtils.union( normalRoles, updates )
        );

        int effectRow = 0;
        if ( !notExistRole.isEmpty() ) {
            effectRow += userRoleDao.saveAll( notExistRole );
        }

        if ( !updates.isEmpty() ) {
            effectRow += userRoleDao.updateAll(
                    userRoleEntities.stream()
                            .filter( entity -> updates.contains( entity.getId() ) )
                            .map( entity -> entity.setStatus( DataStatusEnum.NORMAL.getStatus() ) )
                            .collect( Collectors.toList() )
            );
        }

        if ( !deletes.isEmpty() ) {
            effectRow += userRoleDao.removeAll( deletes, System.currentTimeMillis() );
        }

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
    }

    public CollectionResponseVoBean<RoleVoBean> getRoles() {
        List<RoleEntity> roles = roleDao.queryAll(
                NOT_PAGING.getVal(),
                NOT_PAGING.getVal(),
                NOT_STATUS_FLAG.getVal()
        );

        ResponseStatusEnum status = roles.isEmpty() ? EMPTY_RESULT : SUCCESS;
        return new CollectionResponseVoBean<RoleVoBean>( status )
                .setResult( roles.stream().map( RoleVoBean::new ).collect( Collectors.toList() ) );
    }

    public CollectionResponseVoBean<PermissionVoBean> getPermissions() {
        List<PermissionEntity> permissions = permissionDao.queryAll(
                NOT_PAGING.getVal(),
                NOT_PAGING.getVal(),
                NOT_STATUS_FLAG.getVal()
        );

        ResponseStatusEnum status = permissions.isEmpty() ? EMPTY_RESULT : SUCCESS;
        return new CollectionResponseVoBean<PermissionVoBean>( status )
                .setResult( permissions.stream().map( PermissionVoBean::new ).collect( Collectors.toList() ) );
    }
}
