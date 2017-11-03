package top.itfinally.builder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import top.itfinally.builder.constant.TemplateKeys;
import top.itfinally.builder.core.TableScanner;
import top.itfinally.builder.entity.TableMetaData;
import top.itfinally.builder.repository.po.AEntity;

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

    static final String packageName = "top.itfinally.security.repository";

    @Override
    public void run( String... args ) throws Exception {
        Template template = Velocity.getTemplate( "template/abstractDao.txt" );
        VelocityContext ctx = new VelocityContext();
        StringWriter writer = new StringWriter();

        Map<String, TableMetaData> result = new TableScanner( AEntity.class.getPackage().getName() ).doScan();
        TableMetaData test = result.get( "top.itfinally.builder.repository.po.ttt.aaa.EEntity" );

        ctx.put( TemplateKeys.TIME_TYPE, "Date" );
        ctx.put( TemplateKeys.PACKAGE, packageName );
        ctx.put( "table", test );


//        ctx.put( "baseEntityBase", "BaseEntity" );
//        ctx.put( "baseEntityClassName", "org.apache.velocity.app.Velocity" );


        template.merge( ctx, writer );

        System.out.println(writer);
        // Do anything.
    }

    public static void main( String[] args ) {
        SpringApplication application = new SpringApplication( BuilderApplication.class );
        application.setWebEnvironment( false );
        application.run( args );
    }
}
