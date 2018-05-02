package top.itfinally.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.System.currentTimeMillis;

public interface Dates {
  String toString( String pattern, ZoneId zoneId );

  String toString( String pattern );

  String toString( ZoneId zoneId );

  String toString();

  Dates with( int val, ChronoField field );

  Dates plus( int val, ChronoUnit unit );

  Dates minus( int val, ChronoUnit unit );

  static Dates build() {
    return new DateImpl();
  }

  static Dates build( Date date ) {
    return new DateImpl( date );
  }

  static Dates build( long millis ) {
    return new DateImpl( millis );
  }
}

class DateImpl implements Dates {
  private static final Cache<String, DateTimeFormatter> formatters = CacheBuilder.newBuilder()
      .initialCapacity( 16 )
      .maximumSize( 1024 )
      .weakKeys()
      .build();

  private final AtomicReference<Instant> instant;

  DateImpl() {
    instant = new AtomicReference<>( Instant.ofEpochMilli( currentTimeMillis() ) );
  }

  DateImpl( Date date ) {
    instant = new AtomicReference<>( date.toInstant() );
  }

  DateImpl( long millis ) {
    instant = new AtomicReference<>( Instant.ofEpochMilli( millis ) );
  }

  private void compareAndSwap( Instant theNew ) {
    while ( !instant.compareAndSet( instant.get(), theNew ) ) ;
  }

  @Override
  public Dates with( int val, ChronoField field ) {
    Instant theOld = instant.get();
    ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset( theOld );
    Instant theNew = Instant.from( LocalDateTime.ofInstant( theOld, ZoneId.systemDefault() )
        .with( field, val ).toInstant( offset ) );

    compareAndSwap( theNew );
    return this;
  }

  @Override
  public Dates plus( int val, ChronoUnit unit ) {
    Instant theOld = instant.get();
    Instant theNew = theOld.plus( val, unit );

    compareAndSwap( theNew );
    return this;
  }

  @Override
  public Dates minus( int val, ChronoUnit unit ) {
    Instant theOld = instant.get();
    Instant theNew = theOld.minus( val, unit );

    compareAndSwap( theNew );
    return this;
  }

  private String toString( DateTimeFormatter formatter, ZoneId zoneId ) {
    return formatter.format( instant.get().atZone( zoneId ) );
  }

  @Override
  public String toString( String pattern, ZoneId zoneId ) {
    try {
      return toString( formatters.get( pattern, () -> DateTimeFormatter.ofPattern( pattern ) ), zoneId );

    } catch ( ExecutionException exp ) {
      throw new RuntimeException( exp );
    }
  }

  @Override
  public String toString( String pattern ) {
    try {
      return toString( formatters.get( pattern, () -> DateTimeFormatter.ofPattern( pattern ) ), ZoneId.systemDefault() );

    } catch ( ExecutionException exp ) {
      throw new RuntimeException( exp );
    }
  }

  @Override
  public String toString( ZoneId zoneId ) {
    try {
      return toString( formatters.get( "default", () -> DateTimeFormatter.ofLocalizedDateTime( FormatStyle.FULL ) ), zoneId );

    } catch ( ExecutionException exp ) {
      throw new RuntimeException( exp );
    }
  }

  @Override
  public String toString() {
    try {
      return toString( formatters.get( "default", () -> DateTimeFormatter.ofLocalizedDateTime( FormatStyle.FULL ) ), ZoneId.systemDefault() );

    } catch ( ExecutionException exp ) {
      throw new RuntimeException( exp );
    }
  }
}
