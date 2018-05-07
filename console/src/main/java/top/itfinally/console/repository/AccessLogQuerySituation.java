package top.itfinally.console.repository;

import top.itfinally.core.BasicRuntime;
import top.itfinally.core.repository.BasicEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.Map;

public class AccessLogQuerySituation extends ConditionQuerySituation {
  protected String path;
  protected String method;
  protected String sourceIp;
  protected String username;
  protected Boolean isException;

  protected AccessLogQuerySituation() {
  }

  @SuppressWarnings( "unchecked" )
  public static class InnerBuilder<SituationType extends AccessLogQuerySituation, BuilderType
      extends ConditionQuerySituation.InnerBuilder<SituationType, BuilderType>>
      extends ConditionQuerySituation.InnerBuilder<SituationType, BuilderType> {

    private String path = "";
    private String method = "";
    private String sourceIp = "";
    private String username = "";
    private Boolean isException = null;

    protected InnerBuilder() {
    }

    protected InnerBuilder( Map<String, Object> conditions ) {
      super( conditions );

      if ( null == conditions ) {
        return;
      }

      if ( conditions.containsKey( "path" ) && conditions.get( "path" ) instanceof String ) {
        path = ( String ) conditions.get( "path" );
      }

      if ( conditions.containsKey( "method" ) && conditions.get( "method" ) instanceof String ) {
        method = ( String ) conditions.get( "method" );
      }

      if ( conditions.containsKey( "sourceIp" ) && conditions.get( "sourceIp" ) instanceof String ) {
        sourceIp = ( String ) conditions.get( "sourceIp" );
      }

      if ( conditions.containsKey( "username" ) && conditions.get( "username" ) instanceof String ) {
        username = ( String ) conditions.get( "username" );
      }

      if ( conditions.containsKey( "isException" ) && conditions.get( "isException" ) instanceof Boolean ) {
        isException = ( boolean ) conditions.get( "isException" );
      }
    }

    public BuilderType setSourceIp( String sourceIp ) {
      this.sourceIp = sourceIp;
      return ( BuilderType ) this;
    }

    public BuilderType setUsername( String username ) {
      this.username = username;
      return ( BuilderType ) this;
    }

    public BuilderType setException( Boolean exception ) {
      isException = exception;
      return ( BuilderType ) this;
    }

    @Override
    protected SituationType build( SituationType situation ) {
      situation.path = path;
      situation.method = method;
      situation.sourceIp = sourceIp;
      situation.username = username;
      situation.isException = isException;

      return super.build( situation );
    }
  }

  public static class Builder extends InnerBuilder<AccessLogQuerySituation, Builder> {
    public Builder() {
    }

    public Builder( Map<String, Object> conditions ) {
      super( conditions );
    }

    public AccessLogQuerySituation build() {
      return super.build( new AccessLogQuerySituation() );
    }
  }

  public String getPath() {
    return path;
  }

  public String getSourceIp() {
    return sourceIp;
  }

  public String getUsername() {
    return username;
  }

  public Boolean getException() {
    return isException;
  }

  @Override
  public <Entity extends BasicEntity<Entity>> BasicRuntime<Entity> build( BasicRuntime<Entity> runtime ) {
    super.build( runtime );

    CriteriaBuilder builder = runtime.getBuilder();
    Root<Entity> table = runtime.getTable();

    if ( isException != null ) {
      Path<Boolean> field = table.get( "exception" );
      runtime.where( builder.isTrue( isException ? builder.isTrue( field ) : builder.isFalse( field ) ) );
    }

    if ( !username.isEmpty() ) {
      runtime.where( builder.equal( table.get( "username" ), username ) );
    }

    if ( !method.isEmpty() ) {
      runtime.where( builder.equal( table.get( "requestMethod" ), method ) );
    }

    if ( !sourceIp.isEmpty() ) {
      runtime.where( builder.like( table.get( "sourceIp" ), sourceIp + "%" ) );
    }

    if ( !path.isEmpty() ) {
      runtime.where( builder.like( table.get( "requestPath" ), path + "%" ) );
    }

    return runtime;
  }
}
