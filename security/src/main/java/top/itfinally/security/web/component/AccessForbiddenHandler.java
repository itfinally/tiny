package top.itfinally.security.web.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.BaseResponseVoBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This abstract component will be custom the response data.
 * It is easy to custom any data structure by extends this component and override all method.
 */
public abstract class AccessForbiddenHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private ObjectMapper jsonMapper = new ObjectMapper();

    // called when user authentication fails
    protected abstract Object authorityException( AuthenticationException authException );

    // called when user illegal access resource
    protected abstract Object accessDeniedException( AccessDeniedException accessDeniedException );

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException, ServletException {
        handler( response, authorityException( authException ) );
    }

    @Override
    public void handle( HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException ) throws IOException, ServletException {
        handler( response, accessDeniedException( accessDeniedException ) );
    }

    private void handler( HttpServletResponse response, Object result ) throws IOException {
        response.setHeader( "Content-Type", "application/json;charset=UTF-8" );
        response.getWriter().write( jsonMapper.writeValueAsString( result ) );
    }

    @Component
    public static class Default extends AccessForbiddenHandler {

        @Override
        protected Object authorityException( AuthenticationException authException ) {
            return handler( authException.getMessage() );
        }

        @Override
        protected Object accessDeniedException( AccessDeniedException accessDeniedException ) {
            return handler( accessDeniedException.getMessage() );
        }

        private Object handler( String message ) {
            return new BaseResponseVoBean<>( ResponseStatusEnum.UNAUTHORIZED ).setMessage( message );
        }
    }
}
