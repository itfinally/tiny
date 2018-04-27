package top.itfinally.console.repository;

import top.itfinally.core.BasicRuntime;
import top.itfinally.core.repository.BasicEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.util.Map;

public class AccessLogQuerySituation extends ConditionQuerySituation {
  private String sourceIp = "";
  private String username = "";
  private Boolean isException = null;

  protected AccessLogQuerySituation() {
  }

  public String getSourceIp() {
    return sourceIp;
  }

  public AccessLogQuerySituation setSourceIp( String sourceIp ) {
    this.sourceIp = sourceIp;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public AccessLogQuerySituation setUsername( String username ) {
    this.username = username;
    return this;
  }

  public boolean isException() {
    return isException;
  }

  public AccessLogQuerySituation setException( boolean exception ) {
    isException = exception;
    return this;
  }

  public static AccessLogQuerySituation build( Map<String, Object> conditions ) {
    AccessLogQuerySituation situation = new AccessLogQuerySituation();
    if ( null == conditions ) {
      return situation;
    }

    if ( conditions.containsKey( "sourceIp" ) && conditions.get( "sourceIp" ) instanceof String ) {
      situation.sourceIp = ( String ) conditions.get( "sourceIp" );
    }

    if ( conditions.containsKey( "username" ) && conditions.get( "sourceIp" ) instanceof String ) {
      situation.username = ( String ) conditions.get( "username" );
    }

    if ( conditions.containsKey( "isException" ) && conditions.get( "sourceIp" ) instanceof Boolean ) {
      situation.isException = ( boolean ) conditions.get( "isException" );
    }

    return situation;
  }

  @Override
  public <Entity extends BasicEntity<Entity>> BasicRuntime<Entity> build( BasicRuntime<Entity> runtime ) {
    super.build( runtime );

    CriteriaBuilder builder = runtime.getBuilder();
    Root<Entity> table = runtime.getTable();


    if ( isException != null ) {
      runtime.where( builder.equal( table.get( "isException" ), isException ) );
    }

    if ( !username.isEmpty() ) {
      runtime.where( builder.equal( table.get( "username" ), username ) );
    }

    if ( !sourceIp.isEmpty() ) {
      runtime.where( builder.like( table.get( "sourceIp" ), sourceIp + "%" ) );
    }

    return runtime;
  }
}
