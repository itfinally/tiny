package top.itfinally.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.*;
import top.itfinally.admin.security.UserDetailService;
import top.itfinally.admin.support.vue.MultiFunctionTableQuery;
import top.itfinally.core.component.WebApiViewComponent;
import top.itfinally.core.enumerate.DataStatusEnum;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;

@ResponseBody
@RestController
@RequestMapping( "/user" )
public class UserDetailController extends WebApiViewComponent {
  private UserDetailService userDetailService;

  @Autowired
  public UserDetailController setUserDetailService( UserDetailService userDetailService ) {
    this.userDetailService = userDetailService;
    return this;
  }

  @PostMapping( "/query_by_multi_condition" )
  @PreAuthorize( "hasPermission( null, 'user_read' )" )
  public BaseResponseVoBean queryByMultiCondition(
      @RequestParam( value = "createStartTime", defaultValue = "-1" ) long createStartTime,
      @RequestParam( value = "createEndingTime", defaultValue = "-1" ) long createEndingTime,

      @RequestParam( value = "updateStartTime", defaultValue = "-1" ) long updateStartTime,
      @RequestParam( value = "updateEndingTime", defaultValue = "-1" ) long updateEndingTime,

      @RequestParam( value = "status", defaultValue = "0" ) int status,
      @RequestParam( value = "nickname", defaultValue = "" ) String nickname,
      @RequestParam( value = "id", defaultValue = "" ) String id,

      @RequestParam( "page" ) int page,
      @RequestParam( "row" ) int row
  ) {
    Map<String, Object> condition = conditionBuilder(
        createStartTime, createEndingTime, updateStartTime,
        updateEndingTime, status, nickname, id
    );

    if ( page < 0 || row < 0 ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Page and row must be granter than zero." );
    }

    return userDetailService.queryByMultiCondition( condition, page * row, row );
  }

  @PostMapping( "/count_by_multi_condition" )
  @PreAuthorize( "hasPermission( null, 'user_read' )" )
  public BaseResponseVoBean countByMultiCondition(
      @RequestParam( value = "createStartTime", defaultValue = "-1" ) long createStartTime,
      @RequestParam( value = "createEndingTime", defaultValue = "-1" ) long createEndingTime,

      @RequestParam( value = "updateStartTime", defaultValue = "-1" ) long updateStartTime,
      @RequestParam( value = "updateEndingTime", defaultValue = "-1" ) long updateEndingTime,

      @RequestParam( value = "status", defaultValue = "0" ) int status,
      @RequestParam( value = "nickname", defaultValue = "" ) String nickname,
      @RequestParam( value = "id", defaultValue = "" ) String id
  ) {
    Map<String, Object> condition = conditionBuilder(
        createStartTime, createEndingTime, updateStartTime,
        updateEndingTime, status, nickname, id
    );

    return userDetailService.countByMultiCondition( condition );
  }

  private Map<String, Object> conditionBuilder(
      long createStartTime, long createEndingTime, long updateStartTime,
      long updateEndingTime, int status, String nickname, String id
  ) {
    Map<String, Object> condition = MultiFunctionTableQuery.conditionBuilder( createStartTime, createEndingTime,
        updateStartTime, updateEndingTime, status, id );

    if ( StringUtils.isNotBlank( nickname ) ) {
      condition.put( "nickname", nickname );
    }

    return condition;
  }

  @PostMapping( "/update_user_detail" )
  @PreAuthorize( "hasPermission( null, 'user_write' )" )
  public BaseResponseVoBean updateUserDetail(
      @RequestParam( "id" ) String id,
      @RequestParam( "status" ) int status,
      @RequestParam( "name" ) String name
  ) {
    if ( StringUtils.isBlank( id ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require user id" );
    }

    if ( StringUtils.isBlank( name ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require user nickname" );
    }

    if ( !DataStatusEnum.contains( status ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require correct status." );
    }

    return userDetailService.updateUserDetail( id, name, status );
  }

  @PostMapping( "/update_user_status/{status}" )
  @PreAuthorize( "hasPermission( null, 'user_write' )" )
  public BaseResponseVoBean updateUserStatus( @PathVariable( "status" ) int status, @RequestBody List<String> userIds ) {
    if ( !DataStatusEnum.contains( status ) ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Illegal status code." );
    }

    if ( null == userIds || userIds.isEmpty() ) {
      return new BaseResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require user ids." );
    }

    return userDetailService.updateUserStatus( status, userIds );
  }

  @PostMapping( "/register" )
  @PreAuthorize( "hasPermission( null, 'user_write' )" )
  public BaseResponseVoBean register( @RequestParam( "user" ) String user ) throws UnsupportedEncodingException {
    if ( StringUtils.isBlank( user ) ) {
      return new SingleResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Illegal register information." );
    }

    // 前端 base64 传入后台时, '+' 会变成空格, 并且最后一个字符是 \n
    user = user.replaceAll( " ", "+" ).replaceAll( "\\n", "" );

    user = new String( Base64.decode( user.getBytes() ), "UTF-8" );
    if ( !user.contains( ":" ) || user.split( ":" ).length < 3 ) {
      return new SingleResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Illegal register information." );
    }

    String[] userEntry = user.split( ":" );
    String account = userEntry[ 0 ];
    String nickname = userEntry[ 1 ];
    String password = userEntry[ 2 ];

    if ( StringUtils.isBlank( account ) ) {
      return new SingleResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require account." );
    }

    if ( StringUtils.isBlank( nickname ) ) {
      return new SingleResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require nickname." );
    }

    if ( StringUtils.isBlank( password ) ) {
      return new SingleResponseVoBean( ILLEGAL_REQUEST ).setMessage( "Require password." );
    }

    return userDetailService.register( account, nickname, password );
  }

  @GetMapping( "/get_own_details" )
  public BaseResponseVoBean getOwnDetails() {
    return userDetailService.getOwnDetails();
  }

  @GetMapping( "/get_own_roles" )
  public BaseResponseVoBean getOwnRoles() {
    return userDetailService.getOwnRoles();
  }

  @GetMapping( "/get_own_permissions" )
  public BaseResponseVoBean getOwnPermissions() {
    return userDetailService.getOwnPermissions();
  }
}
