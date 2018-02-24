package top.itfinally.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegExpUtils {
  public static final int NO_OPTION = 0x00;

  private RegExpUtils() {
  }

  public interface RegExp {
    String[] exec( String string );

    Boolean test( String string );
  }

  public static RegExp compile( final String regex ) {
    return compile( regex, NO_OPTION );
  }

  public static RegExp compile( final String regex, final Integer options ) {
    return new RegExp() {
      private Pattern pattern = Pattern.compile( regex, options );

      @Override
      public String[] exec( String string ) {
        String[] group = null;
        Matcher result = pattern.matcher( string );

        if ( result.find() ) {
          group = new String[ result.groupCount() + 1 ];
          for ( int index = 0; index <= result.groupCount(); index += 1 ) {
            group[ index ] = result.group( index );
          }
        }

        return group;
      }

      @Override
      public Boolean test( String string ) {
        return string != null && pattern.matcher( string ).find();
      }
    };
  }
}
