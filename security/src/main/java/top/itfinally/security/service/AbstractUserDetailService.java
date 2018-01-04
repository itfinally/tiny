package top.itfinally.security.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import top.itfinally.security.repository.po.AbstractUserDetailsEntity;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.repository.dao.*;

import java.util.List;

public abstract class AbstractUserDetailService<User extends AbstractUserDetailsEntity<User>> implements UserDetailsService {
    private UserAuthorityDao userAuthorityDao;
    private RoleDao roleDao;

    @Autowired
    public AbstractUserDetailService setUserAuthorityDao( UserAuthorityDao userAuthorityDao ) {
        this.userAuthorityDao = userAuthorityDao;
        return this;
    }

    @Autowired
    public AbstractUserDetailService setRoleDao( RoleDao roleDao ) {
        this.roleDao = roleDao;
        return this;
    }

    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
        User user = loadUserByAccount( username );
        if ( null == user || StringUtils.isBlank( user.getAuthorityId() ) ) {
            throw new UsernameNotFoundException( String.format( "User '%s' is not exists.", username ) );
        }

        UserAuthorityEntity userAuthority = userAuthorityDao.query( user.getAuthorityId() );

        if ( null == userAuthority ) {
            throw new UsernameNotFoundException( String.format( "User '%s' haven't authority details.", username ) );
        }

        if ( !userAuthority.isAccountNonLocked() ) {
            throw new LockedException( String.format( "User '%s' has been locked.", username ) );
        }

        List<RoleEntity> roleEntities = roleDao.queryUserRoleByAuthorityId( userAuthority.getId() );

        // inject user and roles
        return userAuthority.setUser( user ).setAuthorities( roleEntities );
    }

    public abstract User loadUserByAccount( String account );

    public abstract User loadUserById( String userId );

    public abstract int save( AbstractUserDetailsEntity user );

    public abstract int update( AbstractUserDetailsEntity user );
}
