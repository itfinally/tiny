package top.itfinally.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.itfinally.admin.service.RoleService;
import top.itfinally.admin.support.vue.MultiFunctionTableQuery;
import top.itfinally.core.component.WebApiViewComponent;
import top.itfinally.core.enumerate.DataStatusEnum;
import top.itfinally.core.vo.BaseResponseVoBean;

import java.util.List;
import java.util.Map;

import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;

@ResponseBody
@RestController
@RequestMapping( "/role" )
public class RoleController extends WebApiViewComponent {

    private RoleService roleService;

    @Autowired
    public RoleController setRoleService( RoleService roleService ) {
        this.roleService = roleService;
        return this;
    }

    @PostMapping( "/query_by_multi_condition" )
    public BaseResponseVoBean queryByMultiCondition(
            @RequestParam( value = "createStartTime", defaultValue = "-1" ) long createStartTime,
            @RequestParam( value = "createEndingTime", defaultValue = "-1" ) long createEndingTime,

            @RequestParam( value = "updateStartTime", defaultValue = "-1" ) long updateStartTime,
            @RequestParam( value = "updateEndingTime", defaultValue = "-1" ) long updateEndingTime,

            @RequestParam( value = "status", defaultValue = "0" ) int status,
            @RequestParam( value = "id", defaultValue = "" ) String id,

            @RequestParam( "page" ) int page,
            @RequestParam( "row" ) int row
    ) {
        Map<String, Object> condition = MultiFunctionTableQuery.conditionBuilder( createStartTime, createEndingTime,
                updateStartTime, updateEndingTime, status, id );

        if ( page < 0 || row < 0 ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Page and row must be granter than zero." );
        }

        return roleService.queryByMultiCondition( condition, page * row, row );
    }

    @PostMapping( "/count_by_multi_condition" )
    public BaseResponseVoBean countByMultiCondition(
            @RequestParam( value = "createStartTime", defaultValue = "-1" ) long createStartTime,
            @RequestParam( value = "createEndingTime", defaultValue = "-1" ) long createEndingTime,

            @RequestParam( value = "updateStartTime", defaultValue = "-1" ) long updateStartTime,
            @RequestParam( value = "updateEndingTime", defaultValue = "-1" ) long updateEndingTime,

            @RequestParam( value = "status", defaultValue = "0" ) int status,
            @RequestParam( value = "id", defaultValue = "" ) String id
    ) {
        Map<String, Object> condition = MultiFunctionTableQuery.conditionBuilder( createStartTime, createEndingTime,
                updateStartTime, updateEndingTime, status, id );

        return roleService.countByMultiCondition( condition );
    }

    @PostMapping( "/update_role_detail" )
    public BaseResponseVoBean updateRoleDetail(
            @RequestParam( "id" ) String id, @RequestParam( "name" ) String name,
            @RequestParam( "description" ) String description, @RequestParam( "status" ) int status
    ) {
        if ( StringUtils.isBlank( id ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role id." );
        }

        if ( StringUtils.isBlank( name ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role name." );
        }

        if ( StringUtils.isBlank( description ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role description." );
        }

        if ( !DataStatusEnum.contains( status ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require correct status." );
        }

        return roleService.updateRoleDetail( id, name, description, status );
    }

    @PostMapping( "/update_role_status/{status}" )
    public BaseResponseVoBean updateRoleStatus( @RequestBody List<String> ids, @PathVariable( "status" ) int status ) {
        if ( null == ids || ids.isEmpty() ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role id." );
        }

        if ( !DataStatusEnum.contains( status ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require correct status." );
        }

        return roleService.updateRoleStatus( ids, status );
    }

    @GetMapping( "/get_roles" )
    public BaseResponseVoBean getRoles() {
        return roleService.getRoles();
    }

    @GetMapping( "/get_specific_user_roles/{userId}" )
    public BaseResponseVoBean getSpecificUserRoles( @PathVariable( "userId" ) String userId ) {
        if ( StringUtils.isBlank( userId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require user id." );
        }

        return roleService.getSpecificUserRoles( userId );
    }

    @PostMapping( "/grant_permission_to/{roleId}" )
    public BaseResponseVoBean grantPermissionsTo( @PathVariable( "roleId" ) String roleId, @RequestBody List<String> permissionIds ) {
        if ( StringUtils.isBlank( roleId ) ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require role id." );
        }

        if ( null == permissionIds ) {
            return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require permissions id." );
        }

        return roleService.grantPermissionsTo( roleId, permissionIds );
    }
}
