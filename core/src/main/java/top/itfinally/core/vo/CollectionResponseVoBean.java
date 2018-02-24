package top.itfinally.core.vo;

import top.itfinally.core.enumerate.BaseResponseStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class CollectionResponseVoBean<T> extends BaseResponseVoBean<CollectionResponseVoBean<T>> {
  private Collection<T> result = new ArrayList<>();

  public CollectionResponseVoBean() {
  }

  public CollectionResponseVoBean( BaseResponseStatus baseResponseStatus ) {
    super( baseResponseStatus );
  }

  public CollectionResponseVoBean( CollectionResponseVoBean<T> responseVoBean ) {
    super( responseVoBean );
  }

  public Collection<T> getResult() {
    return result;
  }

  public CollectionResponseVoBean<T> setResult( Collection<T> result ) {
    this.result = result;
    return this;
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  @Override
  public CollectionResponseVoBean<T> setMessage( String message ) {
    this.message = message;
    return this;
  }

  @Override
  public int getStatusCode() {
    return super.getStatusCode();
  }

  @Override
  public CollectionResponseVoBean<T> setStatusCode( int statusCode ) {
    this.statusCode = statusCode;
    return this;
  }

  @Override
  public String toString() {
    return "CollectionResponseVoBean{" +
        "message='" + message + '\'' +
        ", statusCode=" + statusCode +
        ", result=" + result +
        '}';
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    CollectionResponseVoBean<?> that = ( CollectionResponseVoBean<?> ) o;
    return Objects.equals( result, that.result );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), result );
  }
}