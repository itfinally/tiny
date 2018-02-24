package top.itfinally.security.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.security.repository.po.PermissionEntity;

import java.util.List;

@Mapper
@Component
public interface PermissionMapper extends BaseMapper<PermissionEntity> {
  List<PermissionEntity> queryByRoleId( @Param( "roleId" ) String roleId );
}
