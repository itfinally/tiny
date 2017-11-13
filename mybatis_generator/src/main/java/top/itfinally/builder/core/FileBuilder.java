package top.itfinally.builder.core;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.engine.BaseJavaFileRenderEngine;
import top.itfinally.builder.engine.JavaFileRenderEngine;
import top.itfinally.builder.engine.RenderEngine;
import top.itfinally.builder.engine.XmlFileRenderEngine;
import top.itfinally.builder.entity.TableInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class FileBuilder {
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final BuilderConfigure configure;
    private final String separator = File.separator;
    private final Map<String, TableInfo> metaDataMap;

    static {
        Velocity.setProperty( "input.encoding", "utf-8" );
        Velocity.setProperty( "output.encoding", "utf-8" );
        Velocity.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
        Velocity.setProperty( "velocimacro.permissions.allow.inline", "true" );
        Velocity.setProperty( "velocimacro.permissions.allow.inline.local.scope", "true" );
        Velocity.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() );
        Velocity.init();
    }

    public FileBuilder( BuilderConfigure configure, Map<String, TableInfo> metaDataMap ) {
        this.configure = configure;
        this.metaDataMap = metaDataMap;
    }

    public FileBuilder initialize() {
        Template xmlTemplate = Velocity.getTemplate( "template/baseXmlMapper.txt" ),
                mapperTemplate = Velocity.getTemplate( "template/baseMapper.txt" ),
                daoTemplate = Velocity.getTemplate( "template/baseDao.txt" );

        RenderEngine javaFileEngine = new BaseJavaFileRenderEngine( configure ),
                xmlFileEngine = new XmlFileRenderEngine( configure, null );

        TableInfo tableInfo = metaDataMap.get( configure.getBaseEntity().getName() );

        if ( null == configure.getAbstractDaoCls() ) {
            writeDao( daoTemplate, tableInfo, javaFileEngine );
        }

        if ( null == configure.getBaseMapperCls() ) {
            writeMapper( mapperTemplate, tableInfo, javaFileEngine );
            writeXmlMapper( xmlTemplate, tableInfo, xmlFileEngine );
        }

        return this;
    }

    public FileBuilder build() {
        Template xmlTemplate = Velocity.getTemplate( "template/xmlMapper.txt" ),
                mapperTemplate = Velocity.getTemplate( "template/mapper.txt" ),
                daoTemplate = Velocity.getTemplate( "template/dao.txt" );

        String baseEntityName = configure.getBaseEntity().getName();
        RenderEngine javaFileEngine = new JavaFileRenderEngine( configure ),
                xmlFileEngine = new XmlFileRenderEngine( configure, metaDataMap );

        metaDataMap.values().forEach( table -> {
            if ( table.getThisEntity().getName().equals( baseEntityName ) ) {
                return;
            }

            writeXmlMapper( xmlTemplate, table, xmlFileEngine );
            writeMapper( mapperTemplate, table, javaFileEngine );
            writeDao( daoTemplate, table, javaFileEngine );
        } );

        return this;
    }

    private void writeXmlMapper( Template template, TableInfo tableInfo, RenderEngine renderEngine ) {
        String path = getFolderPath( "sqlMapper", tableInfo.getThisEntity().getOffset() );

        mkdirs( path );

        write( String.format( "%s%s%s.xml", path, separator, tableInfo.getThisEntity().getSimpleName() ),
                renderEngine.render( template, tableInfo ) );
    }

    private void writeMapper( Template template, TableInfo tableInfo, RenderEngine renderEngine ) {
        String path = getFolderPath( "mapper", tableInfo.getThisEntity().getOffset() );

        mkdirs( path );

        write( String.format( "%s%s%s.java", path, separator, tableInfo.getThisEntity().getMapperSimpleName() ),
                renderEngine.render( template, tableInfo ) );
    }

    private void writeDao( Template template, TableInfo tableInfo, RenderEngine renderEngine ) {
        String path = getFolderPath( "dao", tableInfo.getThisEntity().getOffset() );

        mkdirs( path );

        write( String.format( "%s%s%s.java", path, separator, tableInfo.getThisEntity().getDaoSimpleName() ),
                renderEngine.render( template, tableInfo ) );
    }

    private void write( String path, String content ) {
        File file = new File( path );
        if ( file.exists() && file.isFile() && !configure.isForceCreation() ) {
            return;
        }

        try ( BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( path ) ) ) {
            out.write( content.getBytes() );

        } catch ( IOException e ) {
            throw new RuntimeException( String.format( "Failed to write to file '%s'.", path ), e );
        }
    }

    private void mkdirs( String path ) {
        File folder = new File( path );
        if ( folder.isDirectory() ) {
            return;
        }

        if ( !folder.mkdirs() ) {
            throw new IllegalStateException( String.format( "Failed to create folder '%s'", folder.getPath() ) );
        }
    }

    private String getFolderPath( String name, String offset ) {
        return String.format( "%s%s%s%s%s", configure.getTargetFolder(), separator, name, separator, offset );
    }
}
