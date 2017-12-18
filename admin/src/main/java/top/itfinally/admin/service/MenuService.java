package top.itfinally.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.MenuItemDao;
import top.itfinally.admin.repository.dao.MenuRelationDao;
import top.itfinally.admin.repository.dao.RoleMenuItemDao;
import top.itfinally.admin.repository.po.MenuItemEntity;
import top.itfinally.admin.repository.po.MenuRelationEntity;
import top.itfinally.admin.web.vo.MenuItemVoBean;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.repository.po.BaseEntity;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.web.vo.RoleVoBean;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Service
public class MenuService {
    private MenuItemDao menuItemDao;
    private RoleMenuItemDao roleMenuItemDao;
    private MenuRelationDao menuRelationDao;

    @Autowired
    public MenuService setMenuItemDao( MenuItemDao menuItemDao ) {
        this.menuItemDao = menuItemDao;
        return this;
    }

    @Autowired
    public MenuService setRoleMenuItemDao( RoleMenuItemDao roleMenuItemDao ) {
        this.roleMenuItemDao = roleMenuItemDao;
        return this;
    }

    @Autowired
    public MenuService setMenuRelationDao( MenuRelationDao menuRelationDao ) {
        this.menuRelationDao = menuRelationDao;
        return this;
    }

    public SingleResponseVoBean<MenuItemVoBean> addedRootMenu( String name, boolean isLeaf ) {
        return new SingleResponseVoBean<MenuItemVoBean>( SUCCESS )
                .setResult( new MenuItemVoBean( menuItemDao.addedRootMenu( name, isLeaf ) ) );
    }

    public SingleResponseVoBean<MenuItemVoBean> addedMenu( String parentId, String name, boolean isLeaf ) {
        return new SingleResponseVoBean<MenuItemVoBean>( SUCCESS )
                .setResult( new MenuItemVoBean( menuItemDao.addedMenu( parentId, name, isLeaf ) ) );
    }

    public SingleResponseVoBean<Integer> removeMenuItem( String itemId ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.removeMenuItem( itemId ) );
    }

    public SingleResponseVoBean<Integer> removeMultiMenuItem( List<String> itemIds ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.removeMultiMenuItem( itemIds ) );
    }

    public SingleResponseVoBean<Integer> recoverMenuItem( String itemId ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.recoverItem( itemId ) );
    }

    public SingleResponseVoBean<Integer> recoverMenuMultiItem( List<String> childIds ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.recoverMultiItem( childIds ) );
    }

    public SingleResponseVoBean<Integer> rename( String menuId, String name ) {
        if ( !menuItemDao.isUniqueName( name ) ) {
            return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST )
                    .setMessage( String.format( "Name '%s' already exists.", name ) );
        }

        MenuItemEntity menuItem = menuItemDao.query( menuId );
        if ( null == menuItem ) {
            return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST )
                    .setMessage( String.format( "Menu id '%s' is not exists.", menuId ) );
        }

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.update( menuItem.setName( name ) ) );
    }

    public CollectionResponseVoBean<MenuItemVoBean> getMenuTree() {
        Authentication passport = SecurityContextHolder.getContext().getAuthentication();

        Set<String> rootItemIds;
        List<MenuItemEntity> rootMenuItems;
        Predicate<MenuItemEntity> nodeFilterFn;


        // getting full menu tree if role is admin
        if ( passport.getAuthorities().stream().anyMatch( auth -> auth.getAuthority().equals( "ROLE_ADMIN" ) ) ) {
            rootMenuItems = menuItemDao.queryRootMenuItem();
            rootItemIds = rootMenuItems.stream().map( BaseEntity::getId ).collect( Collectors.toSet() );
            nodeFilterFn = item -> true;

        } else {
            Set<String> menuItemIds = new HashSet<>();

            passport.getAuthorities().stream().map( auth -> roleMenuItemDao.queryRoleMenuItem( ( ( RoleEntity ) auth ).getId() ) )
                    .forEach( item -> menuItemIds.addAll( item.stream().map( BaseEntity::getId ).collect( Collectors.toSet() ) ) );

            rootMenuItems = menuItemDao.queryRootMenuItem().stream()
                    .filter( item -> menuItemIds.contains( item.getId() ) )
                    .collect( Collectors.toList() );

            rootItemIds = rootMenuItems.stream().map( BaseEntity::getId ).collect( Collectors.toSet() );

            if ( rootMenuItems.isEmpty() ) {
                return new CollectionResponseVoBean<>( EMPTY_RESULT );
            }

            nodeFilterFn = item -> item.getStatus() == NORMAL.getStatus();
        }


        List<MenuItemVoBean> menuItemVoBeans = breadthTraversal(
                new HashMap<>( 32 ), new HashMap<>( 32 ),
                rootMenuItems, nodeFilterFn
        )
                .entrySet().stream()
                .filter( entry -> rootItemIds.contains( entry.getKey() ) )
                .map( Map.Entry::getValue )
                .collect( Collectors.toList() );

        return new CollectionResponseVoBean<MenuItemVoBean>( SUCCESS ).setResult( menuItemVoBeans );
    }

    private Map<String, MenuItemVoBean> breadthTraversal(
            Map<String, MenuItemVoBean> itemRecords, Map<String, String> relations,
            List<MenuItemEntity> menuItems, Predicate<MenuItemEntity> nodeFilterFn
    ) {
        List<MenuItemEntity> nextRound = new ArrayList<>();

        menuItems.forEach( item -> {
            List<MenuRelationEntity> childItems = menuRelationDao.queryDirectChildItem( item.getId(), NOT_STATUS_FLAG.getVal() );

            if ( item.isRoot() ) {
                itemRecords.put( item.getId(), new MenuItemVoBean( item ) );

            } else {
                // getting parent item from grandparent item and put it to item records map
                // so that they are same instance, it will be keep easy to traverse the menu tree
                MenuItemVoBean parentItem = itemRecords.get( relations.get( item.getId() ) );
                boolean ignore = parentItem.getChildes().stream().anyMatch( childItem -> {
                    if ( childItem.getId().equals( item.getId() ) ) {
                        itemRecords.put( item.getId(), childItem );
                        return true;
                    }

                    return false;
                } );
            }

            childItems.stream().filter( relation -> relation.getGap() > 0 ).forEach( relation -> {
                String parentId = relation.getParent().getId();

                if ( !relation.getChild().isLeaf() ) {
                    relations.put( relation.getChild().getId(), parentId );
                }

                // if accessor is admin, pass, even node has been removed.
                // else depending on node status
                if ( nodeFilterFn.test( relation.getChild() ) ) {
                    itemRecords.get( parentId ).getChildes().add( new MenuItemVoBean( relation.getChild() ) );
                }
            } );

            nextRound.addAll( childItems.stream()
                    .filter( relation -> relation.getGap() != 0 && !relation.getChild().isLeaf() )
                    .map( MenuRelationEntity::getChild )
                    .collect( Collectors.toList() )
            );
        } );

        return nextRound.isEmpty() ? itemRecords : breadthTraversal( itemRecords, relations, nextRound, nodeFilterFn );
    }

    public CollectionResponseVoBean<RoleVoBean> queryMenuItemRoles( String menuId ) {
        List<RoleEntity> roles = roleMenuItemDao.queryMenuItemRoles( menuId );
        ResponseStatusEnum status = roles.isEmpty() ? SUCCESS : EMPTY_RESULT;

        return new CollectionResponseVoBean<RoleVoBean>( status )
                .setResult( roles.stream().map( RoleVoBean::new ).collect( Collectors.toList() ) );
    }
}
