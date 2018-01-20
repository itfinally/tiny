package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.admin.exception.NoSuchMenuItemException;
import top.itfinally.admin.repository.mapper.MenuItemMapper;
import top.itfinally.admin.repository.mapper.MenuRelationMapper;
import top.itfinally.admin.repository.po.MenuRelationEntity;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.admin.repository.po.MenuItemEntity;

import java.util.*;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@Repository
public class MenuItemDao extends AbstractDao<MenuItemEntity, MenuItemMapper> {
    private MenuItemMapper menuItemMapper;
    private MenuRelationMapper menuRelationMapper;

    @Override
    @Autowired
    protected void setBaseMapper( MenuItemMapper baseMapper ) {
        this.menuItemMapper = baseMapper;
        super.setBaseMapper( baseMapper );
    }

    @Autowired
    public MenuItemDao setMenuRelationMapper( MenuRelationMapper menuRelationMapper ) {
        this.menuRelationMapper = menuRelationMapper;
        return this;
    }

    @Override
    public int save( MenuItemEntity entity ) {
        throw new UnsupportedOperationException( "Should be use method 'addedRootMenu' or 'addedMenu'." );
    }

    @Override
    public int saveAll( Collection<MenuItemEntity> menuItemEntities ) {
        throw new UnsupportedOperationException( "Should be use method 'addedRootMenu' or 'addedMenu'." );
    }

    @Override
    public int remove( String id, long deleteTime ) {
        throw new UnsupportedOperationException( "Should be use method 'removeMenuItem'." );
    }

    @Override
    public int removeAll( Collection<String> ids, long deleteTime ) {
        throw new UnsupportedOperationException( "Should be use method 'removeMultiMenuItem'." );
    }

    @Transactional
    public MenuItemEntity addedRootMenu( String name, boolean isLeaf ) {
        MenuItemEntity menuItem = new MenuItemEntity().setName( name ).setPath( "" ).setRoot( true ).setLeaf( isLeaf );
        addedMenuItem( menuItem.getId(), menuItem );

        return menuItem;
    }

    @Transactional
    public MenuItemEntity addedMenu( String parentId, String name, String path, boolean isLeaf ) throws NoSuchMenuItemException {
        MenuItemEntity menuItem = new MenuItemEntity().setName( name ).setPath( path ).setRoot( false ).setLeaf( isLeaf );
        addedMenuItem( parentId, menuItem );

        return menuItem;
    }

    private void addedMenuItem( String parentId, MenuItemEntity menuItem ) throws NoSuchMenuItemException, IllegalStateException {
        List<MenuRelationEntity> parentItems = menuRelationMapper.queryParentItem( parentId, NOT_STATUS_FLAG.getVal() );
        List<MenuRelationEntity> relationEntities = new ArrayList<>();

        if ( !parentItems.isEmpty() ) {
            if ( isLeafItem( parentId ) ) {
                throw new IllegalStateException( "Cannot add menu item under the leaf item." );
            }

            relationEntities.addAll( parentItems.stream()
                    .map( item -> new MenuRelationEntity()

                            // keep same status with parent item
                            .setStatus( item.getStatus() )
                            .setDeleteTime( item.getDeleteTime() )

                            .setChild( new MenuItemEntity( menuItem.getId() ) )
                            .setParent( new MenuItemEntity( item.getParent().getId() ) )
                            .setGap( item.getGap() + 1 ) )
                    .collect( Collectors.toList() )
            );
        }

        // item must to reference self
        relationEntities.add( new MenuRelationEntity()
                .setChild( new MenuItemEntity( menuItem.getId() ) )
                .setParent( new MenuItemEntity( menuItem.getId() ) )
                .setGap( 0 )
        );

        menuRelationMapper.saveAll( relationEntities );
        menuItemMapper.save( menuItem );
    }

    private boolean isLeafItem( String itemId ) {
        MenuItemEntity item = menuItemMapper.query( itemId );
        if ( null == item ) {
            throw new NoSuchMenuItemException( String.format( "Menu item '%s' is not found.", itemId ) );
        }

        return item.isLeaf();
    }

    // In remove case, remove node in first, if node is folder, then find all child and remove
    @Transactional
    public int removeMenuItem( String itemId ) {
        MenuItemEntity item = menuItemMapper.query( itemId );
        long now = System.currentTimeMillis();
        int[] effectiveRow = new int[]{ 0 };

        effectiveRow[ 0 ] += menuItemMapper.remove( itemId, now );

        if ( item.isLeaf() ) {
            return effectiveRow[ 0 ];
        }

        menuRelationMapper.queryChildItem( itemId, NOT_STATUS_FLAG.getVal() ).stream()
                .filter( relation -> relation.getGap() > 0 )
                .map( MenuRelationEntity::getChild )
                .forEach( child -> effectiveRow[ 0 ] += menuItemMapper.remove( child.getId(), now ) );

        return effectiveRow[ 0 ];
    }

    @Transactional
    public int removeMultiMenuItem( List<String> itemIds ) {
        List<String> folders = new ArrayList<>( 16 );
        long now = System.currentTimeMillis();
        int[] effectiveRow = new int[]{ 0 };

        itemIds.forEach( id -> {
            MenuItemEntity item = menuItemMapper.query( id );
            effectiveRow[ 0 ] += menuItemMapper.remove( id, now );

            if ( !item.isLeaf() ) {
                folders.add( item.getId() );
            }
        } );

        folders.forEach( id -> menuRelationMapper.queryChildItem( id, NOT_STATUS_FLAG.getVal() ).stream()
                .filter( relation -> relation.getGap() > 0 )
                .map( MenuRelationEntity::getChild )
                .forEach( child -> effectiveRow[ 0 ] += menuItemMapper.remove( child.getId(), now ) ) );

        return effectiveRow[ 0 ];
    }

    // In recover case, recover node in first, if node is leaf, then find all parent and recover.

    @Transactional
    public int recoverItem( String itemId ) {
        MenuItemEntity item = menuItemMapper.query( itemId );
        long now = System.currentTimeMillis();
        int[] effectiveRow = new int[]{ 0 };

        menuItemMapper.update( item.setStatus( NORMAL.getStatus() ).setDeleteTime( -1 ).setUpdateTime( now ) );

        menuRelationMapper.queryParentItem( itemId, NOT_STATUS_FLAG.getVal() ).stream()
                .filter( relation -> relation.getGap() > 0 && relation.getParent().getStatus() == DELETE.getStatus() )
                .map( MenuRelationEntity::getParent )

                .forEach( parent -> effectiveRow[ 0 ] += menuItemMapper.update( parent
                        .setStatus( NORMAL.getStatus() ).setDeleteTime( -1 ).setUpdateTime( now ) ) );

        return effectiveRow[ 0 ];
    }

    @Transactional
    public int recoverMultiItem( List<String> childIds ) {
        List<String> leafs = new ArrayList<>();
        long now = System.currentTimeMillis();
        int[] effectiveRow = new int[]{ 0 };

        childIds.forEach( id -> {
            MenuItemEntity item = menuItemMapper.query( id );
            effectiveRow[ 0 ] += menuItemMapper.update( item.setStatus( NORMAL.getStatus() )
                    .setDeleteTime( -1 ).setUpdateTime( now ) );

            leafs.add( item.getId() );
        } );

        leafs.forEach( id -> menuRelationMapper.queryParentItem( id, NOT_STATUS_FLAG.getVal() ).stream()
                .filter( relation -> relation.getGap() > 0 && relation.getParent().getStatus() == DELETE.getStatus() )
                .map( MenuRelationEntity::getParent )

                .forEach( parent -> effectiveRow[ 0 ] += menuItemMapper.update( parent.setStatus( NORMAL.getStatus() )
                        .setDeleteTime( -1 ).setUpdateTime( now ) ) ) );

        return effectiveRow[ 0 ];
    }

    public List<MenuItemEntity> queryRootMenuItem() {
        return menuItemMapper.queryRootItem();
    }

    public boolean isUniqueName( String name ) {
        return menuItemMapper.queryByName( name ) == null;
    }
}
