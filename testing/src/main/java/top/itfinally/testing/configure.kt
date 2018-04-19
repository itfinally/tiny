package top.itfinally.testing

import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import top.itfinally.security.SecurityConfiguration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import top.itfinally.console.ConsoleSecurityConfiguration


@EnableWebSecurity
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class ConsoleSecurityConfigure : ConsoleSecurityConfiguration() {
  override fun configure(http: HttpSecurity) {
    http.authorizeRequests()
        .antMatchers("/testing/**").permitAll()

    super.configure(http)
  }
}

@Configuration
open class CorsConfigure : WebMvcConfigurer {
  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/**")
        .allowCredentials(true)
        .allowedHeaders("*")
        .allowedMethods("*")
        .allowedOrigins("*")
  }
}