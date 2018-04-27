package top.itfinally.console.repository;

import org.springframework.util.StringUtils;
import top.itfinally.core.BasicRuntime;
import top.itfinally.core.EntityStatus;
import top.itfinally.core.QueryStatus;
import top.itfinally.core.repository.BasicEntity;
import top.itfinally.core.repository.BasicQuerySituation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.util.Map;

public class ConditionQuerySituation extends BasicQuerySituation<ConditionQuerySituation> {
  private String id = null;
  private long createTimeStarted = -1;
  private long createTimeEnd = -1;
  private long updateTimeStarted = -1;
  private long updateTimeEnd = -1;

  protected ConditionQuerySituation() {
  }

  public String getId() {
    return id;
  }

  public ConditionQuerySituation setId( String id ) {
    this.id = id;
    return this;
  }

  public ConditionQuerySituation setCreateTimeRange( long started, long end ) {
    this.createTimeStarted = started;
    this.createTimeEnd = end;
    return this;
  }

  public long getCreateTimeStarted() {
    return createTimeStarted;
  }

  public long getCreateTimeEnd() {
    return createTimeEnd;
  }

  public ConditionQuerySituation setUpdateTimeRange( long started, long end ) {
    this.updateTimeStarted = started;
    this.updateTimeEnd = end;
    return this;
  }

  public long getUpdateTimeStarted() {
    return updateTimeStarted;
  }

  public long getUpdateTimeEnd() {
    return updateTimeEnd;
  }

  public static ConditionQuerySituation build( Map<String, Object> conditions ) {
    ConditionQuerySituation situation = new ConditionQuerySituation();
    if ( null == conditions ) {
      return situation;
    }

    if ( conditions.containsKey( "id" ) && conditions.get( "id" ) instanceof String ) {
      situation.id = ( String ) conditions.get( "id" );
    }

    if ( conditions.containsKey( "status" ) && conditions.get( "status" ) instanceof Integer ) {
      situation.status = ( int ) conditions.get( "status" );

    } else {
      situation.status = QueryStatus.NOT_STATUS.getCode();
    }

    if ( conditions.containsKey( "createTimeStarted" ) && conditions.containsKey( "createTimeEnd" ) ) {
      situation.createTimeStarted = ( long ) conditions.get( "createTimeStarted" );
      situation.createTimeEnd = ( long ) conditions.get( "createTimeEnd" );
    }

    if ( conditions.containsKey( "updateTimeStarted" ) && conditions.containsKey( "updateTimeEnd" ) ) {
      situation.createTimeStarted = ( long ) conditions.get( "updateTimeStarted" );
      situation.createTimeEnd = ( long ) conditions.get( "updateTimeEnd" );
    }

    return situation;
  }

  @SuppressWarnings( "unchecked" )
  public <Entity extends BasicEntity<Entity>> BasicRuntime<Entity> build( BasicRuntime<Entity> runtime ) {
    CriteriaBuilder builder = runtime.getBuilder();
    Root table = runtime.getTable();

    if ( !StringUtils.isEmpty( id ) ) {
      runtime.where( builder.equal( table.get( "id" ), id ) );
    }

    if ( createTimeStarted != -1 && createTimeEnd != -1 ) {
      runtime.where(
          builder.greaterThanOrEqualTo( table.get( "createTime" ), createTimeStarted ),
          builder.lessThan( table.get( "createTime" ), createTimeEnd ) );
    }

    if ( updateTimeStarted != -1 && updateTimeEnd != -1 ) {
      runtime.where(
          builder.greaterThanOrEqualTo( table.get( "updateTime" ), updateTimeStarted ),
          builder.lessThan( table.get( "updateTime" ), updateTimeEnd ) );
    }

    return runtime;
  }
}
