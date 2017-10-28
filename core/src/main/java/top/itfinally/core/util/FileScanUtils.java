package top.itfinally.core.util;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FileScanUtils {
    private FileScanUtils() {
    }

    public static List<String> doScan( String path ) {
        List<String> paths = new ArrayList<>();
        if ( null == path ) {
            return paths;
        }

        File sourceFile = new File( path );

        if ( !sourceFile.exists() ) {
            return paths;
        }

        if ( sourceFile.isFile() ) {
            paths.add( path );
            return paths;
        }

        String[] subPaths = sourceFile.list();
        if ( null == subPaths ) {
            return paths;
        }

        Deque<String> subPathDeque = castArrayToDeque( path, subPaths );
        while ( subPathDeque.size() > 0 ) {
            doFileTreeScanning( subPathDeque, paths );
        }

        return paths;
    }

    private static List<String> doFileTreeScanning( Deque<String> deque, List<String> paths ) {
        String path;
        Iterator<String> iterator = deque.iterator();

        while ( iterator.hasNext() ) {
            path = iterator.next();
            if ( new File( path ).isFile() ) {
                paths.add( path );
                iterator.remove();
            }
        }

        if ( deque.isEmpty() ) {
            return paths;

        } else {
            String subPath = deque.pop();
            String[] subPaths = new File( subPath ).list();
            if ( subPaths != null ) {
                deque.addAll( castArrayToDeque( subPath, subPaths ) );
            }

            return doFileTreeScanning( deque, paths );
        }
    }

    private static Deque<String> castArrayToDeque( String path, String[] subPaths ) {
        return Arrays.stream( subPaths )
                .map( item -> path + File.separator + item )
                .collect( Collectors.toCollection( ArrayDeque::new ) );
    }
}
