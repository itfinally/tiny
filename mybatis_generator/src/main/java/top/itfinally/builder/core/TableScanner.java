package top.itfinally.builder.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ExtendedBeanInfoFactory;
import top.itfinally.builder.annotation.MetaData;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.entity.ColumnInfo;
import top.itfinally.builder.entity.EntityInfo;
import top.itfinally.builder.entity.TableInfo;
import top.itfinally.builder.util.TemplateUtils;
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

    private final BuilderConfigure configure;
    private final ColumnScanner columnScanner;

    private final ExtendedBeanInfoFactory beanInfoFactory = new ExtendedBeanInfoFactory();
    private final Map<Class<?>, EntityInfo> entityMetaDataCache = new HashMap<>();

    private Map<String, TableInfo> resultMaps = new HashMap<>();

    public TableScanner( BuilderConfigure configure ) {
        this.configure = configure;
        this.columnScanner = new ColumnScanner( configure );
    }

    public Map<String, TableInfo> doScan() throws IllegalStateException, NullPointerException {
        if ( !resultMaps.isEmpty() ) {
            throw new IllegalStateException( "Table scanner is not reusable." );
        }

        List<String> entityPaths = FileScanUtils.doScan( getAbsolutePath() );
        String scanPackagePath = configure.getScanPackage().replaceAll( "\\.", File.separator );

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

        return resultMaps;
    }

    private void entityAnalysis( Class<?> thisCls ) {
        Table table = thisCls.getAnnotation( Table.class );

        if ( table != null && !resultMaps.containsKey( thisCls.getName() ) ) {
            resultMaps.put( thisCls.getName(), tableAnalysis( thisCls ) );
        }
    }

    private Object parentAnalysis( Class<?> parentCls ) {
        if ( Object.class == parentCls || parentCls.isInterface() || null == parentCls.getAnnotation( MetaData.class ) ) {
            return null;
        }

        // That mean the rest of the extend chain already scan
        // if this class is exist.
        if ( resultMaps.containsKey( parentCls.getName() ) ) {
            return null;
        }

        resultMaps.put( parentCls.getName(), tableAnalysis( parentCls ) );
        return parentAnalysis( parentCls.getSuperclass() );
    }

    private TableInfo tableAnalysis( Class<?> cls ) {
        Table table = cls.getAnnotation( Table.class );
        MetaData metaData = cls.getAnnotation( MetaData.class );

        String tableName = ( null == table ? null : table.name() );
        ColumnInfo[] idColumnPoint = new ColumnInfo[]{ null };

        TableInfo meta = new TableInfo()

                // Use entity name as table name if not set.
                .setTableName( StringUtils.isBlank( tableName ) ? cls.getSimpleName() : tableName )

                .setThisEntity( createEntityMetaData( cls ) )

                .setIdColumn( idColumnPoint[ 0 ] )

                .setColumnInfoList( columnAnalysis( cls, idColumnPoint ) )

                .setMeta( metaData != null )

                .setTable( table != null );

        Class<?> extend = cls.getSuperclass();
        if ( extend.getAnnotation( MetaData.class ) != null ) {
            meta.setExtendEntity( createEntityMetaData( extend ) );
        }

        return meta;
    }

    private List<ColumnInfo> columnAnalysis( Class<?> cls, ColumnInfo[] idPoint ) {
        try {
            List<Field> fields = Arrays.asList( cls.getDeclaredFields() );

            Map<String, PropertyDescriptor> properties = Arrays.stream( beanInfoFactory.getBeanInfo( cls ).getPropertyDescriptors() )
                    .collect( Collectors.toMap( FeatureDescriptor::getName, descriptor -> descriptor ) );

            return columnAnalysis( fields, properties, Stream.empty(), idPoint )
                    .filter( Objects::nonNull ).collect( Collectors.toList() );

        } catch ( IntrospectionException e ) {
            logger.error( String.format( "Failed to load class %s properties.", cls.getName() ) );
            return null;
        }
    }

    private Stream<ColumnInfo> columnAnalysis(
            List<Field> fields, Map<String, PropertyDescriptor> descriptors,
            Stream<ColumnInfo> stream, ColumnInfo[] idColumnPoint
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
            if ( nameChar[ 0 ] < 97 ) {
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

        Object[] result = columnScanner.doScan( field, descriptor );
        ColumnInfo columnInfo = ( null == result ? null : ( ColumnInfo ) result[ 1 ] );

        if ( result != null && ( boolean ) result[ 0 ] ) {
            if ( idColumnPoint[ 0 ] != null ) {
                throw new IllegalStateException( String.format(
                        "Duplicate id in entity '%s'.", field.getDeclaringClass().getName()
                ) );
            }

            idColumnPoint[ 0 ] = columnInfo;
        }

        return columnAnalysis(
                fields.stream().skip( 1 ).collect( Collectors.toList() ), descriptors,
                Stream.concat( stream, Stream.of( columnInfo ) ), idColumnPoint
        );
    }

    private String getAbsolutePath() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                configure.getScanPackage().replaceAll( "\\.", "/" )
        );

        if ( null == url ) {
            throw new NullPointerException( String.format( "Package '%s' is not available.", configure.getScanPackage() ) );
        }

        return url.getPath();
    }

    private EntityInfo createEntityMetaData( Class<?> cls ) {
        if ( entityMetaDataCache.containsKey( cls ) ) {
            return entityMetaDataCache.get( cls );
        }

        String offset = TemplateUtils.getOffset( configure.getScanPackage(), cls ),
                baseName = TemplateUtils.getBaseName( cls.getSimpleName(), configure.getEntityEndWith() ),
                daoName = TemplateUtils.getDaoName( configure.getPackageName(), offset, baseName ),
                mapperName = TemplateUtils.getMapperName( configure.getPackageName(), offset, baseName );

        EntityInfo entityInfo = new EntityInfo()
                .setSimpleName( cls.getSimpleName() )
                .setMapperName( mapperName )
                .setName( cls.getName() )
                .setDaoName( daoName )
                .setOffset( offset );

        entityMetaDataCache.put( cls, entityInfo );
        return entityInfo;
    }
}
