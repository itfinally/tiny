package top.itfinally.builder.core;

import com.google.common.reflect.Reflection;
import org.apache.commons.lang3.StringUtils;
import top.itfinally.builder.annotation.Association;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Id;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.entity.ColumnMetaData;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class ColumnScanner {
    private final String entityEndWith;
    private final boolean mapUnderscoreToCamelCase;

    ColumnScanner( String entityEndWith, boolean mapUnderscoreToCamelCase ) {
        this.entityEndWith = entityEndWith;
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    ColumnMetaData doScan( Field field, PropertyDescriptor descriptor ) {
        Method writeMethod = descriptor.getWriteMethod();
        Method readMethod = descriptor.getReadMethod();

        boolean isId = writeMethod.getAnnotation( Id.class ) != null ||
                readMethod.getAnnotation( Id.class ) != null ||
                field.getAnnotation( Id.class ) != null;

        if ( writeMethod.getAnnotation( Association.class ) != null ||
                readMethod.getAnnotation( Association.class ) != null ||
                field.getAnnotation( Association.class ) != null ) {

            return association( field, descriptor ).setId( isId );
        }

        if ( writeMethod.getAnnotation( Column.class ) != null ||
                readMethod.getAnnotation( Column.class ) != null ||
                field.getAnnotation( Column.class ) != null ) {

            return column( field, descriptor ).setId( isId );
        }

        return null;
    }

    private ColumnMetaData column( Field field, PropertyDescriptor descriptor ) {
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

        return columnInfo;
    }

    private ColumnMetaData collection( Field field, PropertyDescriptor descriptor ) {
        return null;
    }

    private ColumnMetaData association( Field field, PropertyDescriptor descriptor ) {
        Association fromWrite = descriptor.getWriteMethod().getAnnotation( Association.class );
        Association fromRead = descriptor.getReadMethod().getAnnotation( Association.class );
        Association fromField = field.getAnnotation( Association.class );

        ColumnMetaData columnInfo;
        if ( fromField != null ) {
            columnInfo = createAssociationBean( field, fromField );

        } else if ( fromRead != null ) {
            columnInfo = createAssociationBean( field, fromRead );

        } else if ( fromWrite != null ) {
            columnInfo = createAssociationBean( field, fromWrite );

        } else {
            columnInfo = null;
        }

        return columnInfo;
    }

    private ColumnMetaData createColumnBean( Field field, Column column ) {
        String propertyName = column.property();
        String columnName = column.column();

        return new ColumnMetaData()
                .setJavaType( field.getType() )
                .setProperty( StringUtils.isBlank( propertyName ) ? field.getName() : propertyName )
                .setColumn( StringUtils.isBlank( columnName ) ? caseToUnderscore( field.getName() ) : column.column() );
    }

    private ColumnMetaData createAssociationBean( Field field, Association column ) {
        String propertyName = column.property();
        String columnName = column.column();
        Class<?> join = column.join();

        if ( null == join.getAnnotation( Table.class ) ) {
            throw new IllegalArgumentException( "Joined target must be table." );
        }

        return new ColumnMetaData()
                .setJoinKey( join.getName() )
                .setJoinType( "association" )
                .setJavaType( field.getType() )
                .setProperty( StringUtils.isBlank( propertyName ) ? field.getName() : propertyName )
                .setColumn( StringUtils.isBlank( columnName ) ? caseToUnderscore( field.getName() ) : column.column() );
    }

    private String caseToUnderscore( String camel ) {
        return !mapUnderscoreToCamelCase ? camel : camel
                .replaceAll( "[A-Z]+", "_$0" )
                .toLowerCase()
                .replaceAll( "^_", "" );
    }

    private String extractEntityName( String clsSimpleName ) {
        String lowerEntityName = clsSimpleName.toLowerCase();

        if ( lowerEntityName.endsWith( entityEndWith.toLowerCase() ) ) {
            return clsSimpleName.substring( 0, clsSimpleName.length() - entityEndWith.length() );
        }

        return clsSimpleName;
    }
}