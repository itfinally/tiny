package top.itfinally.security.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.security.repository.po.UserRoleEntity;

import java.util.Collection;
import java.util.List;

@Mapper
@Component
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
    List<UserRoleEntity> queryByAuthorityId( @Param( "authorityId" ) String authorityId, @Param( "status" ) int status );

    boolean hasUserAuthority( @Param( "authorityId" ) String authorityId, @Param( "status" ) int status );

    boolean hasAllUserAuthority( @Param( "authorityIds" ) Collection<String> authorityId, @Param( "status" ) int status );

    boolean hasRole( @Param( "roleId" ) String roleId, @Param( "status" ) int status );

    boolean hasAllRole( @Param( "roleIds" ) Collection<String> roleIds, @Param( "status" ) int status );
}
