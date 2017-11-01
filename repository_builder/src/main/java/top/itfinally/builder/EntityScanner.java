package top.itfinally.builder;

import top.itfinally.builder.repository.po.BaseEntity;
import top.itfinally.core.util.FileScanUtils;

import java.net.URL;

public class EntityScanner {
    public static void main( String[] args ) {
        URL url = EntityScanner.class.getClassLoader().getResource( BaseEntity.class.getPackage().getName().replaceAll( "\\.", "/" ) );
//        System.out.println(url);
        System.out.println(FileScanUtils.doScan( url.getPath() ));
    }
}
