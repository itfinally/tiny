package top.itfinally.builder;

import org.apache.velocity.app.Velocity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.core.FileMaker;
import top.itfinally.builder.core.TableScanner;
import top.itfinally.builder.engine.JavaFileRenderEngine;
import top.itfinally.builder.engine.RenderEngine;
import top.itfinally.builder.entity.TableMetaData;
import top.itfinally.builder.repository.po.base.BaseEntity;

import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

/**
 * timeType             时间类型 ( long, Date )
 * package              生成类的包名
 * baseEntityFullName   基类名
 * baseEntityName       基类简称
 * baseName             当前实体的基础名
 */

@SpringBootApplication( exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
} )
public class BuilderApplication implements CommandLineRunner {

    @Override
    public void run( String... args ) throws Exception {
        BuilderConfigure configure = new BuilderConfigure()
                .setForceCreation( true )
                .setTimeUnit( Date.class )
                .setEntityEndWith( "Entity" )
                .setBaseEntity( BaseEntity.class )
                .setMapUnderscoreToCamelCase( true )
                .setPackageName( "top.itfinally.builder.repository" )
                .setScanPackage( "top.itfinally.builder.repository.po" )
                .setTargetFolder( "/Users/finally/workbeach/idea/itfinally_project/repository_builder/src/main/java/top/itfinally/builder/repository" );

        configure.checking();

        Map<String, TableMetaData> metaDataMap = new TableScanner(
                configure.getScanPackage(), configure.getEntityEndWith(),
                configure.isMapUnderscoreToCamelCase()
        ).doScan();

        FileMaker fileMaker = new FileMaker( configure, metaDataMap );
        fileMaker.baseFileInitialize().generate();
    }

    public static void main( String[] args ) {
        SpringApplication application = new SpringApplication( BuilderApplication.class );
        application.setWebEnvironment( false );
        application.run( args );
    }
}
