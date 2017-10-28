package top.itfinally.security.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.itfinally.core.enumerate.DataStatusEnum;
import top.itfinally.core.repository.QueryEnum;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.repository.po.UserDetailsEntity;
import top.itfinally.security.repository.dao.*;

import java.util.List;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.NORMAL;

public abstract class UserDetailService<User extends UserDetailsEntity> implements UserDetailsService {
    private UserAuthorityDao userAuthorityDao;
    private RoleDao roleDao;

    @Autowired
    public UserDetailService setUserAuthorityDao( UserAuthorityDao userAuthorityDao ) {
        this.userAuthorityDao = userAuthorityDao;
        return this;
    }

    @Autowired
    public UserDetailService setRoleDao( RoleDao roleDao ) {
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
        return userAuthority.setUser( user ).setAuthorities( roleEntities );
    }

    public abstract User loadUserByAccount( String account );

    public abstract User loadUserById( String userId );

    public abstract int save( Object user );

    public abstract int update( Object user );

    @Service
    public static class Default extends UserDetailService<UserDetailsEntity.Default> {
        private DefaultUserDao defaultUserDao;

        @Autowired
        public Default setDefaultUserDao( DefaultUserDao defaultUserDao ) {
            this.defaultUserDao = defaultUserDao;
            return this;
        }

        @Override
        public UserDetailsEntity.Default loadUserByAccount( String account ) {
            return defaultUserDao.queryByAccount( account );
        }

        @Override
        public UserDetailsEntity.Default loadUserById( String userId ) {
            return defaultUserDao.query( userId );
        }

        @Override
        public int save( Object user ) {
            return defaultUserDao.save( ( UserDetailsEntity.Default ) user );
        }

        @Override
        public int update( Object user ) {
            return defaultUserDao.update( ( UserDetailsEntity.Default ) user );
        }
    }
}
