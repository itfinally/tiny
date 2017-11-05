package top.itfinally.builder.engine;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import top.itfinally.builder.entity.TableMetaData;

public interface RenderEngine {
    String PACKAGE = "package";
    String TABLE = "table";
    String UTIL = "util";

    String render( Template template, TableMetaData tableMetaData );

    // class static method for call by velocity template

    static String getOffset( String val ) {
        if ( StringUtils.isBlank( val ) ) {
            return val;
        }

        return "." + val;
    }

    static String lowerFirstChar( String val ) {
        char[] valChar = val.toCharArray();

        if ( valChar[ 0 ] < 97 ) {
            valChar[ 0 ] += 32;
        }

        return new String( valChar );
    }
}
