package top.itfinally.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.exception.UserNotFoundException;
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

    private AbstractUserDetailService abstractUserDetailService;
    private PermissionValidService permissionValidService;
    private RolePermissionDao rolePermissionDao;
    private UserAuthorityDao userAuthorityDao;
    private PermissionDao permissionDao;
    private UserRoleDao userRoleDao;
    private RoleDao roleDao;

    @Autowired
    public AuthorizationService setAbstractUserDetailService( AbstractUserDetailService abstractUserDetailService ) {
        this.abstractUserDetailService = abstractUserDetailService;
        return this;
    }

    @Autowired
    public AuthorizationService setPermissionValidService( PermissionValidService permissionValidService ) {
        this.permissionValidService = permissionValidService;
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

    public SingleResponseVoBean<Integer> addRole( RoleEntity role ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleDao.save( role ) );
    }

    public SingleResponseVoBean<Integer> addPermission( PermissionEntity permission ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( permissionDao.save( permission ) );
    }

    public SingleResponseVoBean<Integer> grantPermissionsTo( String roleId, List<String> permissionIds ) {
        int effectRow = rolePermissionDao.grantPermissionsTo( roleId, permissionIds );
        permissionValidService.refreshRolePermission( roleId );

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
    }

    public SingleResponseVoBean<Integer> grantRolesTo( String authorityId, List<String> roleIds ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userRoleDao.grantRolesTo( authorityId, roleIds ) );
    }

    public CollectionResponseVoBean<RoleVoBean> getRoles() {
        List<RoleEntity> roles = roleDao.queryAll( NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal() );

        ResponseStatusEnum status = roles.isEmpty() ? EMPTY_RESULT : SUCCESS;
        return new CollectionResponseVoBean<RoleVoBean>( status )
                .setResult( roles.stream().map( RoleVoBean::new ).collect( Collectors.toList() ) );
    }

    public CollectionResponseVoBean<PermissionVoBean> getPermissions() {
        List<PermissionEntity> permissions = permissionDao.queryAll( NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal() );

        ResponseStatusEnum status = permissions.isEmpty() ? EMPTY_RESULT : SUCCESS;
        return new CollectionResponseVoBean<PermissionVoBean>( status )
                .setResult( permissions.stream().map( PermissionVoBean::new ).collect( Collectors.toList() ) );
    }
}
