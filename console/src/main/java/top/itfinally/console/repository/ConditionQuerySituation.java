package top.itfinally.console.repository;

import org.springframework.util.StringUtils;
import top.itfinally.core.BasicRuntime;
import top.itfinally.core.QueryStatus;
import top.itfinally.core.repository.BasicEntity;
import top.itfinally.core.repository.BasicQuerySituation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.util.Map;

@SuppressWarnings( "unchecked" )
public class ConditionQuerySituation extends BasicQuerySituation {
  protected String id;
  protected long createTimeStarted;
  protected long createTimeEnd;
  protected long updateTimeStarted;
  protected long updateTimeEnd;

  protected ConditionQuerySituation() {
  }

  protected static class InnerBuilder<SituationType extends ConditionQuerySituation, BuilderType
      extends BasicQuerySituation.InnerBuilder<SituationType, BuilderType>>
      extends BasicQuerySituation.InnerBuilder<SituationType, BuilderType> {

    private String id = null;
    private long createTimeStarted = -1;
    private long createTimeEnd = -1;
    private long updateTimeStarted = -1;
    private long updateTimeEnd = -1;

    protected InnerBuilder() {
    }

    protected InnerBuilder( Map<String, Object> conditions ) {
      super( conditions );

      if ( null == conditions ) {
        return;
      }

      if ( conditions.containsKey( "id" ) && conditions.get( "id" ) instanceof String ) {
        id = ( String ) conditions.get( "id" );
      }

      if ( conditions.containsKey( "status" ) && conditions.get( "status" ) instanceof Integer ) {
        super.setStatus( ( int ) conditions.get( "status" ) );

      } else {
        super.setStatus( QueryStatus.NOT_STATUS.getCode() );
      }

      if ( conditions.containsKey( "createTimeStarted" ) && conditions.containsKey( "createTimeEnd" ) ) {
        createTimeStarted = ( long ) conditions.get( "createTimeStarted" );
        createTimeEnd = ( long ) conditions.get( "createTimeEnd" );
      }

      if ( conditions.containsKey( "updateTimeStarted" ) && conditions.containsKey( "updateTimeEnd" ) ) {
        createTimeStarted = ( long ) conditions.get( "updateTimeStarted" );
        createTimeEnd = ( long ) conditions.get( "updateTimeEnd" );
      }
    }

    @Override
    protected SituationType build( SituationType situation ) {
      situation.id = id;
      situation.createTimeStarted = createTimeStarted;
      situation.createTimeEnd = createTimeEnd;
      situation.updateTimeStarted = updateTimeStarted;
      situation.updateTimeEnd = updateTimeEnd;

      return super.build( situation );
    }
  }

  public static class Builder extends InnerBuilder<ConditionQuerySituation, Builder> {
    public Builder() {
    }

    public Builder( Map<String, Object> conditions ) {
      super( conditions );
    }

    public ConditionQuerySituation build() {
      return super.build( new ConditionQuerySituation() );
    }
  }

  public String getId() {
    return id;
  }

  public long getCreateTimeStarted() {
    return createTimeStarted;
  }

  public long getCreateTimeEnd() {
    return createTimeEnd;
  }

  public long getUpdateTimeStarted() {
    return updateTimeStarted;
  }

  public long getUpdateTimeEnd() {
    return updateTimeEnd;
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
