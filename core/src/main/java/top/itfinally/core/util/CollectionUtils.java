package top.itfinally.core.util;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static class Item {
        private final Object[] result;


        private Item( Object[] result ) {
            this.result = result;
        }

        public int size() {
            return result.length;
        }

        public <T> T get( int index ) throws ClassCastException {
            return getOrDefault( index, null );
        }

        public <T> T get( int index, Class<T> cls ) throws ClassCastException {
            return getOrDefault( index, cls, null );
        }

        @SuppressWarnings( "unchecked" )
        public <T> T getOrDefault( int index, T defaultVal ) throws ClassCastException {
            return null == result[ index ] ? defaultVal : ( T ) result[ index ];
        }

        public <T> T getOrDefault( int index, Class<T> cls, T defaultVal ) throws ClassCastException {
            if ( null == cls ) {
                throw new NullPointerException( "Class cannot be null." );
            }

            return index >= result.length || null == result[ index ] ? defaultVal : cls.cast( result[ index ] );
        }

        public Object[] getAll() {
            return Arrays.stream( result ).toArray();
        }

        @SuppressWarnings( "unchecked" )
        private <T> T[] getAll( Class<T> cls ) {
            if ( null == cls ) {
                throw new NullPointerException( "Class cannot be null." );
            }

            return Arrays.stream( result ).toArray( length -> ( T[] ) Array.newInstance( cls, length ) );
        }
    }

    public static final class ZipMethod implements Iterable<Item> {
        private final int maxLength;
        private final int minLength;
        private final boolean isQuick;
        private final List<List<?>> targets;

        private ZipMethod( boolean isQuick, Collection<?>[] targets ) {
            this.maxLength = targets.length <= 0 ? 0 : Arrays.stream( targets )
                    .map( Collection::size )
                    .max( ( sizeA, sizeB ) -> sizeA > sizeB ? sizeA : sizeB )
                    .orElse( 0 );

            this.minLength = targets.length <= 0 ? 0 : Arrays.stream( targets )
                    .map( Collection::size )
                    .min( ( sizeA, sizeB ) -> sizeA < sizeB ? sizeA : sizeB )
                    .orElse( 0 );

            this.targets = Arrays.stream( targets )
                    .map( ( Function<Collection<?>, ? extends ArrayList<?>> ) ArrayList::new )
                    .collect( Collectors.toList() );

            this.isQuick = isQuick;
        }


        @Override
        public Iterator<Item> iterator() {
            return new Iterator<Item>() {
                private final AtomicInteger cursor = new AtomicInteger();

                @Override
                public boolean hasNext() {
                    return cursor.get() < ( isQuick ? minLength : maxLength );
                }

                @Override
                public Item next() {
                    int cursor = this.cursor.getAndIncrement();
                    return new Item( targets.stream().map( item -> cursor >= item.size() ? null : item.get( cursor ) ).toArray() );
                }
            };
        }

        @Override
        public void forEach( Consumer<? super Item> action ) {
            if ( null == action ) {
                throw new NullPointerException( "Action cannot be null." );
            }

            forEach( action, 0 );
        }

        private int forEach( Consumer<? super Item> action, int cursor ) {
            if ( cursor >= ( isQuick ? minLength : maxLength ) ) {
                return 0;
            }

            action.accept( new Item( targets.stream().map( item -> cursor >= item.size() ? null : item.get( cursor ) ).toArray() ) );
            return forEach( action, cursor + 1 );
        }
    }

    public static ZipMethod zip( Collection<?>... targets ) {
        return new ZipMethod( true, targets );
    }

    public static ZipMethod zip( boolean isQuick, Collection<?>... targets ) {
        return new ZipMethod( isQuick, targets );
    }

    // 并集
    @SafeVarargs
    public static <T> List<T> union( @NotNull Collection<T> src, @NotNull Collection<T>... targets ) {
        if ( null == src || null == targets ) {
            throw new NullPointerException( "Src and targets cannot be null." );
        }

        if ( targets.length <= 0 ) {
            return new ArrayList<>( src );
        }

        return union(
                Stream.concat( src.stream(), targets[ 0 ].stream() ).collect( Collectors.toSet() ),
                Arrays.stream( targets ).skip( 1 ).toArray( ( IntFunction<Collection<T>[]> ) Collection[]::new )
        );
    }

    // 交集
    @SafeVarargs
    public static <T> List<T> intersection( @NotNull Collection<T> src, @NotNull Collection<T>... targets ) {
        if ( null == src || null == targets ) {
            throw new NullPointerException( "Src and targets cannot be null." );
        }

        return intersection( new HashSet<>( src ), Stream.empty(), targets ).distinct().collect( Collectors.toList() );
    }

    private static <T> Stream<T> intersection( Set<T> src, Stream<T> container, Collection<T>[] targets ) {
        if ( targets.length <= 0 ) {
            return container;
        }

        return intersection(
                src, Stream.concat( container, targets[ 0 ].stream().filter( src::contains ) ),
                Arrays.stream( targets ).skip( 1 ).toArray( ( IntFunction<Collection<T>[]> ) Collection[]::new )
        );
    }

    // 补集
    @SafeVarargs
    public static <T> List<T> complement( @NotNull Collection<T> src, @NotNull Collection<T>... targets ) {
        if ( null == src || null == targets ) {
            throw new NullPointerException( "Src and targets cannot be null." );
        }

        return complement( new HashSet<>( src ), Stream.empty(), targets ).distinct().collect( Collectors.toList() );
    }

    private static <T> Stream<T> complement( Set<T> src, Stream<T> container, Collection<T>[] targets ) {
        if ( targets.length <= 0 ) {
            return container;
        }

        return complement(
                src, Stream.concat( container, targets[ 0 ].stream().filter( item -> !src.contains( item ) ) ),
                Arrays.stream( targets ).skip( 1 ).toArray( ( IntFunction<Collection<T>[]> ) Collection[]::new )
        );
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T[] removeIf( @NotNull T[] arr, Object match ) {
        if ( null == arr ) {
            throw new NullPointerException( "Arr cannot be null." );
        }

        return ( T[] ) Arrays.stream( arr )
                .filter( item -> null == match ? item != null : !match.equals( item ) )
                .toArray();
    }

    public static <T> List<T> removeIf( @NotNull Collection<T> collection, Object match ) {
        if ( null == collection ) {
            throw new NullPointerException( "Collection cannot be null." );
        }

        return collection.stream()
                .filter( item -> null == match ? item != null : !match.equals( item ) )
                .collect( Collectors.toList() );
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T[] removeIf( @NotNull T[] arr, @NotNull Function<T, Boolean> action ) {
        if ( null == arr || null == action ) {
            throw new NullPointerException( "Arr and action cannot be null." );
        }

        return ( T[] ) Arrays.stream( arr ).filter( item -> !action.apply( item ) ).toArray();
    }

    public static <T> List<T> removeIf( @NotNull Collection<T> collection, @NotNull Function<T, Boolean> action ) {
        if ( null == collection || null == action ) {
            throw new NullPointerException( "Collection and action cannot be null." );
        }

        return collection.stream().filter( item -> !action.apply( item ) ).collect( Collectors.toList() );
    }
}
