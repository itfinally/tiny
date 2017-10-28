package top.itfinally.security.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.security.repository.po.RoleEntity;

import java.util.List;

@Mapper
@Component
public interface RoleMapper extends BaseMapper<RoleEntity> {
    List<RoleEntity> queryUserRoleByAuthorityId( @Param( "authorityId" ) String authorityId );

    RoleEntity queryByName( @Param( "name" ) String name );
}
