package top.itfinally.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import top.itfinally.security.exception.UserNotFoundException;
import top.itfinally.security.repository.po.UserAuthorityEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.naming.AuthenticationException;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class UserDetailCachingService {

    public abstract UserAuthorityEntity loadFromCache( String account );

    public abstract void caching( String account, UserAuthorityEntity user );

    @Component
    public static class Default extends UserDetailCachingService {
        private LoadingCache<String, UserAuthorityEntity> cache = CacheBuilder.newBuilder()
                .expireAfterWrite( 30, TimeUnit.DAYS )
                .build( new CacheLoader<String, UserAuthorityEntity>() {
                    @Override
                    @ParametersAreNonnullByDefault
                    public UserAuthorityEntity load( String s ) throws Exception {

                        // no cache , just redirect to login
                        throw new AuthenticationException();
                    }
                } );

        @Override
        public UserAuthorityEntity loadFromCache( @NotNull String account ) {
            try {
                return cache.get( account );

            } catch ( ExecutionException e ) {
                return null;
            }
        }

        @Override
        public void caching( @NotNull String account, @NotNull UserAuthorityEntity user ) {
            if ( StringUtils.isBlank( account ) || null == user ) {
                throw new NullPointerException( "Account and user cannot be null." );
            }

            if( cache.asMap().containsKey( account ) ) {
                cache.refresh( account );
            }

            cache.put( account, user );
        }
    }
}
