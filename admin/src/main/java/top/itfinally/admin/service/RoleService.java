package top.itfinally.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.RoleEnhancedDao;
import top.itfinally.admin.repository.dao.UserDetailsDao;
import top.itfinally.admin.repository.po.UserDetailsEntity;
import top.itfinally.admin.support.SecurityUtils;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.dao.RoleDao;
import top.itfinally.security.repository.dao.RolePermissionDao;
import top.itfinally.security.repository.dao.UserRoleDao;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.UserRoleEntity;
import top.itfinally.security.web.vo.RoleVoBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Service
public class RoleService {

    private RolePermissionDao rolePermissionDao;
    private RoleEnhancedDao roleEnhancedDao;
    private UserDetailsDao userDetailsDao;
    private UserRoleDao userRoleDao;
    private RoleDao roleDao;

    @Autowired
    public RoleService setRolePermissionDao( RolePermissionDao rolePermissionDao ) {
        this.rolePermissionDao = rolePermissionDao;
        return this;
    }

    @Autowired
    public RoleService setRoleEnhancedDao( RoleEnhancedDao roleEnhancedDao ) {
        this.roleEnhancedDao = roleEnhancedDao;
        return this;
    }

    @Autowired
    public RoleService setUserDetailsDao( UserDetailsDao userDetailsDao ) {
        this.userDetailsDao = userDetailsDao;
        return this;
    }

    @Autowired
    public RoleService setUserRoleDao( UserRoleDao userRoleDao ) {
        this.userRoleDao = userRoleDao;
        return this;
    }

    @Autowired
    public RoleService setRoleDao( RoleDao roleDao ) {
        this.roleDao = roleDao;
        return this;
    }

    public CollectionResponseVoBean<RoleVoBean> queryByMultiCondition( Map<String, Object> condition, int beginRow, int row ) {
        List<RoleEntity> result = roleEnhancedDao.queryByMultiCondition( condition, beginRow, row );
        ResponseStatusEnum status = result.isEmpty() ? EMPTY_RESULT : SUCCESS;

        return new CollectionResponseVoBean<RoleVoBean>( status ).setResult( result.stream()
                .filter( item -> !"admin".equals( item.getName().toLowerCase() ) )
                .map( RoleVoBean::new )
                .collect( Collectors.toList() ) );
    }

    public SingleResponseVoBean<Integer> countByMultiCondition( Map<String, Object> condition ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleEnhancedDao.countByMultiCondition( condition ) );
    }

    public SingleResponseVoBean<Integer> updateRoleStatus( List<String> roleIds, int status ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleEnhancedDao.updateRoleStatus( roleIds, status ) );
    }

    public SingleResponseVoBean<Integer> updateRoleDetail( String id, String name, String description, int status ) {
        RoleEntity role = roleDao.query( id );
        if ( null == role ) {
            return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST ).setMessage( String.format( "Role '%s' not found.", id ) );
        }

        role.setName( name ).setDescription( description ).setStatus( status )
                .setDeleteTime( DELETE.getStatus() == status ? System.currentTimeMillis() : -1 );

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleDao.update( role ) );
    }

    public CollectionResponseVoBean<RoleVoBean> getRoles() {
        RoleEntity role = SecurityUtils.getMaxRoleWithGrant( roleId -> rolePermissionDao.queryByRoleId( roleId, NORMAL.getStatus() ) );

        List<RoleEntity> roleEntities = "ROLE_ADMIN".equals( role.getAuthority() )
                ? roleDao.queryAll( NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal() )
                : roleEnhancedDao.queryLowLevelRoles( role.getPriority() );

        ResponseStatusEnum status = roleEntities.isEmpty() ? EMPTY_RESULT : SUCCESS;

        return new CollectionResponseVoBean<RoleVoBean>( status ).setResult( roleEntities.stream()
                .map( RoleVoBean::new ).collect( Collectors.toList() ) );
    }

    public CollectionResponseVoBean<RoleVoBean> getSpecificUserRoles( String userId ) {
        UserDetailsEntity user = userDetailsDao.query( userId );
        if ( null == user ) {
            return new CollectionResponseVoBean<RoleVoBean>( ILLEGAL_REQUEST ).setMessage( String.format( "User '%s' not found.", userId ) );
        }

        List<RoleVoBean> result = userRoleDao.queryByAuthorityId( user.getAuthorityId(), NORMAL.getStatus() ).stream()
                .map( item -> new RoleVoBean( item.getRole() ) ).collect( Collectors.toList() );

        ResponseStatusEnum status = result.isEmpty() ? EMPTY_RESULT : SUCCESS;

        return new CollectionResponseVoBean<RoleVoBean>( status ).setResult( result );
    }

    public SingleResponseVoBean<Integer> grantPermissionsTo( String roleId, List<String> permissionIds ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( rolePermissionDao.grantPermissionsTo( roleId, permissionIds ) );
    }
}
