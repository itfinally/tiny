package top.itfinally.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.stereotype.Component;
import top.itfinally.security.repository.po.UserAuthorityEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class UserDetailCachingService {

  // security 包无法访问 user details dao, 因此当账户角色出现变化,
  // 使用标记来通知 JwtAuthorizationFilter 更新对应的 user, 为 true 的时候必须刷新用户
  private LoadingCache<String, Boolean> isAccountChange = CacheBuilder.newBuilder()
      .expireAfterAccess( 10, TimeUnit.MINUTES )
      .build( new CacheLoader<String, Boolean>() {
        @Override
        @ParametersAreNonnullByDefault
        public Boolean load( String key ) {
          return false;
        }
      } );

  public abstract UserAuthorityEntity loadFromCache( String account );

  public abstract void caching( String account, UserAuthorityEntity user );

  public abstract void remove( String account );

  public void setAccountChange( String authorityId ) {
    isAccountChange.put( authorityId, true );
  }

  public boolean isChange( String authorityId ) {
    try {
      boolean isChange = isAccountChange.get( authorityId );
      isAccountChange.put( authorityId, false );

      return isChange;

    } catch ( ExecutionException e ) {
      return false;
    }
  }

  @Component
  public static class Default extends UserDetailCachingService {
    private LoadingCache<String, UserAuthorityEntity> cache = CacheBuilder.newBuilder()
        .expireAfterWrite( 30, TimeUnit.DAYS )
        .build( new CacheLoader<String, UserAuthorityEntity>() {
          @Override
          @ParametersAreNonnullByDefault
          public UserAuthorityEntity load( String s ) throws Exception {

            // no cache , just redirect to login
            throw new AccountExpiredException( "Require re-login." );
          }
        } );

    @Override
    public UserAuthorityEntity loadFromCache( @NotNull String account ) {
      try {
        return cache.get( account );

      } catch ( Exception e ) {
        return null;
      }
    }

    @Override
    public void caching( @NotNull String account, @NotNull UserAuthorityEntity user ) {
      if ( StringUtils.isBlank( account ) || null == user ) {
        throw new NullPointerException( "Account and user must not be null." );
      }

      cache.put( account, user );
    }

    @Override
    public void remove( @NotNull String account ) {
      cache.invalidate( account );
    }
  }
}
