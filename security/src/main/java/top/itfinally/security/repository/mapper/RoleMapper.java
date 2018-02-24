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
  List<RoleEntity> queryRoleByAuthorityId( @Param( "authorityId" ) String authorityId, @Param( "status" ) int status );

  RoleEntity queryByName( @Param( "name" ) String name, @Param( "status" ) int status );

  List<RoleEntity> queryByPriority( @Param( "priority" ) int priority, @Param( "status" ) int status );
}
