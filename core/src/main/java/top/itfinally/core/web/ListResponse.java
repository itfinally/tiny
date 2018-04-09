package top.itfinally.core.web;

import java.util.ArrayList;
import java.util.List;

public class ListResponse<T> extends BasicResponse<ListResponse<T>> {
  private List<T> result = new ArrayList<>();

  public ListResponse() {
  }

  public ListResponse( ResponseStatus status ) {
    super( status );
  }

  public ListResponse( BasicResponse<ListResponse<T>> response ) {
    super( response );
  }

  public ListResponse( int code, String message ) {
    super( code, message );
  }

  public List<T> getResult() {
    return result;
  }

  public ListResponse<T> setResult( List<T> result ) {
    this.result = result;
    return this;
  }
}
