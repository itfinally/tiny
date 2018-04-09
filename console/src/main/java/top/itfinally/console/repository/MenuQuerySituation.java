package top.itfinally.console.repository;

import top.itfinally.core.repository.BasicQuerySituation;

public class MenuQuerySituation extends BasicQuerySituation<MenuQuerySituation> {
  // direct parent or first born child
  private boolean isDirect = false;

  public MenuQuerySituation() {
  }

  public MenuQuerySituation( int status ) {
    super( status );
  }

  public MenuQuerySituation( int beginRow, int row ) {
    super( beginRow, row );
  }

  public MenuQuerySituation( int status, int beginRow, int row ) {
    super( status, beginRow, row );
  }

  public boolean isDirect() {
    return isDirect;
  }

  public MenuQuerySituation setDirect( boolean direct ) {
    isDirect = direct;
    return this;
  }
}
