package top.itfinally.core.util;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileScanUtils {
    private FileScanUtils() {
    }

    public static List<String> doScan( String path ) {
        if ( null == path ) {
            return new ArrayList<>();
        }

        File sourceFile = new File( path );

        if ( !sourceFile.exists() ) {
            return new ArrayList<>();
        }

        if ( sourceFile.isFile() ) {
            return Lists.newArrayList( path );
        }

        String[] files = sourceFile.list();
        if ( null == files ) {
            return new ArrayList<>();
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
