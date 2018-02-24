package top.itfinally.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.itfinally.admin.service.RoleMenuService;
import top.itfinally.core.component.WebApiViewComponent;
import top.itfinally.core.vo.BaseResponseVoBean;

import java.util.List;

import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;

@ResponseBody
@RestController
@RequestMapping( "/role_menu" )
public class RoleMenuController extends WebApiViewComponent {

  private RoleMenuService roleMenuService;

  @Autowired
  public RoleMenuController setRoleMenuService( RoleMenuService roleMenuService ) {
    this.roleMenuService = roleMenuService;
    return this;
  }

  @GetMapping( "/query_menu_item_roles/{menuId}" )
  @PreAuthorize( "hasPermission( null, 'grant' )" )
  public BaseResponseVoBean queryMenuItemRoles( @PathVariable( "menuId" ) String menuId ) {
    if ( StringUtils.isBlank( menuId ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu id." );
    }

    return roleMenuService.queryMenuItemRoles( menuId );
  }

  @GetMapping( "/query_available_role/{menuId}" )
  @PreAuthorize( "hasPermission( null, 'grant' )" )
  public BaseResponseVoBean queryAvailableRole( @PathVariable( "menuId" ) String menuId ) {
    if ( StringUtils.isBlank( menuId ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu id" );
    }

    return roleMenuService.queryAvailableRole( menuId );
  }

  @PostMapping( "/add_role_menu/{menuItemId}" )
  @PreAuthorize( "hasPermission( null, 'grant' )" )
  public BaseResponseVoBean addRoleMenu( @PathVariable( "menuItemId" ) String menuItemId, @RequestBody List<String> roleIds ) {
    if ( StringUtils.isBlank( menuItemId ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu item id." );
    }

    if ( null == roleIds || roleIds.isEmpty() ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role id." );
    }

    return roleMenuService.addRoleMenu( menuItemId, roleIds );
  }

  @PostMapping( "/remove_role_menu/{menuItemId}" )
  @PreAuthorize( "hasPermission( null, 'grant' )" )
  public BaseResponseVoBean removeRoleMenu( @PathVariable( "menuItemId" ) String menuItemId, @RequestBody List<String> roleIds ) {
    if ( StringUtils.isBlank( menuItemId ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu item id." );
    }

    if ( null == roleIds || roleIds.isEmpty() ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role id." );
    }

    return roleMenuService.removeRoleMenu( menuItemId, roleIds );
  }
}
