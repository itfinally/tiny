package top.itfinally.security.web.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.service.JwtTokenService;
import top.itfinally.security.service.UserDetailCachingService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.itfinally.core.enumerate.ResponseStatusEnum.UNAUTHORIZED;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private ObjectMapper jsonMapper = new ObjectMapper();
    private UserDetailCachingService userDetailCachingService;
    private JwtTokenService tokenService;

    @Autowired
    public JwtAuthorizationFilter setUserDetailCachingService( UserDetailCachingService userDetailCachingService ) {
        this.userDetailCachingService = userDetailCachingService;
        return this;
    }

    @Autowired
    public JwtAuthorizationFilter setTokenService( JwtTokenService tokenService ) {
        this.tokenService = tokenService;
        return this;
    }

    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain )
            throws ServletException, IOException {

        String token = request.getHeader( "Authorization" );

        if ( StringUtils.isBlank( token ) || !token.startsWith( "Bearer " ) ) {
            filterChain.doFilter( request, response );
            return;
        }

        token = token.substring( 6 );
        String account = tokenService.loadByToken( token );

        if ( StringUtils.isBlank( account ) ) {
            filterChain.doFilter( request, response );
            return;
        }

        UserAuthorityEntity user = this.userDetailCachingService.loadFromCache( account );

        if ( null == user ) {
            response.getWriter().write( jsonMapper.writeValueAsString(
                    new BaseResponseVoBean<>( UNAUTHORIZED ).setMessage( "Token expired." ) ) );

            return;
        }

        JwtAuthenticationToken authResult = new JwtAuthenticationToken( token, user );

        authResult.setDetails( this.authenticationDetailsSource.buildDetails( request ) );

        SecurityContextHolder.getContext().setAuthentication( authResult );

        filterChain.doFilter( request, response );
    }
}
