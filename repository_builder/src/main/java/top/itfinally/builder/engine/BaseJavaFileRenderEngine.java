package top.itfinally.builder.engine;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.entity.TableMetaData;

import java.io.StringWriter;

public class BaseJavaFileRenderEngine implements RenderEngine {
    private static final String TIME_UNIT = "timeUnit";

    private final VelocityContext context = new VelocityContext();

    public BaseJavaFileRenderEngine( BuilderConfigure configure ) {
        context.put( TIME_UNIT, configure.getTimeUnit().getSimpleName() );
        context.put( PACKAGE, configure.getPackageName() );
        context.put( UTIL, RenderEngine.class );
    }

    @Override
    public String render( Template template, TableMetaData tableMetaData ) {
        context.put( TABLE, tableMetaData );

        StringWriter writer = new StringWriter();
        template.merge( context, writer );
        return writer.toString();
    }
}
