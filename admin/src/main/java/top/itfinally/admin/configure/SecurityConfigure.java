package top.itfinally.admin.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.header.HeaderWriterFilter;
import top.itfinally.admin.web.interceptor.SystemErrorInterceptor;
import top.itfinally.security.configure.BaseSecurityConfigure;

@EnableWebSecurity
@Order( SecurityProperties.ACCESS_OVERRIDE_ORDER )
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfigure extends BaseSecurityConfigure {

  private SystemErrorInterceptor systemErrorInterceptor;

  @Autowired
  public SecurityConfigure setSystemErrorInterceptor( SystemErrorInterceptor systemErrorInterceptor ) {
    this.systemErrorInterceptor = systemErrorInterceptor;
    return this;
  }

  @Override
  protected void configure( HttpSecurity http ) throws Exception {
    http.addFilterBefore( systemErrorInterceptor, HeaderWriterFilter.class ).authorizeRequests()
        .antMatchers( "/menu/menu_initializing" ).hasIpAddress( "127.0.0.1" )
        .antMatchers( "/permission/permission_initializing" ).hasIpAddress( "127.0.0.1" )

        .antMatchers( "/test/**" ).permitAll();

    super.configure( http );
  }
}
