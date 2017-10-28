package top.itfinally.core.util;

import com.google.common.collect.Sets;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static class Item {
        private Object[] result;


        private Item( Object[] result ) {
            this.result = result;
        }

        public int size() {
            return result.length;
        }

        public <T> T get( int index ) {
            return getOrDefault( index, null );
        }

        public <T> T get( int index, Class<T> cls ) {
            return getOrDefault( index, cls, null );
        }

        @SuppressWarnings( "unchecked" )
        public <T> T getOrDefault( int index, T defaultVal ) {
            return null == result[ index ] ? defaultVal : ( T ) result[ index ];
        }

        public <T> T getOrDefault( int index, Class<T> cls, T defaultVal ) {
            return null == result[ index ] ? defaultVal : cls.cast( result[ index ] );
        }

        public Object[] getAll() {
            Object[] copy = new Object[ result.length ];
            System.arraycopy( result, 0, copy, 0, result.length );

            return copy;
        }

        @SuppressWarnings( "unchecked" )
        private <T> T[] getAll( Class<T> cls ) {
            return ( T[] ) new Object[ result.length ];
        }
    }

    public static final class ZipMethod implements Iterable<Item> {
        private final List<List<?>> container = new ArrayList<>();
        private final boolean isShortBoard;
        private final int maxLength;
        private final int minLength;

        @NotThreadSafe
        private class Itr implements Iterator<Item> {
            private volatile Object[] objects;
            private volatile int cursor = 0;

            @Override
            public boolean hasNext() {
                return isShortBoard ? cursor < minLength : cursor < maxLength;
            }

            @Override
            public Item next() {
                int cursor = this.cursor;
                int nextCursor = cursor + 1;

                objects = new Object[ container.size() ];

                for ( int index = 0, length = container.size(); index < length; index += 1 ) {
                    if ( cursor < container.get( index ).size() ) {
                        objects[ index ] = container.get( index ).get( cursor );

                    } else {
                        objects[ index ] = null;
                    }
                }

                if ( this.cursor != cursor ) {
                    throw new ConcurrentModificationException();
                }

                this.cursor = nextCursor;

                return new Item( objects );
            }
        }

        @SuppressWarnings( "unchecked" )
        private ZipMethod( boolean isShortBord, List<? extends Collection> iterable ) {
            int minLength = Integer.MAX_VALUE;
            int maxLength = 0;

            for ( Collection item : iterable ) {
                maxLength = Math.max( maxLength, item.size() );
                minLength = Math.min( minLength, item.size() );

                container.add( new ArrayList<>( item ) );
            }

            if ( iterable.isEmpty() ) {
                maxLength = minLength = 0;
            }

            this.maxLength = maxLength;
            this.minLength = minLength;
            this.isShortBoard = isShortBord;
        }


        @Override
        public Iterator<Item> iterator() {
            return new Itr();
        }

        @Override
        public void forEach( Consumer<? super Item> action ) {
            Objects.requireNonNull( action );

            int cursor = 0;
            Object[] objects;

            while ( true ) {
                objects = new Object[ this.container.size() ];

                for ( int index = 0, length = this.container.size(); index < length; index += 1 ) {
                    if ( cursor < this.container.get( index ).size() ) {
                        objects[ index ] = this.container.get( index ).get( cursor );

                    } else {
                        objects[ index ] = null;
                    }
                }

                action.accept( new Item( objects ) );

                cursor += 1;
                if ( isShortBoard ? cursor >= minLength : cursor >= maxLength ) {
                    break;
                }
            }
        }
    }

    private static final class MapMethod<T> {
        private final List<T> result = new ArrayList<>();
        private final Function<Item, T> action;
        private final ZipMethod zipped;

        private MapMethod( Function<Item, T> action, List<? extends Collection> iterable ) {
            Objects.requireNonNull( action );

            this.action = action;
            this.zipped = new ZipMethod( true, iterable );
        }

        private List<T> process() {
            for ( Item item : zipped ) {
                result.add( action.apply( item ) );
            }

            return result;
        }
    }

    public static ZipMethod zip( List<? extends Collection> iterable ) {
        return zip( true, iterable );
    }

    public static ZipMethod zip( boolean isShortBoard, List<? extends Collection> iterable ) {
        return new ZipMethod( isShortBoard, iterable );
    }

    public static <T> List<T> map( Function<Item, T> action, List<? extends Collection> iterable ) {
        return new MapMethod<>( action, iterable ).process();
    }

    // 并集
    @SafeVarargs
    public static <T> List<T> union( Collection<T> src, Collection<T>... iterable ) {
        Set<T> filter = new HashSet<>( src );
        Arrays.stream( iterable ).forEach( filter::addAll );

        return new ArrayList<>( filter );
    }

    // 交集
    @SafeVarargs
    public static <T> List<T> intersection( Collection<T> src, Collection<T>... iterable ) {
        // 浅复制一次, 否则会影响原集合
        src = new ArrayList<>( src );

        Arrays.stream( iterable ).forEach( src::retainAll );
        return new ArrayList<>( src );
    }

    // 补集
    @SafeVarargs
    public static <T> List<T> complement( Collection<T> src, Collection<T>... iterable ) {
        Set<T> hashSrc = Sets.newHashSet( src );
        List<T> result = new ArrayList<>();

        Arrays.stream( iterable ).forEach( collection -> {
            for ( T item : collection ) {
                if ( !hashSrc.contains( item ) ) {
                    result.add( item );
                }
            }
        } );

        return result;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T[] removeIf( T[] arr, Object match ) {
        return ( T[] ) Arrays.stream( arr )
                .filter( item -> {

                    if ( null == match ) {
                        return item != null;
                    }

                    return !match.equals( item );

                } ).toArray();
    }

    public static <T> List<T> removeIf( Collection<T> collection, Object match ) {
        return collection.stream()
                .filter( item -> {

                    if ( null == match ) {
                        return item != null;
                    }

                    return !match.equals( item );
                } )
                .collect( Collectors.toList() );
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T[] removeIf( T[] arr, Function<T, Boolean> action ) {
        if ( null == action ) {
            throw new NullPointerException( "action cannot be null" );
        }

        return ( T[] ) Arrays.stream( arr ).filter( item -> !action.apply( item ) ).toArray();
    }

    public static <T> List<T> removeIf( Collection<T> collection, Function<T, Boolean> action ) {
        if ( null == action ) {
            throw new NullPointerException( "action cannot be null" );
        }

        return collection.stream().filter( item -> !action.apply( item ) ).collect( Collectors.toList() );
    }
}
