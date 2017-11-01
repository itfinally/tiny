package top.itfinally.builder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

import java.io.StringWriter;

@SpringBootApplication( exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
} )
public class BuilderApplication implements CommandLineRunner {

    @Override
    public void run( String... args ) throws Exception {
        Template template = Velocity.getTemplate( "template/testing.vm" );
        VelocityContext ctx = new VelocityContext();
        StringWriter writer = new StringWriter();
        ctx.put( "hello", "aewwefewf" );


        template.merge( ctx, writer );

        System.out.println(writer);
        // Do anything.
    }

    public static void main( String[] args ) {
        SpringApplication application = new SpringApplication( BuilderApplication.class );
        application.setWebEnvironment( false );
        application.run( args );
    }
}
