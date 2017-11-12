package top.itfinally.builder.engine;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.entity.TableInfo;
import top.itfinally.builder.util.TemplateUtils;

import java.io.StringWriter;

public class BaseJavaFileRenderEngine implements RenderEngine {
    private static final String TIME_UNIT = "timeUnit";

    private final VelocityContext context = new VelocityContext();

    public BaseJavaFileRenderEngine( BuilderConfigure configure ) {
        context.put( TIME_UNIT, configure.getTimeUnit().getSimpleName() );
        context.put( UTIL, TemplateUtils.class );
    }

    @Override
    public String render( Template template, TableInfo tableInfo ) {
        context.put( TABLE, tableInfo );

        StringWriter writer = new StringWriter();
        template.merge( context, writer );
        return writer.toString();
    }
}
