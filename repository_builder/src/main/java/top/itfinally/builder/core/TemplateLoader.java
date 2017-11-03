package top.itfinally.builder.core;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class TemplateLoader {
    private String d;

    public static void main(  ) {
        Velocity.setProperty( "input.encoding", "utf-8" );
        Velocity.setProperty( "output.encoding", "utf-8" );
        Velocity.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
        Velocity.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() );
        Velocity.init();
    }
}
