package top.itfinally.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.*;
import top.itfinally.security.repository.dao.PermissionDao;
import top.itfinally.security.repository.dao.RoleDao;
import top.itfinally.security.repository.dao.UserAuthorityDao;
import top.itfinally.security.repository.dao.UserRoleDao;

import java.util.ArrayList;
import java.util.List;

import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;

@Service
public class AdminManagerService {
    private RoleDao roleDao;
    private UserRoleDao userRoleDao;
    private PermissionDao permissionDao;
    private UserAuthorityDao userAuthorityDao;
    private AbstractUserDetailService abstractUserDetailService;
    private AbstractCreatedAdminService abstractCreatedAdminService;

    @Autowired
    public AdminManagerService setRoleDao( RoleDao roleDao ) {
        this.roleDao = roleDao;
        return this;
    }

    @Autowired
    public AdminManagerService setUserRoleDao( UserRoleDao userRoleDao ) {
        this.userRoleDao = userRoleDao;
        return this;
    }

    @Autowired
    public AdminManagerService setPermissionDao( PermissionDao permissionDao ) {
        this.permissionDao = permissionDao;
        return this;
    }

    @Autowired
    public AdminManagerService setUserAuthorityDao( UserAuthorityDao userAuthorityDao ) {
        this.userAuthorityDao = userAuthorityDao;
        return this;
    }

    @Autowired
    public AdminManagerService setAbstractUserDetailService( AbstractUserDetailService abstractUserDetailService ) {
        this.abstractUserDetailService = abstractUserDetailService;
        return this;
    }

    @Autowired
    public AdminManagerService setAbstractCreatedAdminService( AbstractCreatedAdminService abstractCreatedAdminService ) {
        this.abstractCreatedAdminService = abstractCreatedAdminService;
        return this;
    }

    @Transactional
    public SingleResponseVoBean<Integer> createAdminAccount() {
        AbstractUserDetailsEntity admin = abstractCreatedAdminService.getAdmin();

        try {
            UserDetails user = abstractUserDetailService.loadUserByUsername( admin.getAccount() );

            if ( user != null ) {
                for ( GrantedAuthority authority : user.getAuthorities() ) {
                    if ( "ADMIN".equals( authority.getAuthority() ) ) {
                        return new SingleResponseVoBean<>( EMPTY_RESULT );
                    }
                }
            }

        } catch ( AuthenticationException ignored ) {
        }

        // create admin
        int effectRow = 0;
        RoleEntity role = roleDao.queryByName( "ADMIN" );
        UserAuthorityEntity authority = new UserAuthorityEntity();

        if ( null == role ) {
            throw new IllegalStateException( "Should be run the '/admin/initialization' endpoint before create admin." );
        }

        effectRow += userAuthorityDao.save( authority );
        effectRow += abstractUserDetailService.save( admin.setAuthorityId( authority.getId() ) );
        effectRow += userRoleDao.save( new UserRoleEntity().setUserAuthority( authority ).setRole( role ) );

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
    }

    public SingleResponseVoBean<Integer> lockAdminAccount() {
        AbstractUserDetailsEntity admin = abstractCreatedAdminService.getAdmin();
        UserAuthorityEntity user = ( UserAuthorityEntity ) abstractUserDetailService.loadUserByUsername( admin.getAccount() );
        if ( null == user ) {
            return new SingleResponseVoBean<Integer>( ResponseStatusEnum.ILLEGAL_REQUEST )
                    .setMessage( "Lock is failed, admin not found." );
        }

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( userAuthorityDao.update( user.setNonLocked( false ) ) );
    }

    @Transactional
    public SingleResponseVoBean<Integer> initialization() {
        List<RoleEntity> roles = new ArrayList<>();
        roles.add( new RoleEntity().setName( "ADMIN" ).setDescription( "管理员" ) );
        roles.add( new RoleEntity().setName( "USER" ).setDescription( "普通用户" ) );

        List<PermissionEntity> permissions = new ArrayList<>();
        permissions.add( new PermissionEntity().setName( "read" ).setDescription( "只读权限" ) );
        permissions.add( new PermissionEntity().setName( "write" ).setDescription( "写权限" ) );
        permissions.add( new PermissionEntity().setName( "delete" ).setDescription( "删除权限" ) );
        permissions.add( new PermissionEntity().setName( "update" ).setDescription( "修改权限" ) );
        permissions.add( new PermissionEntity().setName( "register_user" ).setDescription( "创建用户权限" ) );
        permissions.add( new PermissionEntity().setName( "grant" ).setDescription( "授权权限" ) );

        int effectRow = 0;
        effectRow += roleDao.saveAll( roles );
        effectRow += permissionDao.saveAll( permissions );

        return new SingleResponseVoBean<Integer>( SUCCESS ).setResult( effectRow );
    }
}
