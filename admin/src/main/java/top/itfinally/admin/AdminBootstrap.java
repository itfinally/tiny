package top.itfinally.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication( scanBasePackages = "top.itfinally" )
public class AdminBootstrap {
  public static void main( String[] args ) {
        SpringApplication.run( AdminBootstrap.class, args );
  }
}