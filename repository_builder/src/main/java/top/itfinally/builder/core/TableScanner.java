package top.itfinally.builder.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ExtendedBeanInfoFactory;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.MetaData;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.entity.ColumnMetaData;
import top.itfinally.builder.entity.TableMetaData;
import top.itfinally.core.util.FileScanUtils;
import top.itfinally.core.util.RegExpUtils;

import javax.annotation.concurrent.NotThreadSafe;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NotThreadSafe
public class TableScanner {
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final String scanPackage;
    private final String entityEndWith;
    private final boolean mapUnderscoreToCamelCase;

    private final ExtendedBeanInfoFactory beanInfoFactory = new ExtendedBeanInfoFactory();
    private final RegExpUtils.RegExp entityNameMatcher = RegExpUtils.compile( "\\.([\\w$]+)$" );
    private final RegExpUtils.RegExp clsNameMatcher = RegExpUtils.compile( "((\\w+/)*([\\w$]+))\\.class" );

    private Map<String, TableMetaData> resultMaps = new HashMap<>();

    public TableScanner( String scanPackage ) {
        this( scanPackage, "Entity", true );
    }

    public TableScanner( String scanPackage, String entityEndWith, boolean mapUnderscoreToCamelCase ) {
        this.scanPackage = scanPackage;
        this.entityEndWith = entityEndWith;
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public Map<String, TableMetaData> doScan() throws IllegalStateException, NullPointerException {
        if ( !resultMaps.isEmpty() ) {
            throw new IllegalStateException( "Table scanner is not reusable." );
        }

        List<String> entityPaths = FileScanUtils.doScan( getAbsolutePath() );
        String scanPackagePath = scanPackage.replaceAll( "\\.", File.separator );

        entityPaths.stream()

                .filter( path -> path.contains( scanPackagePath ) )

                .map( path -> clsNameMatcher.exec( path.split( scanPackagePath )[ 1 ] )[ 1 ] )

                .map( className -> {
                    try {
                        return Class.forName( String.format( "%s.%s", scanPackage, className.replaceAll( File.separator, "." ) ) );

                    } catch ( ClassNotFoundException e ) {
                        logger.error( "Failed to load class " + className );
                        return null;
                    }
                } )

                .filter( cls -> cls != null && cls.getAnnotation( Table.class ) != null )

                .forEach( cls -> {
                    entityAnalysis( cls );
                    parentAnalysis( cls.getSuperclass() );
                } );

        return resultMaps;
    }

    private void entityAnalysis( Class<?> cls ) {
        Table table = cls.getAnnotation( Table.class );

        if ( table != null && !resultMaps.containsKey( cls.getName() ) ) {
            resultMaps.put( cls.getName(), tableAnalysis( cls ) );
        }
    }

    private Object parentAnalysis( Class<?> cls ) {
        if ( Object.class == cls || cls.isInterface() || null == cls.getAnnotation( MetaData.class ) ) {
            return null;
        }

        // That mean the rest of the extend chain already scan
        // if this class is exist.
        if ( resultMaps.containsKey( cls.getName() ) ) {
            return null;
        }

        resultMaps.put( cls.getName(), tableAnalysis( cls ) );
        return parentAnalysis( cls.getSuperclass() );
    }

    private TableMetaData tableAnalysis( Class<?> cls ) {
        Table table = cls.getAnnotation( Table.class );
        MetaData metaData = cls.getAnnotation( MetaData.class );

        String baseName = extractEntityName( cls.getName() );
        String tableName = null == table ? null : table.name();
        String entityName = entityNameMatcher.exec( cls.getName() )[ 1 ];

        List<ColumnMetaData> columns = columnScanner( cls );
        if ( null == columns ) {
            return null;
        }

        TableMetaData meta = new TableMetaData()
                .setThisCls( cls )
                .setColumns( columns )
                .setBaseName( baseName )
                .setEntityName( entityName )

                .setTable( table != null )
                .setMeta( metaData != null )

                // Use entity name as table name if not set.
                .setTableName( StringUtils.isBlank( tableName ) ? entityName : tableName );

        Class<?> extend = cls.getSuperclass();
        if ( extend.getAnnotation( MetaData.class ) != null ) {
            meta.setExtendCls( extend );
        }

        return meta;
    }

    private String extractEntityName( String clsName ) {
        String entityName = entityNameMatcher.exec( clsName )[ 1 ];
        String lowerEntityName = entityName.toLowerCase();

        if ( lowerEntityName.endsWith( entityEndWith ) ) {
            return entityName.substring( 0, entityName.length() - entityEndWith.length() );
        }

        return entityName;
    }

    private List<ColumnMetaData> columnScanner( Class<?> cls ) {
        try {
            List<Field> fields = Arrays.asList( cls.getDeclaredFields() );

            Map<String, PropertyDescriptor> properties = Arrays.stream( beanInfoFactory.getBeanInfo( cls ).getPropertyDescriptors() )
                    .collect( Collectors.toMap( FeatureDescriptor::getName, descriptor -> descriptor ) );

            return columnAnalysis( fields, properties, Stream.empty() ).collect( Collectors.toList() );

        } catch ( IntrospectionException e ) {
            logger.error( String.format( "Failed to load class %s properties.", cls.getName() ) );
            return null;
        }
    }

    private Stream<ColumnMetaData> columnAnalysis(
            List<Field> fields, Map<String, PropertyDescriptor> descriptors, Stream<ColumnMetaData> stream
    ) {
        if ( fields.isEmpty() ) {
            return stream.filter( Objects::nonNull );
        }

        Field field = fields.get( 0 );
        PropertyDescriptor descriptor = descriptors.get( field.getName() );

        if ( null == descriptor ) {
            throw new NullPointerException( String.format(
                    "Not match field '%s' in class %s", field.getName(), field.getDeclaringClass().getName()
            ) );
        }

        Column fromWrite = descriptor.getWriteMethod().getAnnotation( Column.class );
        Column fromRead = descriptor.getReadMethod().getAnnotation( Column.class );
        Column fromField = field.getAnnotation( Column.class );

        ColumnMetaData columnInfo;
        if ( fromField != null ) {
            columnInfo = createColumnBean( field, fromField );

        } else if ( fromRead != null ) {
            columnInfo = createColumnBean( field, fromRead );

        } else if ( fromWrite != null ) {
            columnInfo = createColumnBean( field, fromWrite );

        } else {
            columnInfo = null;
        }

        return columnAnalysis(
                fields.stream().skip( 1 ).collect( Collectors.toList() ),
                descriptors, Stream.concat( stream, Stream.of( columnInfo ) )
        );
    }

    private ColumnMetaData createColumnBean( Field field, Column column ) {
        String propertyName = column.property();
        String columnName = column.column();

        return new ColumnMetaData()
                .setJavaType( field.getType() )
                .setProperty( StringUtils.isBlank( propertyName ) ? field.getName() : propertyName )
                .setColumn( StringUtils.isBlank( columnName ) ? caseToUnderscore( field.getName() ) : column.column() );
    }

    private String caseToUnderscore( String camel ) {
        return !mapUnderscoreToCamelCase ? camel : camel
                .replaceAll( "[A-Z]+", "_$0" )
                .toLowerCase()
                .replaceFirst( "_", "" );
    }

    private String getAbsolutePath() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                this.scanPackage.replaceAll( "\\.", "/" )
        );

        if ( null == url ) {
            throw new NullPointerException( String.format( "Package '%s' is not available.", scanPackage ) );
        }

        return url.getPath();
    }
}
