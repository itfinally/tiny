package top.itfinally.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import top.itfinally.security.exception.UserNotFoundException;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public abstract class JwtTokenService {

  protected abstract SignatureAlgorithm getAlgorithm();

  protected abstract Key encodeKey();

  protected abstract Key decodeKey();

  public String create( String account ) {
    if ( null == account ) {
      throw new UserNotFoundException( "Account is empty, cannot generate token" );
    }

    Map<String, Object> params = new HashMap<>();
    params.put( "account", account );

    return Jwts.builder()
        .addClaims( params )
        .signWith( getAlgorithm(), encodeKey() )
        .compact();
  }

  public String loadByToken( String token ) {
    try {
      Claims body = Jwts.parser()
          .setSigningKey( decodeKey() )
          .parseClaimsJws( token )
          .getBody();

      return ( String ) body.get( "account" );

    } catch ( RuntimeException ignored ) {
      return null;
    }
  }

  @Component
  public static class Default extends JwtTokenService {
    private String randomSecret = "Must to override this secret.";

    @Override
    protected SignatureAlgorithm getAlgorithm() {
      return SignatureAlgorithm.HS256;
    }

    @Override
    protected Key encodeKey() {
      return generateKey();
    }

    @Override
    protected Key decodeKey() {
      return generateKey();
    }

    private Key generateKey() {
      return new SecretKeySpec(
          DatatypeConverter.parseBase64Binary( randomSecret ),
          getAlgorithm().getJcaName()
      );
    }
  }
}
