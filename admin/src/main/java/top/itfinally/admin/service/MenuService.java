package top.itfinally.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.MenuItemDao;
import top.itfinally.admin.repository.dao.MenuRelationDao;
import top.itfinally.admin.repository.po.MenuItemEntity;
import top.itfinally.admin.repository.po.MenuRelationEntity;
import top.itfinally.admin.web.vo.MenuItemVoBean;
import top.itfinally.core.repository.po.BaseEntity;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;

import java.util.*;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;

@Service
public class MenuService {
    private MenuItemDao menuItemDao;
    private MenuRelationDao menuRelationDao;

    @Autowired
    public MenuService setMenuItemDao( MenuItemDao menuItemDao ) {
        this.menuItemDao = menuItemDao;
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

    public CollectionResponseVoBean<MenuItemVoBean> getMenuTree() {
        List<MenuItemEntity> rootMenuItems = menuItemDao.queryRootMenuItem();
        Set<String> rootItemIds = rootMenuItems.stream().map( BaseEntity::getId ).collect( Collectors.toSet() );

        List<MenuItemVoBean> menuItemVoBeans = breadthTraversal(
                new HashMap<>( 32 ), new HashMap<>( 32 ), rootMenuItems
        )
                .entrySet().stream()
                .filter( entry -> rootItemIds.contains( entry.getKey() ) )
                .map( Map.Entry::getValue )
                .collect( Collectors.toList() );

        return new CollectionResponseVoBean<MenuItemVoBean>( SUCCESS ).setResult( menuItemVoBeans );
    }

    private Map<String, MenuItemVoBean> breadthTraversal(
            Map<String, MenuItemVoBean> itemRecords, Map<String, String> relations, List<MenuItemEntity> menuItems
    ) {
        List<MenuItemEntity> nextRound = new ArrayList<>();

        menuItems.forEach( item -> {
            List<MenuRelationEntity> childItems = menuRelationDao.queryDirectChildItem( item.getId(), NORMAL.getStatus() );

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

            childItems.stream().filter( relation -> relation.getGap() != 0 ).forEach( relation -> {
                String parentId = relation.getParent().getId();

                relations.put( relation.getChild().getId(), parentId );
                itemRecords.get( parentId ).getChildes().add( new MenuItemVoBean( relation.getChild() ) );
            } );

            nextRound.addAll( childItems.stream()
                    .filter( relation -> relation.getGap() != 0 && !relation.getChild().isLeaf() )
                    .map( MenuRelationEntity::getChild )
                    .collect( Collectors.toList() )
            );
        } );

        return nextRound.isEmpty() ? itemRecords : breadthTraversal( itemRecords, relations, nextRound );
    }
}
