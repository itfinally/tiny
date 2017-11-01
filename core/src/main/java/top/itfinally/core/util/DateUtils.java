package top.itfinally.core.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final ZoneOffset SYS_TIME_OFFSET = ZoneId.systemDefault()
            .getRules().getOffset( Instant.now() );

    private static final Cache<String, DateTimeFormatter> formatterCache = CacheBuilder
            .newBuilder().expireAfterWrite( 30, TimeUnit.MINUTES ).build();

    private DateUtils() {}

    public static String format( @NotNull String pattern, @NotNull Date date ) {
        if( StringUtils.isBlank( pattern ) ) {
            throw new IllegalArgumentException( "Pattern must be not null." );
        }

        return format( pattern, date.toInstant() );
    }

    public static String format( @NotNull String pattern, long millis ) {
        return format( pattern, new Date( millis ).toInstant() );
    }

    private static String format( String pattern, Instant instant ) {
        return getFormatter( pattern ).format( LocalDateTime.ofInstant( instant, SYS_TIME_OFFSET ) );
    }

    public static String getUTCTime( @NotNull String pattern ) {
        return getFormatter( pattern ).format( LocalDateTime.ofInstant( Instant.now(), ZoneOffset.UTC ) );
    }

    public static Date parseToDate( @NotNull String pattern, @NotNull String time ) {
        return Date.from( getInstant( pattern, time, SYS_TIME_OFFSET ) );
    }

    public static Date parseToDate( @NotNull String pattern, @NotNull String time, @NotNull ZoneOffset offset ) {
        return Date.from( getInstant( pattern, time, offset ) );
    }

    private static Instant getInstant( String pattern, String time, ZoneOffset offset ) {
        boolean hasD = hasDate( pattern ),
                hasT = hasTime( pattern );

        Instant utcTime;
        if ( hasD && hasT ) {
            utcTime = LocalDateTime
                    .from( getFormatter( pattern ).parse( time ) )
                    .toInstant( offset );

        } else if( hasD ) {
            utcTime = LocalDate.parse( time, getFormatter( pattern ) )
                    .atStartOfDay()
                    .toInstant( offset );

        } else if( hasT ) {
            utcTime = LocalTime.parse( time, getFormatter( pattern ) )
                    .atDate( LocalDate.of( 1970, 1, 1 ) )
                    .toInstant( offset );

        } else {
            throw new NullPointerException( String.format( "No matcher format processor. ( %s )", pattern ) );
        }

        return utcTime;
    }

    private static boolean hasDate( String pattern ) {
        return pattern.contains( "yyyy" ) || pattern.contains( "MM" ) || pattern.contains( "dd" );
    }

    private static boolean hasTime( String pattern ) {
        return pattern.contains( "hh" ) || pattern.contains( "HH" ) || pattern.contains( "mm" ) || pattern.contains( "ss" );
    }

    private static DateTimeFormatter getFormatter( String pattern ) {
        try {
            return formatterCache.get( pattern, () -> DateTimeFormatter.ofPattern( pattern ) );

        } catch ( ExecutionException e ) {
            throw new RuntimeException( e );
        }
    }
}
