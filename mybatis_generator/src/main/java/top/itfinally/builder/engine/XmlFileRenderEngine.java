package top.itfinally.builder.engine;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.entity.ColumnInfo;
import top.itfinally.builder.entity.TableInfo;
import top.itfinally.builder.util.TemplateUtils;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class XmlFileRenderEngine implements RenderEngine {
  private static final String ALL_COLUMNS = "allColumns";

  private final VelocityContext context = new VelocityContext();
  private final Map<String, TableInfo> metaDataMap;
  private final BuilderConfigure configure;

  public XmlFileRenderEngine( BuilderConfigure configure, Map<String, TableInfo> metaDataMap ) {
    context.put( UTIL, TemplateUtils.class );

    this.metaDataMap = metaDataMap;
    this.configure = configure;
  }

  @Override
  public String render( Template template, TableInfo tableInfo ) {
    StringWriter writer = new StringWriter();

    TableInfo local = new TableInfo( tableInfo );
    replaceBaseMapper( configure, local );

    context.put( TABLE, local );
    context.put( ALL_COLUMNS, getTableColumns( local, Stream.empty() ) );

    template.merge( context, writer );
    return writer.toString();
  }

  private List<ColumnInfo> getTableColumns( TableInfo tableInfo, Stream<ColumnInfo> columnStream ) {
    if ( null == tableInfo.getExtendEntity() ) {
      return Stream.concat( columnStream, tableInfo.getColumnInfoList().stream() ).collect( toList() );
    }

    String extendEntityName = tableInfo.getExtendEntity().getName();
    if ( !metaDataMap.containsKey( extendEntityName ) ) {
      throw new IllegalStateException( String.format( "require entity '%s'", extendEntityName ) );
    }

    return getTableColumns(
        metaDataMap.get( extendEntityName ),
        Stream.concat( columnStream, tableInfo.getColumnInfoList().stream() )
    );
  }
}
