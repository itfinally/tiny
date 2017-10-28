package top.itfinally.core.util;

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

public class ApiViewScanHelper {
    private final Logger logger = LoggerFactory.getLogger( getClass() );
    private final LocalVariableTableParameterNameDiscoverer discoverer;
    private final RegExpUtils.RegExp clsNameMatcher;
    private final String packageName;
    private final URL sourcePath;

    public ApiViewScanHelper( URL sourcePath, String clsNameRegExpression ) {
        this.clsNameMatcher = RegExpUtils.compile( clsNameRegExpression );

        String absoluteSourcePath = sourcePath.getFile();
        if ( !absoluteSourcePath.matches( ".*classes.*" ) ) {
            throw new IllegalArgumentException( "source path must be a classes path: " + absoluteSourcePath );
        }

        // spring boot 打包后路径会变成 classes! , 此处用非贪婪匹配去除该路径
        String[] paths = absoluteSourcePath.split( "classes.*?/" );
        this.packageName = paths[ paths.length - 1 ].replace( "/", "." );
        this.sourcePath = sourcePath;

        this.discoverer = new LocalVariableTableParameterNameDiscoverer();
    }

    public List<ApiViewVoBean> doScan( Class<?> cls ) {
        List<ApiViewVoBean> apiViewVoBeans = new ArrayList<>();

        String url = "";
        RequestMapping requestMapping = cls.getAnnotation( RequestMapping.class );
        if ( requestMapping != null && requestMapping.value().length > 0 ) {
            url += requestMapping.value()[ 0 ];
        }

        String[] fullNameEntry = cls.getName().split( "\\." );

        doMethodScanning( fullNameEntry[ fullNameEntry.length - 1 ], url, cls, apiViewVoBeans );

        return apiViewVoBeans;
    }

    public List<ApiViewVoBean> doScan() {
        List<ApiViewVoBean> apiViewVoBeans = new ArrayList<>();
        List<String> clsPaths = FileScanUtils.doScan( sourcePath.getPath() );

        if ( clsPaths.isEmpty() ) {
            return apiViewVoBeans;
        }

        String[] clsNameEntry;

        for ( String clsPath : clsPaths ) {
            // cls name 和 cls path 必须分别匹配出两个结果才算正常
            clsNameEntry = clsNameMatcher.exec( clsPath );

            if ( null == clsNameEntry || clsNameEntry.length < 2 ) {
                logger.warn( String.format(
                        "unsure it is a error! missing class name matched\n" +
                                "name: %s\n" +
                                "result: %s\n",
                        clsPath, Arrays.toString( clsNameEntry )
                ) );

                continue;
            }

            Class<?> cls;
            try {
                cls = Class.forName( String.format( "%s.%s", packageName, clsNameEntry[ 1 ] ) );

            } catch ( Exception e ) {
                logger.error(
                        "corrupt when app loading cls( %s )",
                        String.format( "%s.%s", packageName, clsNameEntry[ 1 ] )
                );

                throw new RuntimeException( e );
            }

            if ( null == cls.getAnnotation( Controller.class ) && null == cls.getAnnotation( RestController.class ) ) {
                continue;
            }

            String url = "";
            RequestMapping requestMapping = cls.getAnnotation( RequestMapping.class );
            if ( requestMapping != null && requestMapping.value().length > 0 ) {
                url += requestMapping.value()[ 0 ];
            }

            doMethodScanning( clsNameEntry[ 1 ], url, cls, apiViewVoBeans );
        }

        return apiViewVoBeans;
    }

    private List<ApiViewVoBean> doMethodScanning(
            String clsName, String requestUrl, Class<?> cls, List<ApiViewVoBean> apiViewVoBeans
    ) {
        if ( Object.class == cls || cls.isInterface() ) {
            return apiViewVoBeans;
        }

        String[] methodArgNames;
        Parameter[] methodArgObjs;
        Map<String, String> argEntry;
        Method[] methods = cls.getDeclaredMethods();
        for ( Method method : methods ) {

            // 判断是否抽象方法
            if ( Modifier.isAbstract( method.getModifiers() ) ) {
                continue;
            }

            methodArgNames = discoverer.getParameterNames( method );
            methodArgObjs = method.getParameters();

            if ( methodArgNames.length != methodArgObjs.length ) {
                logger.warn( String.format(
                        "methodArgName's length doesn't match methodArgObj's length.\n" +
                                "methodArgName: %d\n" +
                                "methodArgObj: %d\n" +
                                "method: %s.%s",
                        methodArgNames.length, methodArgObjs.length,
                        cls.getName(), method.getName()
                ) );

                continue;
            }

            argEntry = new HashMap<>();
            for ( int index = 0; index < methodArgNames.length; index += 1 ) {
                argEntry.put( methodArgNames[ index ], methodArgObjs[ index ].getType().getName() );
            }

            Description description = method.getAnnotation( Description.class );

            Map<String, List<String>> mappings = extractRequestMapping( method );
            if( mappings.isEmpty() ) {
                continue;
            }

            apiViewVoBeans.add(
                    new ApiViewVoBean()
                            .setFullName( String.format( "%s.%s.%s", packageName, clsName, method.getName() ) )
                            .setDescription( null == description ? "no description" : description.value() )
                            .setName( method.getName() )
                            .setUrls( mappings )
                            .setArgs( argEntry )
            );
        }

        return doMethodScanning( clsName, requestUrl, cls.getSuperclass(), apiViewVoBeans );
    }

    private Map<String, List<String>> extractRequestMapping( Method method ) {
        Map<String, List<String>> mappings = new HashMap<>();

        RequestMapping requestMapping = method.getAnnotation( RequestMapping.class );
        if ( requestMapping != null ) {
            for ( RequestMethod httpMethod : requestMapping.method() ) {
                mappings.putIfAbsent( httpMethod.name(), new ArrayList<>() );
                mappings.get( httpMethod.name() ).addAll( Arrays.asList( requestMapping.value() ) );
            }

            // has url mapping but not specific http method
            if ( requestMapping.value().length > 0 && requestMapping.method().length < 0 ) {
                mappings.put( "support all method", Arrays.asList( requestMapping.value() ) );
            }
        }

        GetMapping getMapping = method.getAnnotation( GetMapping.class );
        if ( getMapping != null ) {
            mappings.putIfAbsent( RequestMethod.GET.name(), new ArrayList<>() );
            mappings.get( RequestMethod.GET.name() ).addAll( Arrays.asList( getMapping.value() ) );
        }

        PostMapping postMapping = method.getAnnotation( PostMapping.class );
        if ( postMapping != null ) {
            mappings.putIfAbsent( RequestMethod.POST.name(), new ArrayList<>() );
            mappings.get( RequestMethod.POST.name() ).addAll( Arrays.asList( postMapping.value() ) );
        }

        PutMapping putMapping = method.getAnnotation( PutMapping.class );
        if ( putMapping != null ) {
            mappings.putIfAbsent( RequestMethod.PUT.name(), new ArrayList<>() );
            mappings.get( RequestMethod.PUT.name() ).addAll( Arrays.asList( putMapping.value() ) );
        }

        DeleteMapping deleteMapping = method.getAnnotation( DeleteMapping.class );
        if ( deleteMapping != null ) {
            mappings.putIfAbsent( RequestMethod.DELETE.name(), new ArrayList<>() );
            mappings.get( RequestMethod.DELETE.name() ).addAll( Arrays.asList( deleteMapping.value() ) );
        }

        return mappings;
    }
}
