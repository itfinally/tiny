package top.itfinally.builder.bootstrap;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class VelocityStarter implements ApplicationListener<ContextRefreshedEvent> {
    private AtomicBoolean isStarted = new AtomicBoolean( false );
    private VelocityConfigure velocityConfigure;

    @Autowired( required = false )
    public VelocityStarter setVelocityConfigure( VelocityConfigure velocityConfigure ) {
        this.velocityConfigure = velocityConfigure;
        return this;
    }

    @Override
    public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent ) {
        if ( isStarted.compareAndSet( false, true ) ) {

            if( velocityConfigure != null ) {
                velocityConfigure.init();
                return;
            }

            Velocity.setProperty( "input.encoding", "utf-8" );
            Velocity.setProperty( "output.encoding", "utf-8" );
            Velocity.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
            Velocity.setProperty( "velocimacro.permissions.allow.inline", "true" );
            Velocity.setProperty( "velocimacro.permissions.allow.inline.local.scope", "true" );
            Velocity.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() );
            Velocity.init();
        }
    }
}
