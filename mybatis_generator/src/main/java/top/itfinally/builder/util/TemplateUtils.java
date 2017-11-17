package top.itfinally.builder.util;

import org.apache.commons.lang3.StringUtils;
import top.itfinally.builder.entity.ColumnInfo;

import java.util.List;

public class TemplateUtils {
    private TemplateUtils() {
    }

    public static String getMapperName( String packageName, String offset, String baseName ) {
        if ( StringUtils.isNotBlank( offset ) ) {
            return String.format( "%s.mapper%s.%sMapper", packageName, "." + offset, baseName );

        } else {
            return String.format( "%s.mapper%s.%sMapper", packageName, offset, baseName );
        }
    }

    public static String getDaoName( String packageName, String offset, String baseName ) {
        if ( StringUtils.isNotBlank( offset ) ) {
            return String.format( "%s.dao%s.%sDao", packageName, "." + offset, baseName );

        } else {
            return String.format( "%s.dao%s.%sDao", packageName, offset, baseName );
        }
    }

    public static String getOffset( String packageName, Class<?> cls ) {
        return cls.getName()
                .replace( packageName, "" )
                .replace( cls.getSimpleName(), "" )
                .replaceAll( "(^\\.|\\.$)", "" );
    }

    public static String getBaseName( String clsSimpleName, String entityEndWith ) {
        String lowerEntityName = clsSimpleName.toLowerCase();

        if ( lowerEntityName.endsWith( entityEndWith.toLowerCase() ) ) {
            return clsSimpleName.substring( 0, clsSimpleName.length() - entityEndWith.length() );
        }

        return clsSimpleName;
    }

    public static String lowerFirstChar( String val ) {
        char[] valChar = val.toCharArray();

        if ( valChar[ 0 ] < 97 ) {
            valChar[ 0 ] += 32;
        }

        return new String( valChar );
    }

    public static String generateInsertSqlField( List<ColumnInfo> columns ) {
        int[] index = new int[]{ 0 };
        String tab = "                ";
        StringBuilder builder = new StringBuilder();

        columns.forEach( column -> {
            builder.append( column.getColumn() );

            if ( index[ 0 ] + 1 < columns.size() ) {
                builder.append( ", " );
            }

            if ( index[ 0 ] != 0 && index[ 0 ] % 5 == 0 ) {
                builder.append( "\n" ).append( tab );
            }

            index[ 0 ] += 1;
        } );

        return builder.toString();
    }

    public static String generateInsertSqlVal( List<ColumnInfo> columns ) {
        return generateInsertSqlVal( columns, "" );
    }

    public static String generateInsertSqlVal( List<ColumnInfo> columns, String prefix ) {
        int[] index = new int[]{ 0 };
        String tab = "                ";
        StringBuilder builder = new StringBuilder();

        columns.forEach( column -> {
            if ( null == column.getJoinType() ) {
                builder.append( String.format( "#{%s%s}", prefix, column.getProperty() ) );

            } else {
                builder.append( String.format( "#{%s%s.id}", prefix, column.getProperty() ) );
            }

            if ( index[ 0 ] + 1 < columns.size() ) {
                builder.append( ", " );
            }

            if ( index[ 0 ] != 0 && index[ 0 ] % 5 == 0 ) {
                builder.append( "\n" ).append( tab );
            }

            index[ 0 ] += 1;
        } );

        return builder.toString();
    }

    public static String generateUpdateSql( List<ColumnInfo> columns ) {
        int[] index = new int[]{ 0 };
        String tab = "                ";
        boolean[] isFirst = new boolean[]{ true };
        StringBuilder builder = new StringBuilder();

        columns.forEach( column -> {
            if ( "id createTime".contains( column.getProperty() ) ) {
                index[ 0 ] += 1;
                return;
            }

            if ( !isFirst[ 0 ] ) {
                builder.append( tab );

            } else {
                isFirst[ 0 ] = !isFirst[ 0 ];
            }

            if( null == column.getJoinType() ) {
                builder.append( String.format( "%s = #{%s}", column.getColumn(), column.getProperty() ) );

            } else {
                builder.append( String.format( "%s = #{%s.id}", column.getColumn(), column.getProperty() ) );
            }

            if ( index[ 0 ] + 1 < columns.size() ) {
                builder.append( ", " );
            }

            builder.append( "\n" );

            index[ 0 ] += 1;
        } );

        return builder.toString();
    }
}
