package top.itfinally.security.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import top.itfinally.security.web.component.AccessForbiddenHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

public class BaseSecurityConfigure extends WebSecurityConfigurerAdapter {

    private AbstractAuthenticationProcessingFilter jwtAuthenticationProcessingFilter;
    private AuthenticationEntryPoint unAuthorizationEntryPoint;
    private OncePerRequestFilter jwtAuthorizationFilter;
    private PermissionEvaluator permissionValidService;
    private AccessDeniedHandler accessDeniedHandler;
    private OncePerRequestFilter adminManagerFilter;
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder shaPasswordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public BaseSecurityConfigure setJwtAuthenticationProcessingFilter( AbstractAuthenticationProcessingFilter jwtAuthenticationProcessingFilter ) {
        this.jwtAuthenticationProcessingFilter = jwtAuthenticationProcessingFilter;
        return this;
    }

    @Autowired
    public BaseSecurityConfigure setUnAuthorizationEntryPoint( AccessForbiddenHandler forbiddenEntryPoint ) {
        this.unAuthorizationEntryPoint = forbiddenEntryPoint;
        this.accessDeniedHandler = forbiddenEntryPoint;
        return this;
    }

    @Autowired
    public BaseSecurityConfigure setJwtAuthorizationFilter( OncePerRequestFilter jwtAuthorizationFilter ) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        return this;
    }

    @Autowired
    public BaseSecurityConfigure setPermissionValidService( PermissionEvaluator permissionValidService ) {
        this.permissionValidService = permissionValidService;
        return this;
    }

    @Autowired
    public BaseSecurityConfigure setAdminAuthenticationFilter( OncePerRequestFilter adminManagerFilter ) {
        this.adminManagerFilter = adminManagerFilter;
        return this;
    }

    @Autowired
    public BaseSecurityConfigure setUserDetailsService( UserDetailsService userDetailsService ) {
        this.userDetailsService = userDetailsService;
        return this;
    }

    @Override
    protected void configure( HttpSecurity http ) throws Exception {
        http.cors().and()

                .addFilterBefore( jwtAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class )
                .addFilterBefore( jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class )
                .addFilterBefore( adminManagerFilter, HeaderWriterFilter.class )

                .exceptionHandling()
                .accessDeniedHandler( accessDeniedHandler )
                .authenticationEntryPoint( unAuthorizationEntryPoint )

                .and()

                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .anonymous().disable()
                .sessionManagement().sessionCreationPolicy( STATELESS )

                .and()

                .authorizeRequests()
                .antMatchers( HttpMethod.OPTIONS, "/**" ).permitAll()
                .regexMatchers( "^/authorization/(get_roles|get_permissions)" ).permitAll()
                .antMatchers( "/valid/get_valid_image/**" ).permitAll()
                .antMatchers( "/authorization/**" ).hasRole( "ADMIN" )
                .anyRequest().authenticated();
    }

    @Override
    public void configure( WebSecurity web ) throws Exception {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator( permissionValidService );

        web.expressionHandler( handler );
    }

    @Override
    protected void configure( AuthenticationManagerBuilder auth ) throws Exception {
        auth.userDetailsService( userDetailsService );
    }
}
