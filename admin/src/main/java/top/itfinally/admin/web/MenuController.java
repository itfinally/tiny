package top.itfinally.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.itfinally.admin.service.MenuService;
import top.itfinally.core.vo.BaseResponseVoBean;

import java.util.*;

import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;

@RestController
@RequestMapping( "/menu" )
public class MenuController {
    private MenuService menuService;

    @Autowired
    public MenuController setMenuService( MenuService menuService ) {
        this.menuService = menuService;
        return this;
    }

    @ResponseBody
    @PostMapping( "/added_root_menu" )
    public BaseResponseVoBean addedRootMenu(
            @RequestParam( "name" ) String name,
            @RequestParam( "isLeaf" ) boolean isLeaf
    ) {
        if ( StringUtils.isBlank( name ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu name." );
        }

        return menuService.addedRootMenu( name, isLeaf );
    }

    @ResponseBody
    @PostMapping( "/added_menu" )
    public BaseResponseVoBean addedMenu(
            @RequestParam( "parentId" ) String parentId,
            @RequestParam( "name" ) String name,
            @RequestParam( "isLeaf" ) boolean isLeaf
    ) {
        if ( StringUtils.isBlank( parentId ) || StringUtils.isBlank( name ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu name and parent id." );
        }

        return menuService.addedMenu( parentId, name, isLeaf );
    }

    @ResponseBody
    @PostMapping( "/remove_menu_item" )
    public BaseResponseVoBean removeMenuItem( @RequestParam( "itemId" ) String itemId ) {
        if ( StringUtils.isBlank( itemId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require item id." );
        }

        return menuService.removeMenuItem( itemId );
    }

    @ResponseBody
    @PostMapping( "/remove_multi_menu_item" )
    public BaseResponseVoBean removeMultiMenuItem( @RequestBody List<String> itemIds ) {
        if ( null == itemIds || itemIds.isEmpty() ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require item ids" );
        }

        return menuService.removeMultiMenuItem( itemIds );
    }

    @ResponseBody
    @PostMapping( "/recover_menu_item" )
    public BaseResponseVoBean recoverMenuItem( @RequestParam( "itemId" ) String itemId ) {
        if ( StringUtils.isBlank( itemId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require item id." );
        }

        return menuService.recoverMenuItem( itemId );
    }

    @ResponseBody
    @PostMapping( "/recover_menu_multi_item" )
    public BaseResponseVoBean recoverMenuMultiItem( @RequestBody List<String> itemIds ) {
        if ( null == itemIds || itemIds.isEmpty() ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require item ids" );
        }

        return menuService.recoverMenuMultiItem( itemIds );
    }

    @ResponseBody
    @PostMapping( "/rename" )
    public BaseResponseVoBean rename( @RequestParam( "menuId" ) String menuId, @RequestParam( "name" ) String name ) {
        if ( StringUtils.isBlank( menuId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu id" );
        }

        if ( StringUtils.isBlank( name ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require new name" );
        }

        return menuService.rename( menuId, name );
    }

    @ResponseBody
    @GetMapping( "/get_menu_tree" )
    public BaseResponseVoBean getMenuTree() {
        return menuService.getMenuTree();
    }

    @ResponseBody
    @GetMapping( "/query_menu_item_roles/{menuId}" )
    public BaseResponseVoBean queryMenuItemRoles( @PathVariable( "menuId" ) String menuId ) {
        if ( StringUtils.isBlank( menuId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require menu id" );
        }

        return menuService.queryMenuItemRoles( menuId );
    }

}
