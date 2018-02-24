package top.itfinally.admin.support.vue;

import org.apache.commons.lang3.StringUtils;
import top.itfinally.core.enumerate.DataStatusEnum;

import java.util.HashMap;
import java.util.Map;

import static top.itfinally.core.repository.QueryEnum.NOT_DATE_LIMIT;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

// write it for vue component 'multi_function_table'
public class MultiFunctionTableQuery {
  private MultiFunctionTableQuery() {
  }

  public static Map<String, Object> conditionBuilder(
      long createStartTime, long createEndingTime, long updateStartTime,
      long updateEndingTime, int status, String id
  ) {
    Map<String, Object> condition = new HashMap<>();

    if ( createStartTime > 0 ) {
      condition.put( "createStartTime", createStartTime );
    }

    if ( createEndingTime > 0 ) {
      condition.put( "createEndingTime", createEndingTime );
    }

    if ( createStartTime > 0 && createEndingTime <= 0 ) {
      condition.put( "createEndingTime", System.currentTimeMillis() );
    }

    if ( updateStartTime > 0 ) {
      condition.put( "updateStartTime", updateStartTime );
    }

    if ( updateEndingTime > 0 ) {
      condition.put( "updateEndingTime", updateEndingTime );
    }

    if ( updateStartTime > 0 && updateEndingTime <= 0 ) {
      condition.put( "updateEndingTime", System.currentTimeMillis() );
    }

    if ( status != 0 ) {
      condition.put( "status", status );
    }

    if ( StringUtils.isNotBlank( id ) ) {
      condition.put( "id", id );
    }

    return condition;
  }

  public static void conditionValidator( Map<String, Object> condition ) {
    if ( !( condition.containsKey( "createStartTime" ) && condition.containsKey( "createEndingTime" ) ) ) {
      condition.put( "createStartTime", NOT_DATE_LIMIT.getVal() );
      condition.put( "createEndingTime", NOT_DATE_LIMIT.getVal() );
    }

    if ( !( condition.containsKey( "updateStartTime" ) && condition.containsKey( "updateEndingTime" ) ) ) {
      condition.put( "updateStartTime", NOT_DATE_LIMIT.getVal() );
      condition.put( "updateEndingTime", NOT_DATE_LIMIT.getVal() );
    }

    if ( !condition.containsKey( "status" )
        && condition.get( "status" ) instanceof Integer
        && DataStatusEnum.contains( ( int ) condition.get( "status" ) ) ) {

      condition.put( "status", NOT_STATUS_FLAG.getVal() );
    }

    if ( !condition.containsKey( "status" ) ) {
      condition.put( "status", NOT_STATUS_FLAG.getVal() );
    }
  }
}
