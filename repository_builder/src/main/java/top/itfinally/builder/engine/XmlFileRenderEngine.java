package top.itfinally.builder.engine;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.entity.TableMetaData;

import java.io.StringWriter;

public class XmlFileRenderEngine implements RenderEngine {
    private final VelocityContext context = new VelocityContext();

    public XmlFileRenderEngine( BuilderConfigure configure ) {
        context.put( UTIL, RenderEngine.class );
        context.put( PACKAGE, configure.getPackageName() );
    }

    @Override
    public String render( Template template, TableMetaData tableMetaData ) {
        StringWriter writer = new StringWriter();

        context.put( TABLE, tableMetaData );

        template.merge( context, writer );
        return writer.toString();
    }
}
