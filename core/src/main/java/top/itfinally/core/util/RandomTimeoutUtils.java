package top.itfinally.core.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RandomTimeoutUtils {
    private static long range = 5;

    private RandomTimeoutUtils() {
    }

    private static boolean isOdd( long number ) {
        return number == ( number | 0x1 );
    }

    // Generate timeout order by given time and time unit.
    // It will get random time from range
    public static long generateTimeoutMillis( long time, TimeUnit unit ) {
        return unit.toMillis( time + ( System.currentTimeMillis() & range ) );
    }

    public static long getRange() {
        return range;
    }

    // hash operate must to use odd range.
    public static void setRange( long range ) {
        RandomTimeoutUtils.range = isOdd( range ) ? range : range + 1;
    }

    public static void main( String[] args ) throws InterruptedException {
        for ( int i = 0; i < 60; i++ ) {
            System.out.println( generateTimeoutMillis( 9, TimeUnit.DAYS ) );
            TimeUnit.SECONDS.sleep( 1 );
        }
        ;
    }
}
