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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        MenuItemEntity menuItem = new MenuItemEntity().setName( name ).setRoot( true ).setLeaf( isLeaf );
        addedMenuItem( menuItem.getId(), menuItem );

        return menuItem;
    }

    @Transactional
    public MenuItemEntity addedMenu( String parentId, String name, boolean isLeaf ) throws NoSuchMenuItemException {
        MenuItemEntity menuItem = new MenuItemEntity().setName( name ).setRoot( false ).setLeaf( isLeaf );
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

    @Transactional
    public int removeMenuItem( String itemId ) {
        int effectRow = 0;

        long now = System.currentTimeMillis();

        effectRow += menuItemMapper.remove( itemId, now );
        effectRow += menuRelationMapper.removeChildItem( itemId, now );

        return effectRow;
    }

    @Transactional
    public int removeMultiMenuItem( List<String> itemIds ) {
        int effectRow = 0;

        long now = System.currentTimeMillis();

        effectRow += menuItemMapper.removeAll( itemIds, now );
        effectRow += menuRelationMapper.removeMultiChildItem( itemIds, now );

        return effectRow;
    }

    public List<MenuItemEntity> queryRootMenuItem() {
        return menuItemMapper.queryRootItem();
    }
}
