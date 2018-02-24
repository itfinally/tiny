package top.itfinally.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.itfinally.admin.service.PermissionService;
import top.itfinally.admin.support.vue.MultiFunctionTableQuery;
import top.itfinally.core.component.WebApiViewComponent;
import top.itfinally.core.enumerate.DataStatusEnum;
import top.itfinally.core.vo.BaseResponseVoBean;

import java.util.List;
import java.util.Map;

import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;

@ResponseBody
@RestController
@RequestMapping( "/permission" )
public class PermissionController extends WebApiViewComponent {

  private PermissionService permissionService;

  @Autowired
  public PermissionController setPermissionService( PermissionService permissionService ) {
    this.permissionService = permissionService;
    return this;
  }

  @PostMapping( "/query_by_multi_condition" )
  @PreAuthorize( "hasPermission( null, 'permission_read' )" )
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

    return permissionService.queryByMultiCondition( condition, page * row, row );
  }

  @PostMapping( "/count_by_multi_condition" )
  @PreAuthorize( "hasPermission( null, 'permission_read' )" )
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

    return permissionService.countByMultiCondition( condition );
  }

  @PostMapping( "/update_permission_detail" )
  @PreAuthorize( "hasPermission( null, 'permission_write' )" )
  public BaseResponseVoBean updatePermissionDetail(
      @RequestParam( "id" ) String id, @RequestParam( "name" ) String name,
      @RequestParam( "description" ) String description, @RequestParam( "status" ) int status
  ) {
    if ( StringUtils.isBlank( id ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require permission id." );
    }

    if ( StringUtils.isBlank( name ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require permission name." );
    }

    if ( StringUtils.isBlank( description ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require permission description." );
    }

    if ( !DataStatusEnum.contains( status ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require correct status." );
    }

    return permissionService.updatePermissionDetail( id, name, description, status );
  }

  @PostMapping( "/update_permission_status/{status}" )
  @PreAuthorize( "hasPermission( null, 'permission_write' )" )
  public BaseResponseVoBean updatePermissionStatus( @RequestBody List<String> ids, @PathVariable( "status" ) int status ) {
    if ( null == ids || ids.isEmpty() ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require permission id." );
    }

    if ( !DataStatusEnum.contains( status ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require correct status." );
    }

    return permissionService.updatePermissionStatus( ids, status );
  }

  @GetMapping( "/get_permissions" )
  public BaseResponseVoBean getPermissions() {
    return permissionService.getPermissions();
  }

  @GetMapping( "/get_specific_role_permissions/{roleId}" )
  @PreAuthorize( "hasPermission( null, 'grant' )" )
  public BaseResponseVoBean getSpecificRolePermissions( @PathVariable( "roleId" ) String roleId ) {
    return permissionService.getSpecificRolePermissions( roleId );
  }

  @GetMapping( "/permission_initializing" )
  public BaseResponseVoBean permissionInitializing() {
    return permissionService.permissionInitializing();
  }
}
