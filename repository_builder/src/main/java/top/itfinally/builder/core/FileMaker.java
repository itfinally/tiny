package top.itfinally.builder.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.engine.BaseJavaFileRenderEngine;
import top.itfinally.builder.engine.JavaFileRenderEngine;
import top.itfinally.builder.engine.RenderEngine;
import top.itfinally.builder.engine.XmlFileRenderEngine;
import top.itfinally.builder.entity.TableMetaData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class FileMaker {
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final BuilderConfigure configure;
    private final String separator = File.separator;
    private final Map<String, TableMetaData> metaDataMap;

    private final String daoFolder;
    private final String mapperFolder;
    private final String templateFolder;

    public FileMaker( BuilderConfigure configure, Map<String, TableMetaData> metaDataMap ) {
        this.configure = configure;
        this.metaDataMap = metaDataMap;

        this.daoFolder = String.format( "%s%sdao", configure.getTargetFolder(), separator );
        this.mapperFolder = String.format( "%s%smapper", configure.getTargetFolder(), separator );
        this.templateFolder = String.format( "%s%stemplate", configure.getTargetFolder(), separator );
    }

    private void makeFolder() {
        File folder = new File( configure.getTargetFolder() );
        if ( !folder.exists() && !folder.mkdirs() ) {
            throw new RuntimeException( String.format( "Path %s not found.", folder.getPath() ) );
        }

        makeFolder( this.daoFolder );
        makeFolder( this.mapperFolder );
        makeFolder( this.templateFolder );
    }

    private void makeFolder( String path ) {
        File folder = new File( path );
        boolean folderExist = folder.exists(),
                isRealFolder = folder.isDirectory();

        if ( folderExist && isRealFolder && !configure.isForceCreation() ) {
            throw new RuntimeException( "Path %s already exist. ( set forceCreation true if you want to continue. )" );
        }

        if ( ( !folderExist || !isRealFolder ) && !folder.mkdirs() ) {
            throw new RuntimeException( String.format( "Folder %s created failure.", folder.getPath() ) );
        }
    }

    private void writeToJavaFile() {
        RenderEngine engine = new BaseJavaFileRenderEngine( configure );

        TableMetaData baseEntityMetaData = metaDataMap.get( configure.getBaseEntity().getName() );
        if ( null == baseEntityMetaData ) {
            throw new NullPointerException( "Base entity meta data not found. " +
                    "make sure base entity in your scan base package." );
        }

        String abstractDaoPath = String.format(
                "%s%s%sDao.java",
                getPath( daoFolder, baseEntityMetaData.getThisEntity().getOffset() ),
                separator, baseEntityMetaData.getThisEntity().getBaseName()
        );

        String baseMapperPath = String.format(
                "%s%s%sMapper.java",
                getPath( mapperFolder, baseEntityMetaData.getThisEntity().getOffset() ),
                separator, baseEntityMetaData.getThisEntity().getBaseName()
        );

        String baseDaoContent = engine.render( Velocity.getTemplate( "template/baseDao.txt" ), baseEntityMetaData ),
                baseMapperContent = engine.render( Velocity.getTemplate( "template/baseMapper.txt" ), baseEntityMetaData );

        writeToFile( abstractDaoPath, baseDaoContent );
        writeToFile( baseMapperPath, baseMapperContent );
    }

    private void writeToFile( String path, String content ) {
        try ( BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( path ) ) ) {
            out.write( content.getBytes() );

        } catch ( IOException e ) {
            throw new RuntimeException( String.format( "Failed to create file '%s'.", path ), e );
        }
    }

    private void writeToXmlFile() {
        RenderEngine engine = new XmlFileRenderEngine( configure );

        TableMetaData baseEntityMetaData = metaDataMap.get( configure.getBaseEntity().getName() );
        if ( null == baseEntityMetaData ) {
            throw new NullPointerException( "Base entity meta data not found. " +
                    "make sure base entity in your scan base package." );
        }

        String xmlFilePath = String.format(
                "%s%s%sMapper.xml",
                getPath( templateFolder, baseEntityMetaData.getThisEntity().getOffset() ),
                separator, baseEntityMetaData.getThisEntity().getBaseName()
        );

        String xmlContent = engine.render( Velocity.getTemplate( "template/baseXmlMapper.txt" ), baseEntityMetaData );

        writeToFile( xmlFilePath, xmlContent );
    }

    public FileMaker baseFileInitialize() {
        makeFolder();
        writeToXmlFile();
        writeToJavaFile();

        return this;
    }

    public void generate() {
        Map<String, TableMetaData> metaDataMap = this.metaDataMap.entrySet().stream()
                .filter( entry -> !entry.getKey().equals( configure.getBaseEntity().getName() ) )
                .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );

        RenderEngine engine = new JavaFileRenderEngine( configure );

        Template daoTemplate = Velocity.getTemplate( "template/dao.txt" );
        Template mapperTemplate = Velocity.getTemplate( "template/mapper.txt" );

        metaDataMap.values().forEach( meta -> {
            String mapperPath = getPath( mapperFolder, meta.getThisEntity().getOffset() );
            String daoPath = getPath( daoFolder, meta.getThisEntity().getOffset() );

            if ( StringUtils.isBlank( mapperPath ) || StringUtils.isBlank( daoPath ) ) {
                logger.error( String.format( "Failed to generate mapper and dao for %s.", meta.getThisEntity().getSimpleName() ) );
                return;
            }

            writeToFile(
                    String.format( "%s%s%sMapper.java", mapperPath, separator, meta.getThisEntity().getBaseName() ),
                    engine.render( mapperTemplate, meta )
            );

            writeToFile(
                    String.format( "%s%s%sDao.java", daoPath, separator, meta.getThisEntity().getBaseName() ),
                    engine.render( daoTemplate, meta )
            );
        } );
    }

    private String getPath( String base, String offset ) {
        String path = String.format( "%s%s%s", base, separator, offset );
        File folder = new File( path );

        if ( ( !folder.exists() || !folder.isDirectory() ) && !folder.mkdirs() ) {
            return null;
        }

        return path;
    }
}
