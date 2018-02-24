package top.itfinally.admin.support;

import org.springframework.security.core.context.SecurityContextHolder;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.RolePermissionEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public final class SecurityUtils {
  private SecurityUtils() {
  }

  public static RoleEntity getMaxRoleWithGrant( Function<String, List<RolePermissionEntity>> gettingRolePermissionCallBack ) {
    List<RoleEntity> roles = SecurityContextHolder.getContext()
        .getAuthentication().getAuthorities().stream()
        .map( role -> ( RoleEntity ) role )

        // 0, 1, 2, 3, 4
        .sorted( Comparator.comparingInt( RoleEntity::getPriority ) )
        .collect( toList() );

    RoleEntity[] matchRole = new RoleEntity[]{ null };
    boolean ignore = roles.stream().anyMatch( role -> {
      if ( "ROLE_ADMIN".equals( role.getAuthority() ) || gettingRolePermissionCallBack.apply( role.getId() )
          .stream().anyMatch( rp -> "grant".equalsIgnoreCase( rp.getPermission().getName() ) ) ) {

        matchRole[ 0 ] = role;
        return true;
      }

      return false;
    } );

    return matchRole[ 0 ];
  }
}
