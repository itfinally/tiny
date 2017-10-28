package top.itfinally.core.exception;

public class SqlOperationException extends BaseBusinessException {
    public SqlOperationException() {
    }

    public SqlOperationException( String message ) {
        super( message );
    }

    public SqlOperationException( String message, Throwable cause ) {
        super( message, cause );
    }

    public SqlOperationException( Throwable cause ) {
        super( cause );
    }

    public SqlOperationException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
