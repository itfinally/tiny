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

import static java.util.stream.Collectors.toList;
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
    List<MenuRelationEntity>
        parentItems = menuRelationMapper.queryParentItems( parentId, NOT_STATUS_FLAG.getVal() ),
        relationEntities = new ArrayList<>();

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

          .collect( toList() ) );
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
    return menuItemMapper.removeAll( menuRelationMapper.queryChildItems( itemId, NOT_STATUS_FLAG.getVal() ).stream()
        .map( item -> item.getChild().getId() ).collect( toList() ), System.currentTimeMillis() );
  }

  @Transactional
  public int removeMultiMenuItem( List<String> itemIds ) {
    return menuItemMapper.removeAll( menuRelationMapper.queryMultiChildItems( itemIds, NOT_STATUS_FLAG.getVal() ).stream()
        .map( item -> item.getChild().getId() ).collect( toList() ), System.currentTimeMillis() );
  }

  // In recover case, recover node in first, if node is leaf, then find all parent and recover.
  @Transactional
  public int recoverItem( String itemId ) {
    long now = System.currentTimeMillis();
    int[] effectiveRow = new int[]{ 0 };

    menuRelationMapper.queryParentItems( itemId, NOT_STATUS_FLAG.getVal() ).stream().map( MenuRelationEntity::getParent )

        .forEach( item -> effectiveRow[ 0 ] += menuItemMapper
            .update( item.setStatus( NORMAL.getStatus() ).setDeleteTime( -1 ).setUpdateTime( now ) ) );

    return effectiveRow[ 0 ];
  }

  @Transactional
  public int recoverMultiItem( List<String> childIds ) {
    long now = System.currentTimeMillis();
    int[] effectiveRow = new int[]{ 0 };

    menuRelationMapper.queryMultiParentItems( childIds, NOT_STATUS_FLAG.getVal() ).stream().map( MenuRelationEntity::getParent )

        .forEach( item -> effectiveRow[ 0 ] += menuItemMapper
            .update( item.setStatus( NORMAL.getStatus() ).setDeleteTime( -1 ).setUpdateTime( now ) ) );

    return effectiveRow[ 0 ];
  }

  public List<MenuItemEntity> queryRootMenuItem() {
    return menuItemMapper.queryRootItem();
  }

  public boolean isUniqueName( String name ) {
    return menuItemMapper.queryByName( name ) == null;
  }

  @Transactional
  public int menuInitializing() {
    MenuItemEntity testing = menuItemMapper.queryByName( "系统管理" );
    if ( testing != null ) {
      return 0;
    }

    MenuItemEntity systemManager = addedRootMenu( "系统管理", false );
    addedMenu( systemManager.getId(), "菜单列表", "/index/auth/menu_manager", true );
    addedMenu( systemManager.getId(), "角色列表", "/index/auth/role/:metadata?", true );
    addedMenu( systemManager.getId(), "权限列表", "/index/auth/permission/:metadata?", true );
    addedMenu( systemManager.getId(), "用户管理", "/index/auth/user/:metadata?", true );

    return 5;
  }
}
