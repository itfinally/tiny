package top.itfinally.builder.engine;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.entity.TableInfo;
import top.itfinally.builder.util.TemplateUtils;

import java.io.StringWriter;

public class JavaFileRenderEngine implements RenderEngine {
  private final VelocityContext context = new VelocityContext();
  private final BuilderConfigure configure;

  public JavaFileRenderEngine( BuilderConfigure configure ) {
    context.put( UTIL, TemplateUtils.class );

    this.configure = configure;
  }

  @Override
  public String render( Template template, TableInfo tableInfo ) {
    StringWriter writer = new StringWriter();

    TableInfo local = new TableInfo( tableInfo );
    replaceBaseDao( configure, local );
    replaceBaseMapper( configure, local );

    context.put( TABLE, local );

    template.merge( context, writer );
    return writer.toString();
  }
}
