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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

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

  // 时间的更改仅仅是做加减法, 因此谁先谁后不影响结果
  // cas 在竞争大时会效率低下, cas + 锁又会导致复杂度过高, 用队列缓存 job 是个折衷选择
  private static final int MAX_JOB = 64;
  private final BlockingQueue<Supplier<Boolean>> jobs = new LinkedBlockingQueue<>( MAX_JOB );

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

  @SuppressWarnings( "unchecked" )
  private void offerJobAndFlush( Supplier<Boolean> job ) {
    if ( job.get() ) {
      return;
    }

    jobs.offer( job );

    if ( jobs.size() + 4 >= MAX_JOB ) {
      synchronized ( this ) {
        if ( jobs.size() + 4 >= MAX_JOB ) {
          Supplier<Boolean> item;

          while ( !jobs.isEmpty() ) {
            item = jobs.poll();
            while ( item != null && !item.get() ) ;
          }
        }
      }
    }
  }

  @Override
  public Dates with( int val, ChronoField field ) {
    offerJobAndFlush( () -> {
      Instant theOld = instant.get();

      ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset( theOld );
      Instant theNew = Instant.from( LocalDateTime.ofInstant( theOld, ZoneId.systemDefault() )
          .with( field, val ).toInstant( offset ) );

      return instant.compareAndSet( theOld, theNew );
    } );

    return this;
  }

  @Override
  public Dates plus( int val, ChronoUnit unit ) {
    offerJobAndFlush( () -> {
      Instant theOld = instant.get();
      Instant theNew = theOld.plus( val, unit );

      return instant.compareAndSet( theOld, theNew );
    } );
    return this;
  }

  @Override
  public Dates minus( int val, ChronoUnit unit ) {
    offerJobAndFlush( () -> {
      Instant theOld = instant.get();
      Instant theNew = theOld.minus( val, unit );

      return instant.compareAndSet( theOld, theNew );
    } );
    return this;
  }

  private String toString( DateTimeFormatter formatter, ZoneId zoneId ) {
    if ( !jobs.isEmpty() ) {
      synchronized ( this ) {
        while ( !jobs.isEmpty() ) {
          Supplier<Boolean> item = jobs.poll();
          while ( item != null && !item.get() ) ;
        }
      }
    }

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
