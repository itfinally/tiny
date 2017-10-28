package top.itfinally.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class DateUtils {
    private static final long SINGLE_DAY_MILLIS = 86400000L;
    private static final ThreadLocal<SimpleDateFormat> sdfToDay = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> sdfToMinute = new ThreadLocal<>();
    private static final ThreadLocal<SimpleDateFormat> sdfToSecond = new ThreadLocal<>();

    private static Map<ThreadLocal, String> format;

    static {
        Map<ThreadLocal, String> format = new HashMap<>();
        format.put( sdfToDay, "yyyy-MM-dd" );
        format.put( sdfToMinute, "yyyy-MM-dd HH:mm" );
        format.put( sdfToSecond, "yyyy-MM-dd HH:mm:ss" );

        DateUtils.format = Collections.unmodifiableMap( format );
    }

    private DateUtils() {
    }

    public static Date ago( int day ) {
        if ( day < 0 ) {
            throw new IllegalArgumentException( "day can not be negative" );
        }

        return new Date( System.currentTimeMillis() - day * SINGLE_DAY_MILLIS );
    }

    public static String ago( int day, boolean withTime ) {
        return format( ago( day ), withTime );
    }

    public static Date later( int day ) {
        if ( day < 0 ) {
            throw new IllegalArgumentException( "day can not be negative" );
        }

        return new Date( System.currentTimeMillis() - day * SINGLE_DAY_MILLIS );
    }

    public static String later( int day, boolean withTime ) {
        return format( later( day ), withTime );
    }

    public static boolean valid( String string ) {
        return string.matches( "^\\d{4}[._\\-/]\\d{2}[._\\-/]\\d{2}\\s*$(\\d{2}:\\d{2}(:\\d{2})?)?" );
    }

    public static String format( long timeMillis, boolean withTime ) {
        return getDateFormat( withTime ? sdfToSecond : sdfToDay ).format( timeMillis );
    }

    public static String format( Date time, boolean withTime ) {
        return null == time ? "" : getDateFormat( withTime ? sdfToSecond : sdfToDay ).format( time );
    }

    public static Date parse( String string ) {
        if ( !string.matches( "\\d{2,4}[._\\-/]\\d{1,2}[._\\-/]\\d{1,2}\\s*(\\d{1,2}:\\d{1,2}(:\\d{1,2})?)?" ) ) {
            throw new DateTimeParseException( "illegal time format!", string, 0 );
        }

        string = string.trim();
        return string.matches( ".*\\d{1,2}:\\d{1,2}(:\\d{1,2})?" ) ? withTime( string ) : withoutTime( string );
    }

    private static Date withTime( String string ) {
        string = beauty( string, true );

        try {
            return getDateFormat( string.matches( ".*\\d{1,2}:\\d{1,2}:\\d{1,2}$" ) ? sdfToSecond : sdfToMinute )
                    .parse( string );

        } catch ( ParseException e ) {
            throw new RuntimeException( e );
        }
    }

    private static Date withoutTime( String string ) {
        string = beauty( string, false );

        try {
            return getDateFormat( sdfToDay ).parse( string );

        } catch ( ParseException e ) {
            throw new RuntimeException( e );
        }
    }

    private static String beauty( String string, boolean withTime ) {
        String[] twoPart = string.split( "\\s+" );
        String year = twoPart[ 0 ];
        String beautifulDate;
        String[] items;

        items = year.split( "[._\\-/]" );
        for ( int index = 1; index < items.length; index += 1 ) {
            if ( items[ index ].matches( "\\d" ) ) {
                items[ index ] = "0" + items[ index ];
            }
        }

        if ( !items[ 0 ].matches( "\\d{4}" ) ) {
            int length = items[ 0 ].length();
            if ( 2 == length ) {
                items[ 0 ] = "20" + items[ 0 ];

            } else {
                items[ 0 ] = "2" + items[ 0 ];
            }
        }

        beautifulDate = String.format( "%s-%s-%s", ( Object[] ) items );

        if ( withTime ) {
            String time = twoPart[ 1 ];
            items = time.split( ":" );
            for ( int index = 0; index < items.length; index += 1 ) {
                if ( items[ index ].matches( "\\d" ) ) {
                    items[ index ] = "0" + items[ index ];
                }
            }

            if ( items.length < 3 ) {
                beautifulDate += String.format( " %s:%s", ( Object[] ) items );

            } else {
                beautifulDate += String.format( " %s:%s:%s", ( Object[] ) items );
            }
        }

        return beautifulDate;
    }

    private static SimpleDateFormat getDateFormat( ThreadLocal<SimpleDateFormat> threadLocal ) {
        SimpleDateFormat sdf;

        if ( threadLocal.get() != null ) {
            sdf = threadLocal.get();

        } else {
            sdf = new SimpleDateFormat( format.get( threadLocal ) );
            threadLocal.set( sdf );
        }

        return sdf;
    }
}
