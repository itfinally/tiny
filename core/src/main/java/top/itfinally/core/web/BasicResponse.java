package top.itfinally.core.web;

@SuppressWarnings( "unchecked" )
public class BasicResponse<Vo extends BasicResponse<Vo>> {
  private int code = ResponseStatus.SUCCESS.getCode();
  private String message = ResponseStatus.SUCCESS.getMessage();

  public BasicResponse() {
  }

  public BasicResponse( ResponseStatus status ) {
    code = status.getCode();
    message = status.getMessage();
  }

  public BasicResponse( BasicResponse<Vo> response ) {
    code = response.code;
    message = response.message;
  }

  public BasicResponse( int code, String message ) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public Vo setCode( int code ) {
    this.code = code;
    return ( Vo ) this;
  }

  public String getMessage() {
    return message;
  }

  public Vo setMessage( String message ) {
    this.message = message;
    return ( Vo ) this;
  }

  public static class It extends BasicResponse<It> {
    public It() {
    }

    public It( ResponseStatus status ) {
      super( status );
    }

    public It( BasicResponse<It> response ) {
      super( response );
    }

    public It( int code, String message ) {
      super( code, message );
    }
  }
}
