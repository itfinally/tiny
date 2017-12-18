package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.admin.repository.mapper.MenuItemMapper;
import top.itfinally.admin.repository.mapper.MenuRelationMapper;
import top.itfinally.admin.repository.mapper.RoleMenuItemMapper;
import top.itfinally.admin.repository.po.MenuItemEntity;
import top.itfinally.admin.repository.po.MenuRelationEntity;
import top.itfinally.admin.repository.po.RoleMenuItemEntity;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.core.repository.po.BaseEntity;
import top.itfinally.core.util.CollectionUtils;
import top.itfinally.security.repository.po.RoleEntity;

import java.util.*;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;

@Repository
public class RoleMenuItemDao extends AbstractDao<RoleMenuItemEntity, RoleMenuItemMapper> {
    private RoleMenuItemMapper roleMenuItemMapper;
    private MenuRelationMapper menuRelationMapper;
    private MenuItemMapper menuItemMapper;

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

    @Autowired
    public RoleMenuItemDao setMenuItemMapper( MenuItemMapper menuItemMapper ) {
        this.menuItemMapper = menuItemMapper;
        return this;
    }

    @Override
    @Autowired
    protected void setBaseMapper( RoleMenuItemMapper baseMapper ) {
        this.roleMenuItemMapper = baseMapper;
        super.setBaseMapper( baseMapper );
    }

    public List<MenuItemEntity> queryRoleMenuItem( String roleId ) {
        return roleMenuItemMapper.queryRoleMenuItem( roleId ).stream()
                .map( RoleMenuItemEntity::getMenuItem )
                .collect( Collectors.toList() );
    }

    public List<RoleEntity> queryMenuItemRoles( String menuId ) {
        return roleMenuItemMapper.queryMenuItemRoles( menuId ).stream()
                .map( RoleMenuItemEntity::getRole )
                .collect( Collectors.toList() );
    }

    @Transactional
    public int changeRoleMenu( List<String> menuItemIds, RoleEntity role ) {
        if ( menuItemIds.stream().anyMatch( itemId -> this.isUnreachableOrUnauthorized( itemId, role.getId() ) ) ) {
            throw new IllegalStateException( "An unreachable item appears, operation abort." );
        }

        List<RoleMenuItemEntity> roleMenuItems = roleMenuItemMapper.queryRoleMenuItem( role.getId() );

        Map<String, String> mapping = new HashMap<>( roleMenuItems.size() );
        Set<String> existRoleItemIds = new HashSet<>( roleMenuItems.size() );
        List<String> normalRoleItems = new ArrayList<>( roleMenuItems.size() );
        List<RoleMenuItemEntity> notExistRoleItems = new ArrayList<>( roleMenuItems.size() );

        roleMenuItems.forEach( entity -> {
            String itemId = entity.getMenuItem().getId(), entityId = entity.getId();

            existRoleItemIds.add( entityId );
            mapping.put( itemId, entityId );

            if ( entity.getStatus() == NORMAL.getStatus() ) {
                normalRoleItems.add( entityId );
            }
        } );

        Set<String> updates = menuItemIds.stream().filter( itemId -> {
            boolean isExist = existRoleItemIds.contains( itemId );

            if ( !isExist ) {
                notExistRoleItems.add( new RoleMenuItemEntity()
                        .setMenuItem( new MenuItemEntity( itemId ) )
                        .setRole( role )
                );
            }

            return isExist;
        } ).map( mapping::get ).collect( Collectors.toSet() );

        List<String> deletes = CollectionUtils.complement(
                updates, CollectionUtils.union( normalRoleItems, updates )
        );

        int[] effectRow = new int[]{ 0 };

        if ( !notExistRoleItems.isEmpty() ) {
            effectRow[ 0 ] += saveAll( notExistRoleItems );
        }

        if ( !deletes.isEmpty() ) {
            effectRow[ 0 ] += removeAll( deletes, System.currentTimeMillis() );
        }

        if ( !updates.isEmpty() ) {
            roleMenuItems.stream()
                    .filter( entity -> updates.contains( entity.getId() ) )
                    .forEach( entity -> effectRow[ 0 ] += update( entity.setStatus( NORMAL.getStatus() ) ) );
        }

        return effectRow[ 0 ];
    }

    private boolean isUnreachableOrUnauthorized( String itemId, String roleId ) {
        List<MenuRelationEntity> itemChain = menuRelationMapper.queryParentItem( itemId, NORMAL.getStatus() );
        return isUnreachable( itemChain ) || isUnauthorized( itemChain, roleId );
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

        // from large to small
        itemChain.sort( ( a, b ) -> a.getGap() > b.getGap() ? -1 : 1 );
        MenuItemEntity item = itemChain.get( 0 ).getParent();
        if ( null == item || !item.isRoot() ) {
            return true;
        }

        int[] count = new int[]{ itemChain.get( 0 ).getGap() };
        return itemChain.stream().skip( 1 ).anyMatch( innerItem -> {
            if ( count[ 0 ] - 1 == innerItem.getGap() ) {
                count[ 0 ] = innerItem.getGap();
                return false;
            }

            return true;
        } );
    }

    // if item chain is not recorded under specified role, also illegal operation too.
    private boolean isUnauthorized( List<MenuRelationEntity> itemChain, String roleId ) {
        return roleMenuItemMapper.queryRoleMenuItemChain(
                NORMAL.getStatus(), roleId,
                itemChain.stream().map( item -> item.getParent().getId() ).collect( Collectors.toList() )

        ).size() == itemChain.size();
    }
}
