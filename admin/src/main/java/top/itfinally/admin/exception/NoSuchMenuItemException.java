package top.itfinally.admin.exception;

import top.itfinally.core.exception.BaseBusinessException;

public class NoSuchMenuItemException extends BaseBusinessException {
    public NoSuchMenuItemException() {
    }

    public NoSuchMenuItemException( String message ) {
        super( message );
    }

    public NoSuchMenuItemException( String message, Throwable cause ) {
        super( message, cause );
    }

    public NoSuchMenuItemException( Throwable cause ) {
        super( cause );
    }

    public NoSuchMenuItemException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
