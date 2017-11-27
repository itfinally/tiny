package top.itfinally.security.web.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.service.JwtTokenService;
import top.itfinally.security.service.KaptchaService;
import top.itfinally.security.service.UserDetailCachingService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private UserDetailCachingService userDetailCachingService;
    private AccessForbiddenHandler accessForbiddenHandler;
    private JwtTokenService jwtTokenService;
    private KaptchaService kaptchaService;

    protected JwtAuthenticationProcessingFilter() {
        super( "/verifies/login" );
    }

    @Override
    @Autowired
    public void setAuthenticationManager( AuthenticationManager authenticationManager ) {
        super.setAuthenticationManager( authenticationManager );
    }

    @Autowired
    public JwtAuthenticationProcessingFilter setUserDetailCachingService( UserDetailCachingService userDetailCachingService ) {
        this.userDetailCachingService = userDetailCachingService;
        return this;
    }

    @Autowired
    public JwtAuthenticationProcessingFilter setAccessForbiddenHandler( AccessForbiddenHandler accessForbiddenHandler ) {
        this.accessForbiddenHandler = accessForbiddenHandler;
        return this;
    }

    @Autowired
    public JwtAuthenticationProcessingFilter setJwtTokenService( JwtTokenService jwtTokenService ) {
        this.jwtTokenService = jwtTokenService;
        return this;
    }

    @Autowired
    public JwtAuthenticationProcessingFilter setKaptchaService( KaptchaService kaptchaService ) {
        this.kaptchaService = kaptchaService;
        return this;
    }

    @Override
    public Authentication attemptAuthentication( HttpServletRequest request, HttpServletResponse response ) throws AuthenticationException, IOException, ServletException {
        String token = request.getHeader( "Authorization" ),
                validCode = request.getParameter( "validCode" );

        if ( StringUtils.isBlank( token ) || !token.startsWith( "Basic " ) ) {
            throw new BadCredentialsException( "Missing token in request headers." );
        }

        token = new String( Base64.decode( token.substring( 6 ).getBytes() ), "UTF-8" );

        if ( !token.contains( ":" ) ) {
            throw new BadCredentialsException( "Invalid basic authentication token." );
        }

        String[] entry = token.split( ":" );
        if ( kaptchaService.requireValid( entry[ 0 ] ) && !kaptchaService.valid( entry[ 0 ], validCode ) ) {
            throw new BadCredentialsException( "Wrong valid code." );
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                entry[ 0 ], entry[ 1 ]
        );

        try {
            return getAuthenticationManager().authenticate( authToken );

        } catch ( AuthenticationException ex ) {
            kaptchaService.count( entry[ 0 ] );
            throw ex;
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult
    ) throws IOException, ServletException {

        UserAuthorityEntity userAuthority = ( UserAuthorityEntity ) authResult.getPrincipal();
        String token = jwtTokenService.create( userAuthority.getUsername() );
        userDetailCachingService.caching( userAuthority.getUsername(), userAuthority );

        response.setContentType( "application/json;charset=UTF-8" );
        response.getWriter().write( jsonMapper.writeValueAsString(
                new SingleResponseVoBean<>( ResponseStatusEnum.SUCCESS ).setResult( token )
        ) );
    }

    @Override
    protected void unsuccessfulAuthentication( HttpServletRequest request, HttpServletResponse response, AuthenticationException failed ) throws IOException, ServletException {
        accessForbiddenHandler.commence( request, response, failed );
    }
}
