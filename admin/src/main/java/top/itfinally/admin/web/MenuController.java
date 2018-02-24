package top.itfinally.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.itfinally.admin.service.MenuService;
import top.itfinally.core.component.WebApiViewComponent;
import top.itfinally.core.vo.BaseResponseVoBean;

import java.util.*;

import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;

@ResponseBody
@RestController
@RequestMapping( "/menu" )
public class MenuController extends WebApiViewComponent {
  private MenuService menuService;

  @Autowired
  public MenuController setMenuService( MenuService menuService ) {
    this.menuService = menuService;
    return this;
  }

  @PostMapping( "/added_root_menu" )
  @PreAuthorize( "hasPermission( null, 'menu_write' )" )
  public BaseResponseVoBean addedRootMenu(
      @RequestParam( "name" ) String name,
      @RequestParam( "isLeaf" ) boolean isLeaf
  ) {
    if ( StringUtils.isBlank( name ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu name." );
    }

    return menuService.addedRootMenu( name, isLeaf );
  }

  @PostMapping( "/added_menu" )
  @PreAuthorize( "hasPermission( null, 'write_menu' )" )
  public BaseResponseVoBean addedMenu(
      @RequestParam( "parentId" ) String parentId,
      @RequestParam( "name" ) String name,
      @RequestParam( "path" ) String path,
      @RequestParam( "isLeaf" ) boolean isLeaf
  ) {
    if ( StringUtils.isBlank( parentId ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require parent id." );
    }

    if ( StringUtils.isBlank( name ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu name." );
    }

    if ( StringUtils.isBlank( path ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu path." );
    }

    return menuService.addedMenu( parentId, name, path, isLeaf );
  }

  @PostMapping( "/remove_menu_item" )
  @PreAuthorize( "hasPermission( null, 'menu_write' )" )
  public BaseResponseVoBean removeMenuItem( @RequestParam( "itemId" ) String itemId ) {
    if ( StringUtils.isBlank( itemId ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require item id." );
    }

    return menuService.removeMenuItem( itemId );
  }

  @PostMapping( "/remove_multi_menu_item" )
  @PreAuthorize( "hasPermission( null, 'menu_write' )" )
  public BaseResponseVoBean removeMultiMenuItem( @RequestBody List<String> itemIds ) {
    if ( null == itemIds || itemIds.isEmpty() ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require item ids" );
    }

    return menuService.removeMultiMenuItem( itemIds );
  }

  @PostMapping( "/recover_menu_item" )
  @PreAuthorize( "hasPermission( null, 'menu_write' )" )
  public BaseResponseVoBean recoverMenuItem( @RequestParam( "itemId" ) String itemId ) {
    if ( StringUtils.isBlank( itemId ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require item id." );
    }

    return menuService.recoverMenuItem( itemId );
  }

  @PostMapping( "/recover_menu_multi_item" )
  @PreAuthorize( "hasPermission( null, 'menu_write' )" )
  public BaseResponseVoBean recoverMenuMultiItem( @RequestBody List<String> itemIds ) {
    if ( null == itemIds || itemIds.isEmpty() ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require item ids" );
    }

    return menuService.recoverMenuMultiItem( itemIds );
  }

  @PostMapping( "/update_menu" )
  @PreAuthorize( "hasPermission( null, 'menu_write' )" )
  public BaseResponseVoBean updateMenu(
      @RequestParam( "menuId" ) String menuId,
      @RequestParam( "name" ) String name,
      @RequestParam( "path" ) String path
  ) {
    if ( StringUtils.isBlank( menuId ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu id." );
    }

    if ( StringUtils.isBlank( name ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require new name." );
    }

    if ( StringUtils.isBlank( path ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require new path." );
    }

    return menuService.updateMenu( menuId, name, path );
  }

  @GetMapping( "/get_menu_tree" )
  @PreAuthorize( "!isAnonymous()" )
  public BaseResponseVoBean getMenuTree() {
    return menuService.getMenuTree();
  }

  @GetMapping( "/menu_initializing" )
  public BaseResponseVoBean menuInitializing() {
    return menuService.menuInitializing();
  }
}
