package top.itfinally.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import top.itfinally.security.repository.dao.RolePermissionDao;
import top.itfinally.security.repository.po.PermissionEntity;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.RolePermissionEntity;
import top.itfinally.security.repository.po.UserAuthorityEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Primary
public class PermissionValidService implements PermissionEvaluator {
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private RolePermissionDao rolePermissionDao;
    private LoadingCache<String, Set<PermissionEntity>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite( 5, TimeUnit.DAYS )
            .build( new CacheLoader<String, Set<PermissionEntity>>() {
                @Override
                @ParametersAreNonnullByDefault
                public Set<PermissionEntity> load( String roleId ) throws Exception {
                    return rolePermissionDao.queryByRoleId( roleId ).stream()
                            .map( RolePermissionEntity::getPermission )
                            .collect( Collectors.toSet() );
                }
            } );

    @Autowired
    public PermissionValidService setRolePermissionDao( RolePermissionDao rolePermissionDao ) {
        this.rolePermissionDao = rolePermissionDao;
        return this;
    }

    @Override
    public boolean hasPermission( Authentication authentication, Object targetDomainObject, Object permission ) {
        if ( "anonymousUser".equals( authentication.getPrincipal() ) ) {
            return false;
        }

        List<RoleEntity> roles = ( ( UserAuthorityEntity ) authentication.getPrincipal() ).getRoles();
        return roles.stream()

                .filter( role -> {

                    // Admin has super power by default
                    if ( "ROLE_ADMIN".equals( role.getAuthority() ) ) {
                        return true;
                    }

                    try {
                        return cache.get( role.getAuthority() ).stream()
                                .filter( entity -> entity.getName().equals( permission ) )
                                .count() > 0;

                    } catch ( ExecutionException e ) {
                        logger.error( "Failed to load role's permission, ignore it and return false.", e );
                    }

                    return false;
                } )

                .count() > 0;
    }

    @Override
    public boolean hasPermission( Authentication authentication, Serializable targetId, String targetType, Object permission ) {
        return !"anonymousUser".equals( authentication.getPrincipal() );
    }

    public void refreshRolePermission( String roleId ) {
        if ( cache.asMap().containsKey( roleId ) ) {
            cache.refresh( roleId );
        }
    }
}
