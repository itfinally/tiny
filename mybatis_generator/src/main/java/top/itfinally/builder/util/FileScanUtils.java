package top.itfinally.builder.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class FileScanUtils {
  private static final String separator = File.separator;

  private FileScanUtils() {
  }

  public static List<String> doScan( String path ) {
    if ( null == path ) {
      return new ArrayList<>();
    }

    File fileOrDirectory = new File( path );
    if ( isFile( fileOrDirectory ) ) {
      return Collections.singletonList( path );
    }

    return doScanning( mergePaths( path, list( fileOrDirectory ) ), Stream.empty() )
        .collect( toList() );
  }

  private static Stream<String> doScanning( List<String> paths, Stream<String> fileStream ) {
    if ( paths.isEmpty() ) {
      return fileStream;
    }

    List<String> allFiles = paths.stream()
        .filter( pathOrFile -> new File( pathOrFile ).isFile() )
        .collect( toList() );

    List<String> allPaths = unBoxList( paths.stream()
        .filter( pathOrFile -> !new File( pathOrFile ).isFile() )
        .map( path -> mergePaths( path, new File( path ).list() ) )
        .collect( toList() ), Stream.empty() )

        .collect( toList() );

    return doScanning( allPaths, Stream.concat( fileStream, allFiles.stream() ) );
  }

  private static List<String> mergePaths( String path, String[] files ) {
    return Arrays.stream( files ).map( item -> path + separator + item ).collect( toList() );
  }

  private static Stream<String> unBoxList( List<List<String>> paths, Stream<String> stream ) {
    if ( paths.isEmpty() ) {
      return stream;
    }

    return unBoxList(
        paths.stream().skip( 1 ).collect( toList() ),
        Stream.concat( stream, paths.get( 0 ).stream() )
    );
  }

  public static boolean isFile( String path ) {
    return null != path && isFile( new File( path ) );
  }

  private static boolean isFile( File file ) {
    return file.exists() && file.isFile();
  }

  public static boolean isDirectory( String path ) {
    return null != path && isDirectory( new File( path ) );
  }

  private static boolean isDirectory( File directory ) {
    return directory.exists() && directory.isDirectory();
  }

  public static String[] list( String path ) {
    if ( null == path ) {
      return new String[ 0 ];
    }

    return list( new File( path ) );
  }

  private static String[] list( File directory ) {
    if ( !isDirectory( directory ) ) {
      return new String[ 0 ];
    }

    String[] list = directory.list();
    return null == list ? new String[ 0 ] : list;
  }
}
