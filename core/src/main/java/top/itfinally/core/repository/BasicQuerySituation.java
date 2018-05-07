package top.itfinally.core.repository;

import top.itfinally.core.EntityStatus;
import top.itfinally.core.QueryStatus;

import java.util.Map;

public class BasicQuerySituation {
  protected int beginRow;
  protected int status;
  protected int row;

  @SuppressWarnings( "unchecked" )
  protected static class InnerBuilder<SituationType extends BasicQuerySituation,
      BuilderType extends BasicQuerySituation.InnerBuilder<SituationType, BuilderType>> {
    private int beginRow = QueryStatus.NOT_PAGING.getCode();
    private int row = QueryStatus.NOT_PAGING.getCode();
    private int status = EntityStatus.NORMAL.getCode();

    protected InnerBuilder() {
    }

    protected InnerBuilder( Map<String, Object> conditions ) {
      if ( null == conditions ) {
        return;
      }

      if ( conditions.containsKey( "status" ) && conditions.get( "status" ) instanceof Integer ) {
        status = ( int ) conditions.get( "status" );
      }

      if ( conditions.containsKey( "beginRow" ) && conditions.get( "beginRow" ) instanceof Integer ) {
        beginRow = ( int ) conditions.get( "beginRow" );
      }

      if ( conditions.containsKey( "row" ) && conditions.get( "row" ) instanceof Integer ) {
        row = ( int ) conditions.get( "row" );
      }
    }

    public BuilderType setBeginRow( int beginRow ) {
      this.beginRow = beginRow;
      return ( BuilderType ) this;
    }

    public BuilderType setRow( int row ) {
      this.row = row;
      return ( BuilderType ) this;
    }

    public BuilderType setStatus( int status ) {
      this.status = status;
      return ( BuilderType ) this;
    }

    protected SituationType build( SituationType situation ) {
      situation.beginRow = beginRow;
      situation.status = status;
      situation.row = row;

      return situation;
    }
  }

  public static class Builder extends InnerBuilder<BasicQuerySituation, Builder> {
    public Builder() {
    }

    public Builder( Map<String, Object> conditions ) {
      super( conditions );
    }

    public BasicQuerySituation build() {
      return super.build( new BasicQuerySituation() );
    }
  }

  protected BasicQuerySituation() {
  }

  public int getBeginRow() {
    return beginRow;
  }

  public int getRow() {
    return row;
  }

  public int getStatus() {
    return status;
  }

  public boolean isPaging() {
    int code = QueryStatus.NOT_PAGING.getCode();
    return beginRow != code && row != code;
  }

  public boolean hasStatus() {
    return status != QueryStatus.NOT_STATUS.getCode();
  }
}
