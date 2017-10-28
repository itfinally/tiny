package top.itfinally.security.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import top.itfinally.security.repository.po.UserAuthorityEntity;

import java.io.Serializable;

@Service
@Primary
public class PermissionValidService implements PermissionEvaluator {

    @Override
    public boolean hasPermission( Authentication authentication, Object targetDomainObject, Object permission ) {
        if ( "anonymousUser".equals( authentication.getPrincipal() ) ) {
            return false;
        }

        UserAuthorityEntity userAuthority = ( UserAuthorityEntity ) authentication.getPrincipal();

        return true;
    }

    @Override
    public boolean hasPermission( Authentication authentication, Serializable targetId, String targetType, Object permission ) {
        return true;
    }
}
