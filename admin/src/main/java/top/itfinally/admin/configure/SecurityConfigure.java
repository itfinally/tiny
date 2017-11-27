package top.itfinally.admin.configure;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import top.itfinally.security.configure.BaseSecurityConfigure;

@EnableWebSecurity
@Order( SecurityProperties.ACCESS_OVERRIDE_ORDER )
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfigure extends BaseSecurityConfigure {
    @Override
    protected void configure( HttpSecurity http ) throws Exception {
        super.configure( http );

        http.anonymous()
        .and().
        authorizeRequests().anyRequest().permitAll();
    }
}
