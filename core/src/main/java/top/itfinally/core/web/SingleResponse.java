package top.itfinally.core.web;

public class SingleResponse<T> extends BasicResponse<SingleResponse<T>> {
  private T result;

  public SingleResponse() {
  }

  public SingleResponse( ResponseStatus status ) {
    super( status );
  }

  public SingleResponse( SingleResponse<T> response ) {
    super( response );
  }

  public SingleResponse( int code, String message ) {
    super( code, message );
  }

  public T getResult() {
    return result;
  }

  public SingleResponse<T> setResult( T result ) {
    this.result = result;
    return this;
  }
}
