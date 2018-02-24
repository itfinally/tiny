package top.itfinally.security.exception;

import top.itfinally.core.exception.BaseBusinessException;

public class UserNotFoundException extends BaseBusinessException {
  public UserNotFoundException() {
  }

  public UserNotFoundException( String message ) {
    super( message );
  }

  public UserNotFoundException( String message, Throwable cause ) {
    super( message, cause );
  }

  public UserNotFoundException( Throwable cause ) {
    super( cause );
  }

  public UserNotFoundException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
