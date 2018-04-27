package top.itfinally.core.repository;

import top.itfinally.core.EntityStatus;
import top.itfinally.core.QueryStatus;

@SuppressWarnings( "unchecked" )
public class BasicQuerySituation<Situation extends BasicQuerySituation<Situation>> {
  protected int beginRow = QueryStatus.NOT_PAGING.getCode();
  protected int row = QueryStatus.NOT_PAGING.getCode();
  protected int status = EntityStatus.NORMAL.getCode();

  public static class It extends BasicQuerySituation<It> {
    public It() {
    }

    public It( int status ) {
      super( status );
    }

    public It( int beginRow, int row ) {
      super( beginRow, row );
    }

    public It( int status, int beginRow, int row ) {
      super( status, beginRow, row );
    }
  }

  protected BasicQuerySituation() {
  }

  public BasicQuerySituation( int status ) {
    this( QueryStatus.NOT_PAGING.getCode(), QueryStatus.NOT_PAGING.getCode() );
    this.status = status;
  }

  public BasicQuerySituation( int beginRow, int row ) {
    this.beginRow = beginRow;
    this.row = row;
  }

  public BasicQuerySituation( int status, int beginRow, int row ) {
    this( beginRow, row );
    this.status = status;
  }

  public int getBeginRow() {
    return beginRow;
  }

  public Situation setBeginRow( int beginRow ) {
    this.beginRow = beginRow;
    return ( Situation ) this;
  }

  public int getRow() {
    return row;
  }

  public Situation setRow( int row ) {
    this.row = row;
    return ( Situation ) this;
  }

  public int getStatus() {
    return status;
  }

  public Situation setStatus( int status ) {
    this.status = status;
    return ( Situation ) this;
  }

  public boolean isPaging() {
    int code = QueryStatus.NOT_PAGING.getCode();
    return beginRow != code && row != code;
  }

  public boolean hasStatus() {
    return status != QueryStatus.NOT_STATUS.getCode();
  }
}
