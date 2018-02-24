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

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
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
    return new SingleResponseVoBean<MenuItemVoBean>( SUCCESS ).setResult( new MenuItemVoBean( menuItemDao.addedRootMenu( name, isLeaf ) ) );
  }

  public SingleResponseVoBean<MenuItemVoBean> addedMenu( String parentId, String name, String path, boolean isLeaf ) {
    return new SingleResponseVoBean<MenuItemVoBean>( SUCCESS ).setResult( new MenuItemVoBean( menuItemDao.addedMenu( parentId, name, path, isLeaf ) ) );
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

  public SingleResponseVoBean<Integer> updateMenu( String menuId, String name, String path ) {
    MenuItemEntity menuItem = menuItemDao.query( menuId );

    if ( null == menuItem ) {
      return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST ).setMessage( String.format( "Menu id '%s' is not exists.", menuId ) );
    }

    if ( !menuItem.getName().equals( name ) && !menuItemDao.isUniqueName( name ) ) {
      return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST ).setMessage( String.format( "Name '%s' already exists.", name ) );
    }

    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.update( menuItem.setName( name ).setPath( path ) ) );
  }

  public CollectionResponseVoBean<MenuItemVoBean> getMenuTree() {
    Authentication passport = SecurityContextHolder.getContext().getAuthentication();

    Set<String> rootItemIds;
    List<MenuItemEntity> rootMenuItems;
    Predicate<MenuItemEntity> nodeFilterFn;

    // getting full menu tree if role is admin
    if ( passport.getAuthorities().stream().anyMatch( auth -> auth.getAuthority().equals( "ROLE_ADMIN" ) ) ) {
      rootMenuItems = menuItemDao.queryRootMenuItem();
      rootItemIds = rootMenuItems.stream().map( BaseEntity::getId ).collect( toSet() );
      nodeFilterFn = item -> true;

    } else {
      List<String> roleIds = passport.getAuthorities().stream()
          .map( role -> ( ( RoleEntity ) role ).getId() ).collect( toList() );

      Set<String> availableItems = roleIds.isEmpty()
          ? new HashSet<>()
          : roleMenuItemDao.queryMenuItems( roleIds ).stream().map( BaseEntity::getId ).collect( toSet() );

      nodeFilterFn = item -> item.getStatus() == NORMAL.getStatus() && availableItems.contains( item.getId() );

      rootMenuItems = menuItemDao.queryRootMenuItem().stream().filter( nodeFilterFn ).collect( toList() );

      rootItemIds = rootMenuItems.stream().map( BaseEntity::getId ).collect( toSet() );

      if ( rootMenuItems.isEmpty() ) {
        return new CollectionResponseVoBean<>( EMPTY_RESULT );
      }
    }

    Map<String, MenuItemVoBean> menuItems = breadthTraversal(
        new HashMap<>( 32 ), new HashMap<>( 32 ),
        rootMenuItems, nodeFilterFn
    );

    List<MenuItemVoBean> menuItemVoBeans = rootItemIds.stream().map( menuItems::get )
        .filter( Objects::nonNull ).collect( toList() );

    ResponseStatusEnum status = menuItemVoBeans.isEmpty() ? EMPTY_RESULT : SUCCESS;
    return new CollectionResponseVoBean<MenuItemVoBean>( status ).setResult( menuItemVoBeans );
  }

  private Map<String, MenuItemVoBean> breadthTraversal(
      Map<String, MenuItemVoBean> itemRecords, Map<String, String> relations,
      List<MenuItemEntity> menuItems, Predicate<MenuItemEntity> nodeFilterFn
  ) {
    List<MenuItemEntity> nextRound = new ArrayList<>();

    menuItems.forEach( item -> {
      List<MenuRelationEntity> childItems = !item.isLeaf()
          ? menuRelationDao.queryDirectChildItems( item.getId(), NOT_STATUS_FLAG.getVal() )
          : new ArrayList<>();

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

      if ( childItems.isEmpty() ) {
        return;
      }

      childItems.stream().filter( relation -> relation.getGap() > 0 ).forEach( relation -> {
        String parentId = relation.getParent().getId();
        MenuItemEntity child = relation.getChild();

        // if accessor is admin, pass, even node has been removed.
        // else depending on node status
        if ( !nodeFilterFn.test( child ) ) {
          return;
        }

        if ( !child.isLeaf() ) {
          relations.put( child.getId(), parentId );
        }

        itemRecords.get( parentId ).getChildes().add( new MenuItemVoBean( child ) );
      } );

      nextRound.addAll( childItems.stream()
          .filter( relation -> relation.getGap() != 0
              && !relation.getChild().isLeaf()
              && nodeFilterFn.test( relation.getChild() ) )

          .map( MenuRelationEntity::getChild )
          .collect( toList() ) );
    } );

    return nextRound.isEmpty() ? itemRecords : breadthTraversal( itemRecords, relations, nextRound, nodeFilterFn );
  }

  public SingleResponseVoBean<Integer> menuInitializing() {
    return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( menuItemDao.menuInitializing() );
  }
}
