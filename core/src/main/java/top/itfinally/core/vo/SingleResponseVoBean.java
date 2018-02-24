package top.itfinally.core.vo;

import top.itfinally.core.enumerate.BaseResponseStatus;

import java.util.Objects;

public class SingleResponseVoBean<T> extends BaseResponseVoBean<SingleResponseVoBean> {
  private T result;

  public SingleResponseVoBean() {
  }

  public SingleResponseVoBean( BaseResponseStatus baseResponseStatus ) {
    super( baseResponseStatus );
  }

  public SingleResponseVoBean( SingleResponseVoBean responseVoBean ) {
    super( responseVoBean );
  }

  public T getResult() {
    return result;
  }

  public SingleResponseVoBean<T> setResult( T result ) {
    this.result = result;
    return this;
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  @Override
  public SingleResponseVoBean<T> setMessage( String message ) {
    this.message = message;
    return this;
  }

  @Override
  public int getStatusCode() {
    return super.getStatusCode();
  }

  @Override
  public SingleResponseVoBean setStatusCode( int statusCode ) {
    this.statusCode = statusCode;
    return this;
  }

  @Override
  public String toString() {
    return "SingleResponseVoBean{" +
        "message='" + message + '\'' +
        ", result=" + result +
        ", statusCode=" + statusCode +
        '}';
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    SingleResponseVoBean<?> that = ( SingleResponseVoBean<?> ) o;
    return Objects.equals( result, that.result );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), result );
  }
}
