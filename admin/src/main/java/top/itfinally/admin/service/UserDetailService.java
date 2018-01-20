package top.itfinally.admin.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import top.itfinally.admin.repository.dao.UserDetailsDao;
import top.itfinally.admin.repository.po.UserDetailsEntity;
import top.itfinally.admin.web.vo.UserDetailVoBean;
import top.itfinally.core.enumerate.DataStatusEnum;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.exception.PermissionDeniedException;
import top.itfinally.security.repository.dao.UserRoleDao;
import top.itfinally.security.repository.po.AbstractUserDetailsEntity;
import top.itfinally.security.service.AbstractUserDetailService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.ILLEGAL_REQUEST;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;

@Service
public class UserDetailService extends AbstractUserDetailService<UserDetailsEntity> {
    private UserDetailsDao userDetailsDao;
    private UserRoleDao userRoleDao;

    @Autowired
    public UserDetailService setUserDetailsDao( UserDetailsDao userDetailsDao ) {
        this.userDetailsDao = userDetailsDao;
        return this;
    }

    @Autowired
    public UserDetailService setUserRoleDao( UserRoleDao userRoleDao ) {
        this.userRoleDao = userRoleDao;
        return this;
    }

    public CollectionResponseVoBean<UserDetailVoBean> queryByMultiCondition( Map<String, Object> condition, int beginRow, int row ) {
        List<UserDetailsEntity> result = userDetailsDao.queryByMultiCondition( condition, beginRow, row );
        ResponseStatusEnum status = result.isEmpty() ? EMPTY_RESULT : SUCCESS;

        return new CollectionResponseVoBean<UserDetailVoBean>( status ).setResult( result.stream()
                .filter( item -> !"admin".equals( item.getAccount() ) )
                .map( UserDetailVoBean::new )
                .collect( Collectors.toList() ) );
    }

    public SingleResponseVoBean<Integer> countByMultiCondition( Map<String, Object> condition ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userDetailsDao.countByMultiCondition( condition ) );
    }

    public SingleResponseVoBean<Integer> updateUserDetail( String userId, String nickname, int status ) {
        UserDetailsEntity user = userDetailsDao.query( userId );
        if ( null == user ) {
            return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST ).setMessage( "User is not exists." );
        }

        if ( "admin".equals( user.getAccount() ) ) {
            throw new PermissionDeniedException( "Cannot modify admin by web interface." );
        }

        user.setStatus( status ).setNickname( nickname )
                .setDeleteTime( DELETE.getStatus() == status ? System.currentTimeMillis() : -1 );

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userDetailsDao.update( user ) );
    }

    public SingleResponseVoBean<Integer> updateUserStatus( int status, List<String> userIds ) {
        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userDetailsDao.updateUserStatus( status, userIds ) );
    }

    public SingleResponseVoBean<Integer> register( String account, String nickname, String password ) {
        if ( userDetailsDao.queryByAccount( account ) != null ) {
            return new SingleResponseVoBean<Integer>( EMPTY_RESULT ).setMessage( String.format( "Account '%s' already exist.", account ) );
        }

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userDetailsDao.register( account, nickname, password ) );
    }

    public SingleResponseVoBean<Integer> grantRolesTo( String userId, List<String> roleIds ) {
        UserDetailsEntity user = userDetailsDao.query( userId );
        if ( null == user || StringUtils.isBlank( user.getAuthorityId() ) ) {
            return new SingleResponseVoBean<Integer>( ILLEGAL_REQUEST ).setMessage( String.format( "User '%s' is not found.", userId ) );
        }

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userRoleDao.grantRolesTo( user.getAuthorityId(), roleIds ) );
    }

    @Override
    public UserDetailsEntity loadUserByAccount( String account ) {
        return userDetailsDao.queryByAccount( account );
    }

    @Override
    public UserDetailsEntity loadUserById( String userId ) {
        return userDetailsDao.query( userId );
    }

    @Override
    public int save( AbstractUserDetailsEntity user ) {
        return userDetailsDao.save( ( UserDetailsEntity ) user );
    }

    @Override
    public int update( AbstractUserDetailsEntity user ) {
        return userDetailsDao.update( ( UserDetailsEntity ) user );
    }
}
