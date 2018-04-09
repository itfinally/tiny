package top.itfinally.security

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import com.google.common.eventbus.EventBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.authentication.*
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import top.itfinally.security.component.*

@Configuration
open class SecurityBeanFactory {
  @Bean
  open fun shaPasswordEncoderBean(): PasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean(name = ["securityEventBus"])
  open fun securityEventBus(): EventBus {
    return EventBus()
  }

  @Bean
  open fun daoAuthenticationProvider(userDetailCachingComponent: BasicUserSecurityComponent<*>,
                                     passwordEncoder: PasswordEncoder): DaoAuthenticationProvider {

    val daoAuthenticationProvider = DaoAuthenticationProvider()
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder)
    daoAuthenticationProvider.setUserDetailsService(userDetailCachingComponent)

    return daoAuthenticationProvider
  }

  @Bean
  open fun authenticationManager(daoAuthenticationProvider: DaoAuthenticationProvider): AuthenticationManager {
    return ProviderManager(listOf(daoAuthenticationProvider, AnonymousAuthenticationProvider("noMatch")))
  }
}

open class SecurityConfigure : WebSecurityConfigurerAdapter() {

  @Autowired
  private
  lateinit var jwtLoginProcessingFilter: JwtLoginProcessingFilter

  @Autowired
  private
  lateinit var jwtAuthorizationFilter: JwtAuthorizationFilter

  @Autowired
  private
  lateinit var forbiddenHandler: AbstractAccessForbiddenHandler

  @Autowired
  private
  lateinit var logoutHandler: JwtLogoutHandler

  @Autowired
  private
  lateinit var permissionValidationComponent: PermissionEvaluator

  @Autowired
  private
  lateinit var userSecurityComponent: BasicUserSecurityComponent<*>

  override fun configure(http: HttpSecurity) {
    http.cors().and()

        .addFilterBefore(jwtLoginProcessingFilter, UsernamePasswordAuthenticationFilter::class.java)
        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)

        .exceptionHandling()
        .accessDeniedHandler(forbiddenHandler)
        .authenticationEntryPoint(forbiddenHandler)

        .and()

        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()

        .logout()
        .logoutUrl("/verifies/logout")
        .addLogoutHandler(logoutHandler)
        .logoutSuccessHandler(logoutHandler)

        .and()

        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .antMatchers("/verifies/get_valid_image").permitAll()
        .anyRequest().authenticated()
  }

  override fun configure(web: WebSecurity) {
    val handler = DefaultWebSecurityExpressionHandler()
    handler.setPermissionEvaluator(permissionValidationComponent)

    web.expressionHandler(handler)
  }

  override fun configure(auth: AuthenticationManagerBuilder) {
    if (null == userSecurityComponent as BasicUserSecurityComponent<*>?) {
      throw UnsupportedOperationException("Please implement interface 'BasicUserSecurityComponent' before use it.")
    }

    auth.userDetailsService(userSecurityComponent)
  }
}