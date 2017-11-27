package top.itfinally.security.service;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KaptchaService {
    private final Producer producer;

    private final Cache<String, String> validCodes = CacheBuilder.newBuilder()
            .expireAfterWrite( 15, TimeUnit.MINUTES ).build();

    private final LoadingCache<String, AtomicInteger> loginCounter = CacheBuilder.newBuilder()
            .expireAfterWrite( 1, TimeUnit.DAYS ).build( new CacheLoader<String, AtomicInteger>() {
                @Override
                @ParametersAreNonnullByDefault
                public AtomicInteger load( String key ) throws Exception {
                    return new AtomicInteger();
                }
            } );

    public KaptchaService() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig( getConfig() );

        this.producer = kaptcha;
    }

    private Config getConfig() {
        Properties properties = new Properties();
        properties.setProperty( "kaptcha.border", "no" );
        properties.setProperty( "kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise" );
        properties.setProperty( "kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" );

        properties.setProperty( "kaptcha.image.width", "130" );
        properties.setProperty( "kaptcha.image.height", "45" );
        properties.setProperty( "kaptcha.textproducer.font.size", "32" );

//        properties.setProperty( "kaptcha.noise.color", "black" );
        properties.setProperty( "kaptcha.textproducer.font.color", "244,86,55" );

        properties.setProperty( "kaptcha.textproducer.char.length", "4" );
        properties.setProperty( "kaptcha.textproducer.font.names", "Monaco,Consolas,微软雅黑" );

        DefaultKaptcha producer = new DefaultKaptcha();
        producer.setConfig( new Config( properties ) );

        return new Config( properties );
    }

    public BufferedImage getImage( String account ) {
        String code = producer.createText();
        validCodes.put( account, code );

        return producer.createImage( code );
    }

    public void count( String account ) {
        try {
            loginCounter.get( account ).getAndIncrement();

        } catch ( ExecutionException ignored ) {
        }
    }

    public boolean requireValid( String account ) {
        try {
            return loginCounter.get( account ).get() >= 3;

        } catch ( ExecutionException e ) {
            return false;
        }
    }

    public boolean valid( String account, String code ) {
        if ( validCodes.asMap().containsKey( account ) ) {
            try {
                boolean isExist = validCodes.get( account, String::new ).equals( code );
                if ( isExist ) {
                    validCodes.asMap().remove( account );
                    loginCounter.asMap().remove( account );
                }

                return isExist;

            } catch ( ExecutionException e ) {
                return false;
            }
        }

        return true;
    }
}
