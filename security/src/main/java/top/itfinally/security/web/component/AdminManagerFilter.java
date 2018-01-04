package top.itfinally.security.web.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.security.service.AdminManagerService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static top.itfinally.core.enumerate.ResponseStatusEnum.SERVER_ERROR;

@Component
public class AdminManagerFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger( getClass() );
    private ObjectMapper jsonMapper = new ObjectMapper();
    private Map<String, Method> methodMapper;
    private WebApplicationContext context;

    private volatile AdminManagerService adminManagerService;

    {
        Map<String, Method> methodMapper = new HashMap<>();
        Method[] methods = getClass().getDeclaredMethods();
        for ( Method method : methods ) {
            if ( "setContext doFilterInternal".contains( method.getName() ) ) {
                continue;
            }

            // Only get method in http request, otherwise raise a null point exception!
            GetMapping getMapping = method.getAnnotation( GetMapping.class );
            methodMapper.put( getMapping.value()[ 0 ], method );
        }

        this.methodMapper = Collections.unmodifiableMap( methodMapper );
    }

    @Autowired
    public AdminManagerFilter setContext( WebApplicationContext context ) {
        this.context = context;
        return this;
    }

    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {

        if ( !( "127.0.0.1".equals( request.getRemoteHost() ) &&
                "GET".equals( request.getMethod() ) &&
                methodMapper.containsKey( request.getRequestURI() ) ) ) {

            filterChain.doFilter( request, response );
            return;
        }

        if ( null == adminManagerService ) {
            synchronized ( this ) {
                if ( null == adminManagerService ) {
                    this.adminManagerService = context.getBean( AdminManagerService.class );
                }
            }
        }

        Object result;

        try {
            result = methodMapper.get( request.getRequestURI() ).invoke( this );

        } catch ( Exception allException ) {
            logger.error( "Raise a exception from request" + request.getRequestURI(), allException );
            result = new BaseResponseVoBean<>( SERVER_ERROR ).setMessage( allException.getMessage() );
        }

        response.getWriter().write( jsonMapper.writeValueAsString( result ) );
    }

    @GetMapping( "/admin/initialization" )
    private BaseResponseVoBean initialization() {
        return adminManagerService.initialization();
    }

    @GetMapping( "/admin/create" )
    private BaseResponseVoBean createAdmin() {
        return adminManagerService.createAdminAccount();
    }

    @GetMapping( "/admin/lock" )
    public BaseResponseVoBean lockAdmin() {
        return adminManagerService.lockAdminAccount();
    }
}
