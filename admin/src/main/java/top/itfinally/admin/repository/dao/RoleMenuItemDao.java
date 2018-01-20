package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.admin.repository.mapper.MenuRelationMapper;
import top.itfinally.admin.repository.mapper.RoleMenuItemMapper;
import top.itfinally.admin.repository.po.MenuItemEntity;
import top.itfinally.admin.repository.po.MenuRelationEntity;
import top.itfinally.admin.repository.po.RoleMenuItemEntity;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.RoleEntity;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Repository
public class RoleMenuItemDao extends AbstractDao<RoleMenuItemEntity, RoleMenuItemMapper> {
    private RoleMenuItemMapper roleMenuItemMapper;
    private MenuRelationMapper menuRelationMapper;

    @Autowired
    public RoleMenuItemDao setRoleMenuItemMapper( RoleMenuItemMapper roleMenuItemMapper ) {
        this.roleMenuItemMapper = roleMenuItemMapper;
        return this;
    }

    @Autowired
    public RoleMenuItemDao setMenuRelationMapper( MenuRelationMapper menuRelationMapper ) {
        this.menuRelationMapper = menuRelationMapper;
        return this;
    }

    @Override
    @Autowired
    protected void setBaseMapper( RoleMenuItemMapper baseMapper ) {
        this.roleMenuItemMapper = baseMapper;
        super.setBaseMapper( baseMapper );
    }

    public List<MenuItemEntity> queryRoleMenuItem( String roleId ) {
        return roleMenuItemMapper.queryByRoleId( roleId, NORMAL.getStatus() ).stream()
                .map( RoleMenuItemEntity::getMenuItem )
                .collect( Collectors.toList() );
    }

    public List<MenuItemEntity> queryMenuItems( List<String> roleIds ) {
        return roleMenuItemMapper.queryByMultiRoleIds( roleIds, NORMAL.getStatus() ).stream()
                .map( RoleMenuItemEntity::getMenuItem ).distinct().collect( Collectors.toList() );
    }

    public List<RoleEntity> queryMenuItemRoles( String menuId ) {
        return roleMenuItemMapper.queryByMenuId( menuId, NORMAL.getStatus() ).stream()
                .map( RoleMenuItemEntity::getRole ).collect( Collectors.toList() );
    }

    @Transactional
    public int addRoleMenu( String menuItemId, List<String> roleIds ) {
        List<MenuRelationEntity> parents = menuRelationMapper.queryParentItem( menuItemId, NOT_STATUS_FLAG.getVal() );
        List<String> menuItemIds = parents.stream().map( item -> item.getParent().getId() ).distinct().collect( Collectors.toList() );

        if ( isUnreachable( parents ) || roleIds.stream().anyMatch( id -> isUnauthorized( parents, id ) ) ) {
            throw new IllegalStateException( "An unreachable item appears, operation abort." );
        }

        return changeRoleMenu( roleIds, menuItemIds, item -> item.setStatus( NORMAL.getStatus() ).setDeleteTime( -1 ) );
    }

    @Transactional
    public int removeRoleMenu( String menuItemId, List<String> roleIds ) {
        long now = System.currentTimeMillis();
        List<String> menuItemIds = menuRelationMapper.queryChildItem( menuItemId, NOT_STATUS_FLAG.getVal() )
                .stream().map( item -> item.getChild().getId() ).distinct().collect( Collectors.toList() );

        List<MenuRelationEntity> parents = menuRelationMapper.queryParentItem( menuItemId, NOT_STATUS_FLAG.getVal() );
        if ( isUnreachable( parents ) || roleIds.stream().anyMatch( id -> isUnauthorized( parents, id ) ) ) {
            throw new IllegalStateException( "An unreachable item appears, operation abort." );
        }

        return changeRoleMenu( roleIds, menuItemIds, item -> item.setStatus( DELETE.getStatus() )
                .setUpdateTime( now ).setDeleteTime( now ) );
    }

    private int changeRoleMenu( List<String> roleIds, List<String> menuItemIds, Function<RoleMenuItemEntity, RoleMenuItemEntity> decorator ) {
        int[] effectiveRow = new int[]{ 0 };
        roleIds.forEach( roleId -> {
            Map<String, RoleMenuItemEntity> exists = roleMenuItemMapper

                    .queryRoleMenuItemChain( NOT_STATUS_FLAG.getVal(), roleId, menuItemIds )

                    .stream().collect( Collectors.toMap( item -> item.getMenuItem().getId(), item -> item ) );

            List<RoleMenuItemEntity> theNews = menuItemIds.stream()

                    .filter( item -> !exists.containsKey( item ) )

                    .map( itemId -> decorator.apply( new RoleMenuItemEntity()
                            .setRole( new RoleEntity( roleId ) )
                            .setMenuItem( new MenuItemEntity( itemId ) ) ) )

                    .collect( Collectors.toList() );

            // add
            if ( !theNews.isEmpty() ) {
                effectiveRow[ 0 ] += roleMenuItemMapper.saveAll( theNews );
            }

            // update
            menuItemIds.stream().filter( exists::containsKey )

                    .map( itemId -> decorator.apply( exists.get( itemId ) ) )

                    .forEach( item -> effectiveRow[ 0 ] += roleMenuItemMapper.update( item ) );
        } );

        return effectiveRow[ 0 ];
    }

    // if this item is reachable, it should be able to trace to the root item
    // or else as illegal operation
    //
    // return true if unreachable.
    private boolean isUnreachable( List<MenuRelationEntity> itemChain ) {
        if ( itemChain.isEmpty() ) {
            return true;
        }

        // Note: itemChain.get( 0 ).getChildId()
        // if gap is zero, then child and parent are self
        if ( 1 == itemChain.size() && 0 == itemChain.get( 0 ).getGap() ) {
            MenuItemEntity item = itemChain.get( 0 ).getChild();
            return null == item || !item.isRoot();
        }

        Map<Integer, MenuRelationEntity> itemFloorChain = new HashMap<>();
        itemChain.forEach( item -> itemFloorChain.put( item.getGap(), item ) );

        List<Integer> gaps = new ArrayList<>( itemFloorChain.keySet() );

        // from large to small
        gaps.sort( ( a, b ) -> a > b ? -1 : 1 );

        int last = itemFloorChain.size() - 1;
        if ( !itemFloorChain.containsKey( last ) || !itemFloorChain.get( last ).getParent().isRoot() ) {
            return true;
        }

        int[] count = new int[]{ gaps.get( 0 ) };
        return gaps.stream().skip( 1 ).anyMatch( gap -> {
            MenuRelationEntity relation = itemFloorChain.get( gap );
            if ( count[ 0 ] - 1 != relation.getGap() ) {
                return true;
            }

            count[ 0 ] = gap;
            return false;
        } );
    }

    // if item chain is not recorded under specified role, also illegal operation too.
    private boolean isUnauthorized( List<MenuRelationEntity> itemChain, String roleId ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream().noneMatch( role -> "ROLE_ADMIN".equals( role.getAuthority() ) )

                && roleMenuItemMapper.queryRoleMenuItemChain( NORMAL.getStatus(), roleId, itemChain.stream()
                .map( item -> item.getParent().getId() ).collect( Collectors.toList() ) )
                .size() != itemChain.size();
    }
}
