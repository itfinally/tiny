package top.itfinally.core.vo;

import top.itfinally.core.enumerate.BaseResponseStatus;

import java.util.Objects;

public class BaseResponseVoBean<Vo extends BaseResponseVoBean> {
    protected String message;
    protected int statusCode;

    public BaseResponseVoBean() {}

    public BaseResponseVoBean( BaseResponseStatus baseResponseStatus ) {
        this.message = baseResponseStatus.getMessage();
        this.statusCode = baseResponseStatus.getStatusCode();
    }

    public BaseResponseVoBean( Vo responseVoBean ) {
        this.message = responseVoBean.getMessage();
        this.statusCode = responseVoBean.getStatusCode();
    }

    public String getMessage() {
        return message;
    }

    @SuppressWarnings( "unchecked" )
    public Vo setMessage( String message ) {
        this.message = message;
        return ( Vo ) this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @SuppressWarnings( "unchecked" )
    public Vo setStatusCode( int statusCode ) {
        this.statusCode = statusCode;
        return ( Vo ) this;
    }

    @Override
    public String toString() {
        return "BaseResponseVoBean{" +
                "message='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        BaseResponseVoBean<?> that = ( BaseResponseVoBean<?> ) o;
        return statusCode == that.statusCode &&
                Objects.equals( message, that.message );
    }

    @Override
    public int hashCode() {
        return Objects.hash( message, statusCode );
    }
}
