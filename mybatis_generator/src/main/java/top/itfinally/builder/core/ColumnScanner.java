package top.itfinally.builder.core;

import org.apache.commons.lang3.StringUtils;
import top.itfinally.builder.annotation.Association;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Id;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.entity.ColumnInfo;
import top.itfinally.builder.util.TemplateUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class ColumnScanner {
    private final BuilderConfigure configure;

    ColumnScanner( BuilderConfigure configure ) {
        this.configure = configure;
    }

    // return parameter 1: id flag
    // return parameter 2: columns
    Object[] doScan( Field field, PropertyDescriptor descriptor ) {
        Method writeMethod = descriptor.getWriteMethod();
        Method readMethod = descriptor.getReadMethod();

        boolean isId = writeMethod.getAnnotation( Id.class ) != null ||
                readMethod.getAnnotation( Id.class ) != null ||
                field.getAnnotation( Id.class ) != null;

        if ( writeMethod.getAnnotation( Association.class ) != null ||
                readMethod.getAnnotation( Association.class ) != null ||
                field.getAnnotation( Association.class ) != null ) {

            return new Object[]{ isId, association( field, descriptor ) };
        }

        if ( writeMethod.getAnnotation( Column.class ) != null ||
                readMethod.getAnnotation( Column.class ) != null ||
                field.getAnnotation( Column.class ) != null ) {

            return new Object[]{ isId, column( field, descriptor ) };
        }

        return null;
    }

    private ColumnInfo column( Field field, PropertyDescriptor descriptor ) {
        Column fromWrite = descriptor.getWriteMethod().getAnnotation( Column.class );
        Column fromRead = descriptor.getReadMethod().getAnnotation( Column.class );
        Column fromField = field.getAnnotation( Column.class );

        ColumnInfo columnInfo;
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

    private ColumnInfo collection( Field field, PropertyDescriptor descriptor ) {
        return null;
    }

    private ColumnInfo association( Field field, PropertyDescriptor descriptor ) {
        Association fromWrite = descriptor.getWriteMethod().getAnnotation( Association.class );
        Association fromRead = descriptor.getReadMethod().getAnnotation( Association.class );
        Association fromField = field.getAnnotation( Association.class );

        ColumnInfo columnInfo;
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

    private ColumnInfo createColumnBean( Field field, Column column ) {
        String propertyName = column.property();
        String columnName = column.column();

        return new ColumnInfo()
                .setColumn( StringUtils.isBlank( columnName ) ? caseToUnderscore( field.getName() ) : column.column() )
                .setProperty( StringUtils.isBlank( propertyName ) ? field.getName() : propertyName )
                .setJavaType( field.getType().getName() );
    }

    private ColumnInfo createAssociationBean( Field field, Association column ) {
        String propertyName = column.property();
        String columnName = column.column();
        Class<?> join = column.join();

        if ( null == join.getAnnotation( Table.class ) ) {
            throw new IllegalArgumentException( "Joined target must be table." );
        }

        String mapperName = TemplateUtils.getMapperName(
                configure.getPackageName(),
                TemplateUtils.getOffset( configure.getScanPackage(), join ),
                TemplateUtils.getBaseName( join.getSimpleName(), configure.getEntityEndWith() )
        );

        return new ColumnInfo()
                .setColumn( StringUtils.isBlank( columnName ) ? caseToUnderscore( field.getName() ) : column.column() )
                .setProperty( StringUtils.isBlank( propertyName ) ? field.getName() : propertyName )
                .setJavaType( field.getType().getName() )
                .setJoinEntityName( join.getName() )
                .setJoinMapperName( mapperName )
                .setJoinType( "association" );
    }

    private String caseToUnderscore( String camel ) {
        return camel.replaceAll( "[A-Z]+", "_$0" )
                .toLowerCase().replaceAll( "^_", "" );
    }
}