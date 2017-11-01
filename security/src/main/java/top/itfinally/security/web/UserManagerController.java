package top.itfinally.security.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.repository.po.UserDetailsEntity;
import top.itfinally.security.service.AuthorizationService;
import top.itfinally.security.service.UserDetailService;
import top.itfinally.security.service.UserManagerService;
import top.itfinally.security.web.vo.UserAuthorityVoBean;

@RestController
@RequestMapping( "/user" )
public class UserManagerController {

    private UserManagerService userManagerService;

    private UserDetailService userDetailService;

    private AuthorizationService authorizationService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserManagerController setPasswordEncoder( PasswordEncoder passwordEncoder ) {
        this.passwordEncoder = passwordEncoder;
        return this;
    }

    @Autowired
    public UserManagerController setUserDetailService( UserDetailService userDetailService ) {
        this.userDetailService = userDetailService;
        return this;
    }

    @Autowired
    public UserManagerController setAuthorizationService( AuthorizationService authorizationService ) {
        this.authorizationService = authorizationService;
        return this;
    }

    @Autowired
    public UserManagerController setUserManagerService( UserManagerService userManagerService ) {
        this.userManagerService = userManagerService;
        return this;
    }

    @ResponseBody
    @GetMapping( "/get_own_authority_details" )
    public BaseResponseVoBean getOwnDetails() {
        UserAuthorityEntity user = ( UserAuthorityEntity ) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        return new SingleResponseVoBean<>( ResponseStatusEnum.SUCCESS )
                .setResult( new UserAuthorityVoBean( user ) );
    }

    @ResponseBody
    @GetMapping( "/create_user" )
    @Transactional
    public BaseResponseVoBean createUser() {
        UserDetailsEntity user = new UserDetailsEntity.Default()
                .setAccount( "testing2" )
                .setPassword( passwordEncoder.encode( "950116" ) )
                .setNickname( "Bug2" );

        userDetailService.save( user );
        return authorizationService.register( user.getId() );
    }
}
