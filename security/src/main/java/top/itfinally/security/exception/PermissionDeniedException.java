package top.itfinally.security.exception;

import org.springframework.security.core.AuthenticationException;

public class PermissionDeniedException extends AuthenticationException {
    public PermissionDeniedException( String msg, Throwable t ) {
        super( msg, t );
    }

    public PermissionDeniedException( String msg ) {
        super( msg );
    }
}
