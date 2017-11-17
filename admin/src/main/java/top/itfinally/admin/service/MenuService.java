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

    public SingleResponseVoBean<Integer> addedRootMenu( String name, boolean isRoot, boolean isLeaf ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.addedRootMenu( name, isRoot, isLeaf ) );
    }

    public SingleResponseVoBean<Integer> addedMenu( String parentId, String name, boolean isRoot, boolean isLeaf ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.addedMenu( parentId, name, isRoot, isLeaf ) );
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

        List<MenuItemVoBean> menuItemVoBeans = breadthTraversal( new HashMap<>( 32 ), rootMenuItems )
                .entrySet().stream()
                .filter( entry -> rootItemIds.contains( entry.getKey() ) )
                .map( Map.Entry::getValue )
                .collect( Collectors.toList() );

        return new CollectionResponseVoBean<MenuItemVoBean>( SUCCESS ).setResult( menuItemVoBeans );
    }

    private Map<String, MenuItemVoBean> breadthTraversal( Map<String, MenuItemVoBean> itemRecords, List<MenuItemEntity> menuItems ) {
        List<MenuItemEntity> nextRound = new ArrayList<>();

        menuItems.forEach( item -> {
            List<MenuRelationEntity> childItems = menuRelationDao.queryDirectChildItem( item.getId(), NORMAL.getStatus() );

            childItems.stream().filter( relation -> relation.getGap() != 0 ).forEach( relation -> {
                String parentId = relation.getParent().getId();

                if ( !itemRecords.containsKey( parentId ) ) {
                    throw new IllegalStateException();
                }

                itemRecords.get( parentId ).getChildes().add( new MenuItemVoBean( relation.getChild() ) );
            } );

            nextRound.addAll( childItems.stream()
                    .filter( relation -> relation.getGap() != 0 && !relation.getChild().isLeaf() )
                    .map( MenuRelationEntity::getChild )
                    .collect( Collectors.toList() )
            );

            itemRecords.put( item.getId(), new MenuItemVoBean( item ) );
        } );

        return nextRound.isEmpty() ? itemRecords : breadthTraversal( itemRecords, nextRound );
    }
}
