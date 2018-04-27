package top.itfinally.console

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.HeaderWriterFilter
import org.springframework.transaction.annotation.EnableTransactionManagement
import top.itfinally.console.web.AccessLoggerInterceptor
import top.itfinally.console.web.HoldingErrorInterceptor
import top.itfinally.security.SecurityConfiguration


@EnableTransactionManagement
@EntityScan(basePackages = ["top.itfinally"])
class ConsoleAutoConfiguration

open class ConsoleSecurityConfiguration : SecurityConfiguration() {

  @Autowired
  private lateinit var accessLoggerInterceptor: AccessLoggerInterceptor

  @Autowired
  private lateinit var holdingErrorInterceptor: HoldingErrorInterceptor

  override fun configure(http: HttpSecurity) {
    super.configure(http
        .addFilterBefore(holdingErrorInterceptor, HeaderWriterFilter::class.java)
        .addFilterBefore(accessLoggerInterceptor, UsernamePasswordAuthenticationFilter::class.java))
  }
}