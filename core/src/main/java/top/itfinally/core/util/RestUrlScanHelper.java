package top.itfinally.core.util;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.itfinally.core.vo.ApiViewVoBean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RestUrlScanHelper {
    private final Logger logger = LoggerFactory.getLogger( getClass() );
    private final LocalVariableTableParameterNameDiscoverer discoverer;
    private final RegExpUtils.RegExp subPackageClassMatcher;
    private final RegExpUtils.RegExp clsNameMatcher;
    private final String packageName;
    private final URL sourcePath;

    public RestUrlScanHelper( Class<?> cls ) {
        if( null == cls ) {
            throw new NullPointerException( "Class must be not null." );
        }

        String packagePath = cls.getPackage().getName().replaceAll( "\\.", "/" );
        this.sourcePath = cls.getClassLoader().getResource( packagePath );

        if( null == this.sourcePath ) {
            throw new NullPointerException( "Not thing found from " + packagePath );
        }

        String absoluteSourcePath = sourcePath.getFile();
        if ( !absoluteSourcePath.matches( ".*classes.*" ) ) {
            throw new IllegalArgumentException( "Source path must be a classes path: " + absoluteSourcePath );
        }

        // spring boot 打包后路径会变成 classes! , 此处用非贪婪匹配去除该路径
        String[] paths = absoluteSourcePath.split( "classes.*?/" );
        this.packageName = paths[ paths.length - 1 ].replaceAll( "/", "." );

        this.discoverer = new LocalVariableTableParameterNameDiscoverer();
        this.clsNameMatcher = RegExpUtils.compile( "([\\w$]+)\\.class" );
        this.subPackageClassMatcher = RegExpUtils.compile( "((\\w+/)*([\\w$]+)).class" );
    }

    private class NullClass {
    }

    private class MethodMetaData {
        private final Method method;
        private final String[] names;
        private final Parameter[] parameters;

        MethodMetaData( Method method, String[] names ) {
            this.names = names;
            this.method = method;
            this.parameters = method.getParameters();
        }

        Method getMethod() {
            return method;
        }

        String[] getNames() {
            return names;
        }

        Parameter[] getParameters() {
            return parameters;
        }

        boolean isSameParameterLength( Class<?> cls ) {
            boolean isSame = names.length == parameters.length;

            if ( !isSame ) {
                logger.warn( String.format(
                        "method's parameter length doesn't match.\n" +
                                "method parameter: %d\n" +
                                "scan result: %d\n" +
                                "method: %s.%s",
                        names.length, parameters.length,
                        cls.getName(), method.getName()
                ) );
            }

            return isSame;
        }

        Map<String, String> getParameterDetails() {
            return Lists.newArrayList( CollectionUtils

                    .zip( Arrays.asList( names ), Arrays.asList( parameters ) ).iterator() )

                    .stream().collect( Collectors.toMap(
                            item -> item.get( 0 ),
                            item -> item.get( 1, Parameter.class ).getType().getName()
                    ) );
        }
    }

    public List<ApiViewVoBean> doScan( Class<?> cls ) {
        String[] fullNameEntry = cls.getName().split( "\\." );

        return doMethodScanning( fullNameEntry[ fullNameEntry.length - 1 ], cls, Stream.empty() )
                .collect( Collectors.toList() );
    }

    public List<ApiViewVoBean> doScanForPackage() {
        List<String> clsPaths = FileScanUtils.doScan( sourcePath.getPath() );
        String packagePath = packageName.replaceAll( "\\.", "/" );

        if ( clsPaths.isEmpty() ) {
            return new ArrayList<>();
        }

        List<Stream<ApiViewVoBean>> streams = clsPaths.stream()

                .filter( clsNameMatcher::test )

                // aaa/bbb/ccc/top/itfinally/core/a_package/example.class -> /a_package/example.class
                .map( path -> path.split( packagePath )[ 1 ] )

                .collect( Collectors.toMap(
                        // /a_package/example.class -> example
                        clsName -> clsNameMatcher.exec( clsName )[ 1 ],
                        clsName -> {
                            try {
                                // /a_package/example.class -> a_package.example
                                String realClsName = subPackageClassMatcher.exec( clsName )[ 1 ].replaceAll( "/", "." );
                                return Class.forName( String.format( "%s.%s", packageName, realClsName ) );

                            } catch ( ClassNotFoundException e ) {
                                logger.error(
                                        "Corrupt when app loading cls( %s )",
                                        String.format( "%s.%s", packageName, clsName )
                                );

                                return NullClass.class;
                            }
                        }
                ) )

                .entrySet().stream()

                .filter( entry -> {
                    Class<?> cls = entry.getValue();

                    return cls.getAnnotation( Controller.class ) != null ||
                            cls.getAnnotation( RestController.class ) != null;
                } )

                .map( entry -> doMethodScanning( entry.getKey(), entry.getValue(), Stream.empty() ) )

                .collect( Collectors.toList() );

        return streamMerge( streams, Stream.empty() );
    }

    private List<ApiViewVoBean> streamMerge( List<Stream<ApiViewVoBean>> streams, Stream<ApiViewVoBean> stream ) {
        if ( streams.isEmpty() ) {
            return stream.collect( Collectors.toList() );
        }

        return streamMerge(
                streams.stream().skip( 1 ).collect( Collectors.toList() ),
                Stream.concat( stream, streams.get( 0 ) )
        );
    }

    private Stream<ApiViewVoBean> doMethodScanning( String clsName, Class<?> cls, Stream<ApiViewVoBean> stream ) {
        if ( Object.class == cls || cls.isInterface() ) {
            return stream;
        }

        Stream<ApiViewVoBean> newStream = Arrays.stream( cls.getDeclaredMethods() )

                // Remove abstract method
                .filter( method -> !Modifier.isAbstract( method.getModifiers() ) )

                // Mapping checking
                .filter( this::hasRequestMapping )

                .map( method -> new MethodMetaData( method, discoverer.getParameterNames( method ) ) )

                // Parameters checking
                .filter( meta -> meta.isSameParameterLength( cls ) )

                .map( meta -> {
                    Description description = meta.getMethod().getAnnotation( Description.class );

                    return new ApiViewVoBean()
                            .setFullName( String.format( "%s.%s.%s", packageName, clsName, meta.getMethod().getName() ) )
                            .setDescription( null == description ? "No description" : description.value() )
                            .setUrls( extractRequestMapping( meta.getMethod() ) )
                            .setName( meta.getMethod().getName() )
                            .setArgs( meta.getParameterDetails() );
                } );

        return doMethodScanning( clsName, cls.getSuperclass(), Stream.concat( stream, newStream ) );
    }

    private boolean hasRequestMapping( Method method ) {
        RequestMapping requestMapping = method.getAnnotation( RequestMapping.class );
        PostMapping postMapping = method.getAnnotation( PostMapping.class );
        GetMapping getMapping = method.getAnnotation( GetMapping.class );
        PutMapping putMapping = method.getAnnotation( PutMapping.class );
        DeleteMapping deleteMapping = method.getAnnotation( DeleteMapping.class );

        return ( requestMapping != null && requestMapping.value().length > 0 ) ||
                ( postMapping != null && postMapping.value().length > 0 ) ||
                ( getMapping != null && getMapping.value().length > 0 ) ||
                ( putMapping != null && putMapping.value().length > 0 ) ||
                ( deleteMapping != null && deleteMapping.value().length > 0 );
    }

    private Map<String, List<String>> extractRequestMapping( Method method ) {
        Map<String, List<String>> mappings = new HashMap<>();

        RequestMapping requestMapping = method.getAnnotation( RequestMapping.class );
        if ( requestMapping != null ) {
            Arrays.stream( requestMapping.method() ).forEach( httpMethod -> mappings.putIfAbsent(
                    httpMethod.name(), new ArrayList<>( Arrays.asList( requestMapping.value() ) )
            ) );

            // has url mapping but not specific http method
            if ( requestMapping.value().length > 0 && requestMapping.method().length < 0 ) {
                mappings.put( "support all method", Arrays.asList( requestMapping.value() ) );
            }
        }

        GetMapping getMapping = method.getAnnotation( GetMapping.class );
        if ( getMapping != null ) {
            mappings.putIfAbsent( RequestMethod.GET.name(), new ArrayList<>( Arrays.asList( getMapping.value() ) ) );
        }

        PostMapping postMapping = method.getAnnotation( PostMapping.class );
        if ( postMapping != null ) {
            mappings.putIfAbsent( RequestMethod.POST.name(), new ArrayList<>( Arrays.asList( postMapping.value() ) ) );
        }

        PutMapping putMapping = method.getAnnotation( PutMapping.class );
        if ( putMapping != null ) {
            mappings.putIfAbsent( RequestMethod.PUT.name(), Arrays.asList( putMapping.value() ) );
        }

        DeleteMapping deleteMapping = method.getAnnotation( DeleteMapping.class );
        if ( deleteMapping != null ) {
            mappings.putIfAbsent( RequestMethod.DELETE.name(), Arrays.asList( deleteMapping.value() ) );
        }

        return mappings;
    }
}
