package top.itfinally.admin.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CorsConfigure extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings( CorsRegistry registry ) {
        registry.addMapping( "/**" ).allowedHeaders( "*" )
                .allowedMethods( "*" ).allowedOrigins( "*" );
    }
}
