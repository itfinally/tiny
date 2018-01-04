package top.itfinally.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.RoleMenuItemDao;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.repository.po.BaseEntity;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.dao.RoleDao;
import top.itfinally.security.repository.dao.RolePermissionDao;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.web.vo.RoleVoBean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;

@Service
public class MenuRoleService {

    private RolePermissionDao rolePermissionDao;
    private RoleMenuItemDao roleMenuItemDao;
    private RoleDao roleDao;

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
    public MenuRoleService setRoleDao( RoleDao roleDao ) {
        this.roleDao = roleDao;
        return this;
    }

    private RoleEntity getMaxRoleWithGrant() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<RoleEntity> roles = auth.getAuthorities().stream()
                .map( role -> ( RoleEntity ) role )

                // 0, 1, 2, 3, 4
                .sorted( ( o1, o2 ) -> o1.getPriority() > o2.getPriority() ? 1 : -1 )
                .collect( Collectors.toList() );

        // getting the maximum role with grant permission
        RoleEntity[] matchRole = new RoleEntity[]{ null };
        boolean ignore = roles.stream().anyMatch( role -> {
            if ( "ROLE_ADMIN".equals( role.getAuthority() ) || rolePermissionDao.queryByRoleId( role.getId() )
                    .stream().anyMatch( rp -> "grant".equals( rp.getPermission().getName() ) ) ) {

                matchRole[ 0 ] = role;
                return true;
            }

            return false;
        } );

        return matchRole[ 0 ];
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
        List<RoleEntity> roles = roleDao.queryByPriority( getMaxRoleWithGrant().getPriority() );

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
