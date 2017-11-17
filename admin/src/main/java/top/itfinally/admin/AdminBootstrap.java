package top.itfinally.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.itfinally.security.SecurityServerApplication;

@SpringBootApplication( scanBasePackages = "top.itfinally" )
@MapperScan( basePackages = "top.itfinally" )
public class AdminBootstrap {
    public static void main( String[] args ) {
        SpringApplication.run( SecurityServerApplication.class, args );
    }
}
