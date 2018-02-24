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
import top.itfinally.security.web.component.AccessForbiddenHandler;
import top.itfinally.security.web.component.JwtAuthorizationFilter;
import top.itfinally.security.web.component.JwtLogoutHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

public class BaseSecurityConfigure extends WebSecurityConfigurerAdapter {

  private AbstractAuthenticationProcessingFilter jwtAuthenticationProcessingFilter;
  private AuthenticationEntryPoint unAuthorizationEntryPoint;
  private JwtAuthorizationFilter jwtAuthorizationFilter;
  private PermissionEvaluator permissionValidService;
  private AccessDeniedHandler accessDeniedHandler;
  private UserDetailsService userDetailsService;
  private JwtLogoutHandler jwtLogoutHandler;

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
  public BaseSecurityConfigure setJwtAuthorizationFilter( JwtAuthorizationFilter jwtAuthorizationFilter ) {
    this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    return this;
  }

  @Autowired
  public BaseSecurityConfigure setPermissionValidService( PermissionEvaluator permissionValidService ) {
    this.permissionValidService = permissionValidService;
    return this;
  }

  @Autowired
  public BaseSecurityConfigure setUserDetailsService( UserDetailsService userDetailsService ) {
    this.userDetailsService = userDetailsService;
    return this;
  }

  @Autowired
  public BaseSecurityConfigure setJwtLogoutFilter( JwtLogoutHandler jwtLogoutHandler ) {
    this.jwtLogoutHandler = jwtLogoutHandler;
    return this;
  }

  @Override
  protected void configure( HttpSecurity http ) throws Exception {
    http.cors().and()

        .addFilterBefore( jwtAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class )
        .addFilterBefore( jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class )

        .exceptionHandling()
        .accessDeniedHandler( accessDeniedHandler )
        .authenticationEntryPoint( unAuthorizationEntryPoint )

        .and()

        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()
        .sessionManagement().sessionCreationPolicy( STATELESS )

        .and()

        .logout()
        .logoutUrl( "/verifies/logout" )
        .addLogoutHandler( jwtLogoutHandler )
        .logoutSuccessHandler( jwtLogoutHandler )

        .and()

        .authorizeRequests()
        .antMatchers( HttpMethod.OPTIONS, "/**" ).permitAll()
        .antMatchers( "/verifies/get_valid_image/**" ).permitAll()
        .regexMatchers( ".+/api$" ).hasRole( "ADMIN" )
        .antMatchers( "/authorization/**" ).hasRole( "ADMIN" )
        .antMatchers( "/admin/**" ).hasIpAddress( "127.0.0.1" )
        .anyRequest().authenticated();
  }

  @Override
  public void configure( WebSecurity web ) {
    DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
    handler.setPermissionEvaluator( permissionValidService );

    web.expressionHandler( handler );
  }

  @Override
  protected void configure( AuthenticationManagerBuilder auth ) throws Exception {
    auth.userDetailsService( userDetailsService );
  }
}
