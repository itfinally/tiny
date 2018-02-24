package top.itfinally.security.web.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.service.UserDetailCachingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;

@Component
public class JwtLogoutHandler implements LogoutHandler, LogoutSuccessHandler {
  private ObjectMapper jsonMapper = new ObjectMapper();

  private UserDetailCachingService userDetailCachingService;

  @Autowired
  public JwtLogoutHandler setUserDetailCachingService( UserDetailCachingService userDetailCachingService ) {
    this.userDetailCachingService = userDetailCachingService;
    return this;
  }

  @Override
  public void logout( HttpServletRequest request, HttpServletResponse response, Authentication authentication ) {
    authentication.setAuthenticated( false );

    Object principal = authentication.getPrincipal();
    if ( null == principal || "anonymousUser".equals( principal.toString() ) ) {
      return;
    }

    UserAuthorityEntity authorityEntity = ( UserAuthorityEntity ) principal;
    userDetailCachingService.remove( authorityEntity.getUser().getAccount() );
  }

  @Override
  public void onLogoutSuccess( HttpServletRequest request, HttpServletResponse response, Authentication authentication ) throws IOException {
    response.setContentType( "application/json;charset=UTF-8" );
    response.getWriter().write( jsonMapper.writeValueAsString( new BaseResponseVoBean( SUCCESS ).setMessage( "Logout successful." ) ) );
  }
}
