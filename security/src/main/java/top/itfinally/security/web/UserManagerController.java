package top.itfinally.security.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import top.itfinally.core.enumerate.ResponseStatusEnum;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.service.UserManagerService;
import top.itfinally.security.web.vo.UserAuthorityVoBean;

@RestController
@RequestMapping( "/user" )
public class UserManagerController {

    private UserManagerService userManagerService;

    @Autowired
    public UserManagerController setUserManagerService( UserManagerService userManagerService ) {
        this.userManagerService = userManagerService;
        return this;
    }

    @ResponseBody
    @SuppressWarnings( "unchecked" )
    @GetMapping( "/get_own_authority_details" )
    public BaseResponseVoBean getOwnDetails() {
        UserAuthorityEntity user = ( UserAuthorityEntity ) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        return new SingleResponseVoBean<>( ResponseStatusEnum.SUCCESS )
                .setResult( new UserAuthorityVoBean( user ) );
    }
}
