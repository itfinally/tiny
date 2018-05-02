package top.itfinally.core;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Regex {
  boolean test( String target );

  String[] findAll( String target );

  Iterator<String> findAsIterator( String target );

  static Regex compile( String regex ) {
    return compile( regex, 0 );
  }

  static Regex compile( String regex, Integer options ) {
    return new Regex() {
      private Pattern pattern = Pattern.compile( regex, options );

      @Override
      public boolean test( String target ) {
        return target != null && pattern.matcher( target ).find();
      }

      @Override
      public String[] findAll( String target ) {
        List<String> group = Lists.newArrayList();
        Matcher matcher = pattern.matcher( target );

        if ( matcher.find() ) {
          for ( int index = 0, length = matcher.groupCount() + 1; index < length; index += 1 ) {
            group.add( matcher.group( index ) );
          }
        }

        return group.toArray( new String[ 0 ] );
      }

      @Override
      public Iterator<String> findAsIterator( String target ) {
        return new Iterator<String>() {
          private Matcher matcher = pattern.matcher( target );
          private boolean isInitialize = false;
          private boolean isFind = false;

          private AtomicInteger index = new AtomicInteger( 0 );
          private int length = 0;

          @Override
          public boolean hasNext() {
            if ( !isInitialize ) {
              synchronized ( this ) {
                if ( !isInitialize ) {
                  isFind = matcher.find();
                  length = matcher.groupCount() + 1;

                  isInitialize = true;
                }
              }
            }

            return isFind && index.get() < length;
          }

          @Override
          public String next() {
            int localIndex = index.getAndIncrement();

            if ( localIndex >= length ) {
              throw new NoSuchElementException();
            }

            return matcher.group( localIndex );
          }
        };
      }
    };
  }
}
