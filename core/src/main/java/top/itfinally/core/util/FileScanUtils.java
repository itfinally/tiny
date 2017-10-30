package top.itfinally.core.util;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        String[] files = sourceFile.list();
        if ( null == files ) {
            return paths;
        }

        return doScanning( mergePaths( path, files ), Stream.empty() )
                .collect( Collectors.toList() );
    }

    private static Stream<String> doScanning( List<String> paths, Stream<String> fileStream ) {
        if ( paths.isEmpty() ) {
            return fileStream;
        }

        List<String> allFiles = paths.stream()
                .filter( pathOrFile -> new File( pathOrFile ).isFile() )
                .collect( Collectors.toList() );

        List<String> allPaths = unBoxList( paths.stream()
                .filter( pathOrFile -> !new File( pathOrFile ).isFile() )
                .map( path -> mergePaths( path, new File( path ).list() ) )
                .collect( Collectors.toList() ), Stream.empty() )

                .collect( Collectors.toList() );


        for (String p: allPaths) {
            System.out.println(p);
        }

        return doScanning( allPaths, Stream.concat( fileStream, allFiles.stream() ) );
    }

    private static List<String> mergePaths( String path, String[] files ) {
        return Arrays.stream( files ).map( item -> path + File.separator + item ).collect( Collectors.toList() );
    }

    private static Stream<String> unBoxList( List<List<String>> paths, Stream<String> stream ) {
        if ( paths.isEmpty() ) {
            return stream;
        }

        return unBoxList(
                paths.stream().skip( 1 ).collect( Collectors.toList() ),
                Stream.concat( stream, paths.get( 0 ).stream() )
        );
    }
}
