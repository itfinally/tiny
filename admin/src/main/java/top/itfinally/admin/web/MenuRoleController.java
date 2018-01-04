package top.itfinally.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.itfinally.admin.service.MenuRoleService;
import top.itfinally.core.component.WebApiViewComponent;
import top.itfinally.core.vo.BaseResponseVoBean;

import java.util.List;

import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;

@ResponseBody
@RestController
@RequestMapping( "/menu_role" )
public class MenuRoleController extends WebApiViewComponent {

    private MenuRoleService menuRoleService;

    @Autowired
    public MenuRoleController setMenuRoleService( MenuRoleService menuRoleService ) {
        this.menuRoleService = menuRoleService;
        return this;
    }

    @GetMapping( "/query_menu_item_roles/{menuId}" )
    public BaseResponseVoBean queryMenuItemRoles( @PathVariable( "menuId" ) String menuId ) {
        if ( StringUtils.isBlank( menuId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu id." );
        }

        return menuRoleService.queryMenuItemRoles( menuId );
    }

    @GetMapping( "/query_available_role/{menuId}" )
    public BaseResponseVoBean queryAvailableRole( @PathVariable( "menuId" ) String menuId ) {
        if ( StringUtils.isBlank( menuId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu id" );
        }

        return menuRoleService.queryAvailableRole( menuId );
    }

    @PostMapping( "/add_role_menu/{menuItemId}" )
    public BaseResponseVoBean addRoleMenu( @PathVariable( "menuItemId" ) String menuItemId, @RequestBody List<String> roleIds ) {
        if ( StringUtils.isBlank( menuItemId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu item id." );
        }

        if ( null == roleIds || roleIds.isEmpty() ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role id." );
        }

        return menuRoleService.addRoleMenu( menuItemId, roleIds );
    }

    @PostMapping( "/remove_role_menu/{menuItemId}" )
    public BaseResponseVoBean removeRoleMenu( @PathVariable( "menuItemId" ) String menuItemId, @RequestBody List<String> roleIds ) {
        if ( StringUtils.isBlank( menuItemId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu item id." );
        }

        if ( null == roleIds || roleIds.isEmpty() ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role id." );
        }

        return menuRoleService.removeRoleMenu( menuItemId, roleIds );
    }
}
