package top.itfinally.security.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.*;
import top.itfinally.security.service.AuthorizationService;

import java.util.List;

import static top.itfinally.core.enumerate.ResponseStatusEnum.BAD_REQUEST;

@RestController
@RequestMapping( "/authorization" )
public class AuthorizationController {

    private AuthorizationService authorizationService;

    @Autowired
    public AuthorizationController setAuthorizationService( AuthorizationService authorizationService ) {
        this.authorizationService = authorizationService;
        return this;
    }

    @ResponseBody
    @PostMapping( "/add_permission" )
    public BaseResponseVoBean addPermission(
            @RequestParam( "name" ) String name,
            @RequestParam( "description" ) String description
    ) {
        if ( StringUtils.isBlank( name ) ) {
            return new SingleResponseVoBean( BAD_REQUEST )
                    .setMessage( "Require parameter 'name'." );
        }

        if ( StringUtils.isBlank( description ) ) {
            return new SingleResponseVoBean( BAD_REQUEST )
                    .setMessage( "Require parameter 'description'." );
        }

        return authorizationService.addPermission( new PermissionEntity()
                .setName( name.toLowerCase() )
                .setDescription( description )
        );
    }

    @ResponseBody
    @PostMapping( "/add_role" )
    public BaseResponseVoBean addRole(
            @RequestParam( "name" ) String name,
            @RequestParam( "description" ) String description
    ) {
        if ( StringUtils.isBlank( name ) ) {
            return new SingleResponseVoBean( BAD_REQUEST )
                    .setMessage( "Require parameter 'name'." );
        }

        if ( StringUtils.isBlank( description ) ) {
            return new SingleResponseVoBean( BAD_REQUEST )
                    .setMessage( "Require parameter 'description'." );
        }

        return authorizationService.addRole( new RoleEntity()
                .setName( name.toUpperCase() )
                .setDescription( description )
        );
    }

    @ResponseBody
    @PostMapping( "/grant_role_to/{authorityId}" )
    public BaseResponseVoBean grantRoleTo(
            @PathVariable( "authorityId" ) String authorityId,
            @RequestBody List<String> roleIds
    ) {
        if ( StringUtils.isBlank( authorityId ) ) {
            return new SingleResponseVoBean( BAD_REQUEST )
                    .setMessage( "Require parameter 'authorityId'" );
        }

        if ( null == roleIds || roleIds.isEmpty() ) {
            return new SingleResponseVoBean( BAD_REQUEST )
                    .setMessage( "Require parameter 'roleIds'" );
        }

        return authorizationService.grantRolesTo( authorityId, roleIds );
    }

    @ResponseBody
    @PostMapping( "/grant_permission_to/{roleId}" )
    public BaseResponseVoBean grantPermissionTo(
            @PathVariable( "roleId" ) String roleId,
            @RequestBody List<String> permissionIds
    ) {
        if ( StringUtils.isBlank( roleId ) ) {
            return new SingleResponseVoBean( BAD_REQUEST )
                    .setMessage( "Require parameter 'roleId'" );
        }

        if ( null == permissionIds || permissionIds.isEmpty() ) {
            return new SingleResponseVoBean( BAD_REQUEST )
                    .setMessage( "Require parameter 'permissionIds'" );
        }

        return authorizationService.grantPermissionsTo( roleId, permissionIds );
    }

    @ResponseBody
    @GetMapping( "/get_roles" )
    public BaseResponseVoBean getRoles() {
        return authorizationService.getRoles();
    }

    @ResponseBody
    @GetMapping( "/get_permissions" )
    public BaseResponseVoBean getPermissions() {
        return authorizationService.getPermissions();
    }

    @ResponseBody
    @GetMapping( "/get_user_roles/{account}" )
    public BaseResponseVoBean getUserRoles( @PathVariable( "account" ) String account ) {
        return null;
    }
}
