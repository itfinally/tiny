package top.itfinally.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.RoleEnhancedDao;
import top.itfinally.admin.repository.dao.RoleMenuItemDao;
import top.itfinally.admin.support.SecurityUtils;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.repository.po.BaseEntity;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.dao.RolePermissionDao;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.web.vo.RoleVoBean;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;

@Service
public class MenuRoleService {

    private RolePermissionDao rolePermissionDao;
    private RoleMenuItemDao roleMenuItemDao;
    private RoleEnhancedDao roleEnhancedDao;

    @Autowired
    public MenuRoleService setRolePermissionDao( RolePermissionDao rolePermissionDao ) {
        this.rolePermissionDao = rolePermissionDao;
        return this;
    }

    @Autowired
    public MenuRoleService setRoleMenuItemDao( RoleMenuItemDao roleMenuItemDao ) {
        this.roleMenuItemDao = roleMenuItemDao;
        return this;
    }

    @Autowired
    public MenuRoleService setRoleEnhancedDao( RoleEnhancedDao roleEnhancedDao ) {
        this.roleEnhancedDao = roleEnhancedDao;
        return this;
    }

    @PreAuthorize( "hasPermission( null, 'grant' )" )
    public CollectionResponseVoBean<RoleVoBean> queryMenuItemRoles( String menuId ) {
        List<RoleEntity> roles = roleMenuItemDao.queryMenuItemRoles( menuId );
        ResponseStatusEnum status = roles.isEmpty() ? SUCCESS : EMPTY_RESULT;

        return new CollectionResponseVoBean<RoleVoBean>( status )
                .setResult( roles.stream().map( RoleVoBean::new ).collect( Collectors.toList() ) );
    }

    @PreAuthorize( "hasPermission( null, 'grant' )" )
    public CollectionResponseVoBean<RoleEntity> queryAvailableRole( String menuId ) {
        List<RoleEntity> roles = roleEnhancedDao.queryLowLevelRoles( SecurityUtils.getMaxRoleWithGrant(
                roleId -> rolePermissionDao.queryByRoleId( roleId, NORMAL.getStatus() ) ).getPriority() );

        Set<String> roleIds = roleMenuItemDao.queryMenuItemRoles( menuId ).stream()
                .map( BaseEntity::getId ).collect( Collectors.toSet() );

        List<RoleEntity> availableRoles = roles.stream()
                .filter( role -> !roleIds.contains( role.getId() ) )
                .collect( Collectors.toList() );

        ResponseStatusEnum status = availableRoles.isEmpty() ? EMPTY_RESULT : SUCCESS;
        return new CollectionResponseVoBean<RoleEntity>( status ).setResult( availableRoles );
    }

    @PreAuthorize( "hasPermission( null, 'grant' )" )
    public SingleResponseVoBean<Integer> addRoleMenu( String menuItemId, List<String> roleIds ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleMenuItemDao.addRoleMenu( menuItemId, roleIds ) );
    }

    @PreAuthorize( "hasPermission( null, 'grant' )" )
    public SingleResponseVoBean<Integer> removeRoleMenu( String menuItemId, List<String> roleIds ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( roleMenuItemDao.removeRoleMenu( menuItemId, roleIds ) );
    }
}
