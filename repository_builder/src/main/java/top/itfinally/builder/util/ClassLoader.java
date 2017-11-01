package top.itfinally.builder.util;

import com.mysql.cj.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.itfinally.builder.annotation.MetaData;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.entity.ColumnMetaData;
import top.itfinally.builder.entity.TableMetaData;
import top.itfinally.core.util.CollectionUtils;
import top.itfinally.core.util.FileScanUtils;
import top.itfinally.core.util.RegExpUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassLoader {
    private final Logger logger = LoggerFactory.getLogger( getClass() );
    private

    static String scanPackage = "top.itfinally.builder.repository.po";
    static String packageName = "top.itfinally.builder.repository.po";
    static String rootPath = "classpath:top/itfinally/builder/repository";

    public static void main( String[] args ) {
        URL url = ClassLoader.class.getClassLoader().getResource( scanPackage.replaceAll( "\\.", "/" ) );

        new ClassLoader().loadEntity( FileScanUtils.doScan( url.getPath() ) );
    }

    private List<TableMetaData> loadEntity( List<String> entityPaths ) {
        RegExpUtils.RegExp clsNameMatcher = RegExpUtils.compile( "/([\\w$]+)\\.class" );

        return entityPaths.stream()

                .filter( clsNameMatcher::test )

                .map( path -> clsNameMatcher.exec( path )[ 1 ] )

                .map( className -> {
                    try {
                        return Class.forName( className );

                    } catch ( ClassNotFoundException e ) {
                        logger.error( "Failed to load class " + className );
                        return null;
                    }
                } )

                .filter( cls -> cls != null && cls.getAnnotation( Table.class ) != null )

                .map( this::entityAnalysis )

                .filter( Objects::nonNull )

                .collect( Collectors.toList() );
    }

    private TableMetaData entityAnalysis( Class<?> cls ) {
        Table table = cls.getAnnotation( Table.class );

        String tableName = table.name();
        String entityName = RegExpUtils.compile( "\\.([\\w$]+)$" ).exec( cls.getName() )[ 1 ];

        List<ColumnMetaData> columns;
        try {
            columns = columnAnalysis( Introspector.getBeanInfo( cls ).getPropertyDescriptors(), Stream.empty() )
                    .collect( Collectors.toList() );

        } catch ( IntrospectionException e ) {
            logger.error( String.format( "Failed to load class %s properties.", cls.getName() ) );
            return null;
        }

        TableMetaData meta = new TableMetaData().setEntityName( entityName )
                .setTableName( tableName ).setColumns( columns );

        Class<?> parent = cls.getSuperclass();
        if ( Object.class == parent || parent.isInterface() || Modifier.isAbstract( parent.getModifiers() ) ) {
            return meta;
        }

        return parentAnalysis( parent, meta );
    }

    private TableMetaData parentAnalysis( Class<?> cls, TableMetaData meta ) {
        MetaData flag = cls.getAnnotation( MetaData.class );
        if ( null == flag ) {
            return meta;
        }

        List<ColumnMetaData> columns;
        try {
            columns = columnAnalysis( Introspector.getBeanInfo( cls ).getPropertyDescriptors(), Stream.empty() )
                    .collect( Collectors.toList() );

        } catch ( IntrospectionException e ) {
            logger.error( String.format( "Failed to load class %s properties.", cls.getName() ) );
            return null;
        }

        Class<?> parent = cls.getSuperclass();
        if ( Object.class == parent || parent.isInterface() ) {
            return new TableMetaData( meta ).setColumns( CollectionUtils.merge( meta.getColumns(), columns ) );
        }

        return parentAnalysis( parent, meta );
    }

    private Stream<ColumnMetaData> columnAnalysis( PropertyDescriptor[] descriptors, Stream<ColumnMetaData> stream ) {
        if ( descriptors.length <= 0 ) {
            return stream;
        }

        PropertyDescriptor descriptor = descriptors[ 0 ];
//        descriptor.get

        return columnAnalysis(
                Arrays.stream( descriptors ).skip( 1 ).toArray( PropertyDescriptor[]::new ),
                Stream.concat( stream, Stream.of() )
        );
    }
}
