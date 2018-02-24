package top.itfinally.security.web.component;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import top.itfinally.security.repository.po.UserAuthorityEntity;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
  private String token;
  private UserAuthorityEntity userAuthority;

  public JwtAuthenticationToken( String token, UserAuthorityEntity userAuthority ) {
    super( userAuthority.getAuthorities() );
    super.setAuthenticated( true );

    this.token = token;
    this.userAuthority = userAuthority;
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return userAuthority;
  }

  // Just copy from 'UsernamePasswordAuthenticationToken'
  public void setAuthenticated( boolean isAuthenticated ) throws IllegalArgumentException {
    if ( isAuthenticated ) {
      throw new IllegalArgumentException(
          "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead" );
    }

    super.setAuthenticated( false );
  }
}
