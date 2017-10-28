package top.itfinally.security.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import top.itfinally.security.web.component.AccessForbiddenHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity( debug = true )
@Order( SecurityProperties.ACCESS_OVERRIDE_ORDER )
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfigure extends WebSecurityConfigurerAdapter {

    private AbstractAuthenticationProcessingFilter jwtAuthenticationProcessingFilter;
    private AuthenticationEntryPoint unAuthorizationEntryPoint;
    private OncePerRequestFilter jwtAuthorizationFilter;
    private PermissionEvaluator permissionValidService;
    private AccessDeniedHandler accessDeniedHandler;
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder shaPasswordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public SecurityConfigure setJwtAuthenticationProcessingFilter( AbstractAuthenticationProcessingFilter jwtAuthenticationProcessingFilter ) {
        this.jwtAuthenticationProcessingFilter = jwtAuthenticationProcessingFilter;
        return this;
    }

    @Autowired
    public SecurityConfigure setUnAuthorizationEntryPoint( AccessForbiddenHandler forbiddenEntryPoint ) {
        this.unAuthorizationEntryPoint = forbiddenEntryPoint;
        this.accessDeniedHandler = forbiddenEntryPoint;
        return this;
    }

    @Autowired
    public SecurityConfigure setJwtAuthorizationFilter( OncePerRequestFilter jwtAuthorizationFilter ) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        return this;
    }

    @Autowired
    public SecurityConfigure setPermissionValidService( PermissionEvaluator permissionValidService ) {
        this.permissionValidService = permissionValidService;
        return this;
    }

    @Autowired
    public SecurityConfigure setUserDetailsService( UserDetailsService userDetailsService ) {
        this.userDetailsService = userDetailsService;
        return this;
    }

    @Override
    protected void configure( HttpSecurity http ) throws Exception {
        http.addFilterBefore( jwtAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class )
                .addFilterBefore( jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class )

                .exceptionHandling()
                .accessDeniedHandler( accessDeniedHandler )
                .authenticationEntryPoint( unAuthorizationEntryPoint )

                .and()

                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .anonymous().disable()      // Cannot access '/verifies/login' to login if disable anonymous request
                .sessionManagement().sessionCreationPolicy( STATELESS )

                .and()

                .authorizeRequests()
                .antMatchers( HttpMethod.OPTIONS ).permitAll()
                .antMatchers( "/verifies/**" ).anonymous()
                .regexMatchers( "^/authorization/(get_roles|get_permissions)" ).permitAll()
                .antMatchers( "/authorization/**" ).hasRole( "ADMIN" )
                .antMatchers( "/admin/**" ).hasIpAddress( "127.0.0.1" )
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
