package top.itfinally.core.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class PropertiesUtils {
    private static ClassLoader loader = PropertiesUtils.class.getClassLoader();

    public static class Properties {
        private String path;
        private java.util.Properties properties;

        private Properties( String path ) {
            this.properties = new java.util.Properties();

            URL url = loader.getResource( path );
            if ( url != null ) {
                this.path = url.getPath();
            }

            try ( InputStream file = loader.getResourceAsStream( path ) ) {
                this.properties.load( file );

            } catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }

        public String getPath() {
            return path;
        }

        public String read( String name ) {
            return ( String ) properties.get( name );
        }

        public void write( String name, String val ) {
            properties.setProperty( name, val );
        }

        public java.util.Properties getProperties() {
            return this.properties;
        }

        public List<String> names() {
            List<String> names = new ArrayList<>();
            Enumeration<?> iterator = properties.propertyNames();

            while ( iterator.hasMoreElements() ) {
                names.add( ( String ) iterator.nextElement() );
            }

            return names;
        }

        public void save() {
            save( "" );
        }

        public void save( String comments ) {
            save( path, comments );
        }

        public void save( String path, String comment ) {
            try ( FileOutputStream file = new FileOutputStream( path ) ) {
                properties.store( file, comment );

            } catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    public static Properties getProperties( String path ) {
        return new Properties( path );
    }
}
