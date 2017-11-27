package top.itfinally.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan( basePackages = "top.itfinally" )
public class SecurityServerApplication {
    public static void main( String[] args ) {
        SpringApplication.run( SecurityServerApplication.class, args );
    }
}
