package top.itfinally.builder.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ExtendedBeanInfoFactory;
import top.itfinally.builder.annotation.MetaData;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.entity.ColumnMetaData;
import top.itfinally.builder.entity.EntityMetaData;
import top.itfinally.builder.entity.TableMetaData;
import top.itfinally.core.util.CollectionUtils;
import top.itfinally.core.util.FileScanUtils;

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
    private final ColumnScanner columnScanner;

    private final ExtendedBeanInfoFactory beanInfoFactory = new ExtendedBeanInfoFactory();
    private final Map<Class<?>, EntityMetaData> entityMetaDataCache = new HashMap<>();

    private Map<String, TableMetaData> resultMaps = new HashMap<>();

    public TableScanner( String scanPackage ) {
        this( scanPackage, "Entity", true );
    }

    public TableScanner( String scanPackage, String entityEndWith, boolean mapUnderscoreToCamelCase ) {
        this.scanPackage = scanPackage;
        this.entityEndWith = entityEndWith;
        this.columnScanner = new ColumnScanner( entityEndWith, mapUnderscoreToCamelCase );
    }

    public Map<String, TableMetaData> doScan() throws IllegalStateException, NullPointerException {
        if ( !resultMaps.isEmpty() ) {
            throw new IllegalStateException( "Table scanner is not reusable." );
        }

        List<String> entityPaths = FileScanUtils.doScan( getAbsolutePath() );
        String scanPackagePath = scanPackage.replaceAll( "\\.", File.separator );

        entityPaths.stream()

                .filter( path -> path.contains( scanPackagePath ) )

                .map( path -> path.substring( path.indexOf( scanPackagePath ) ).replace( ".class", "" ) )

                .map( className -> {
                    try {
                        return Class.forName( className.replaceAll( File.separator, "." ) );

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

        return rebuildId( resultMaps );
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

        String tableName = ( null == table ? null : table.name() );
        List<ColumnMetaData> columns = columnAnalysis( cls );

        if ( null == columns ) {
            return null;
        }

        TableMetaData meta = new TableMetaData()

                .setColumns( columns )

                .setTable( table != null )

                .setMeta( metaData != null )

                .setThisEntity( createEntityMetaData( cls ) )

                // Use entity name as table name if not set.
                .setTableName( StringUtils.isBlank( tableName ) ? cls.getSimpleName() : tableName );

        Class<?> extend = cls.getSuperclass();
        if ( extend.getAnnotation( MetaData.class ) != null ) {
            meta.setExtendEntity( createEntityMetaData( extend ) );
        }

        return meta;
    }

    private String extractEntityName( String clsSimpleName ) {
        String lowerEntityName = clsSimpleName.toLowerCase();

        if ( lowerEntityName.endsWith( entityEndWith.toLowerCase() ) ) {
            return clsSimpleName.substring( 0, clsSimpleName.length() - entityEndWith.length() );
        }

        return clsSimpleName;
    }

    private List<ColumnMetaData> columnAnalysis( Class<?> cls ) {
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
        PropertyDescriptor descriptor;

        if ( !field.getName().startsWith( "is" ) ) {
            descriptor = descriptors.get( field.getName() );

        } else {
            char[] nameChar = field.getName().replaceFirst( "^is", "" ).toCharArray();

            // switch A-Z to a-z
            if( nameChar[ 0 ] < 97 ) {
                nameChar[ 0 ] += 32;
            }

            descriptor = descriptors.get( new String( nameChar ) );

            if ( null == descriptor ) {
                throw new NullPointerException( String.format(
                        "Not match descriptor for field '%s' in class '%s'.",
                        field.getName(), field.getDeclaringClass()
                ) );
            }
        }

        return columnAnalysis(
                fields.stream().skip( 1 ).collect( Collectors.toList() ),
                descriptors, Stream.concat( stream, Stream.of( columnScanner.doScan( field, descriptor ) ) )
        );
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

    private EntityMetaData createEntityMetaData( Class<?> cls ) {
        if ( entityMetaDataCache.containsKey( cls ) ) {
            return entityMetaDataCache.get( cls );
        }

        String offset = cls.getName()
                .replace( scanPackage, "" )
                .replace( cls.getSimpleName(), "" )
                .replaceAll( "(^\\.|\\.$)", "" );

        EntityMetaData entityMetaData = new EntityMetaData()
                .setThisCls( cls )
                .setOffset( offset )
                .setName( cls.getName() )
                .setSimpleName( cls.getSimpleName() )
                .setPath( cls.getResource( "" ).getPath() )
                .setBaseName( extractEntityName( cls.getSimpleName() ) );

        entityMetaDataCache.put( cls, entityMetaData );
        return entityMetaData;
    }

    private Map<String, TableMetaData> rebuildId( Map<String, TableMetaData> metaDataMap ) {
        return metaDataMap.entrySet().stream().collect( Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    TableMetaData metaData = entry.getValue();
                    List<ColumnMetaData> columns = metaData.getColumns();

                    if ( null == metaData.getExtendEntity() ) {
                        return new TableMetaData( metaData ).setIdColumn(
                                checkingAndGetId( entry.getValue().getThisEntity().getThisCls(), columns )
                        );
                    }

                    TableMetaData parentMetaData = metaDataMap.get( metaData.getExtendEntity().getName() );
                    List<ColumnMetaData> allColumns = CollectionUtils.merge( metaData.getColumns(), parentMetaData.getColumns() );

                    return new TableMetaData( metaData ).setIdColumn(
                            checkingAndGetId( entry.getValue().getThisEntity().getThisCls(), allColumns )
                    );
                }
        ) );
    }

    private ColumnMetaData checkingAndGetId( Class<?> cls, List<ColumnMetaData> columns ) {
        List<ColumnMetaData> ids = columns.stream()
                .filter( ColumnMetaData::isId )
                .collect( Collectors.toList() );

        if ( ids.isEmpty() ) {
            throw new IllegalStateException( "Table require id." );
        }

        if ( ids.size() != 1 ) {
            throw new IllegalStateException( String.format( "Duplicate id in entity %s.", cls.getSimpleName() ) );
        }

        return ids.get( 0 );
    }
}
