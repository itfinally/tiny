package top.itfinally.security.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.security.repository.po.RolePermissionEntity;

import java.util.Collection;
import java.util.List;

@Mapper
@Component
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {
    List<RolePermissionEntity> queryByRoleId( @Param( "roleId" ) String roleId, @Param( "status" ) int status );

    boolean hasPermission( @Param( "permissionId" ) String permissionId, @Param( "status" ) int status );

    boolean hasAllPermission( @Param( "permissionIds" ) Collection<String> permissionIds, @Param( "status" ) int status );

    boolean hasRole( @Param( "roleId" ) String roleId, @Param( "status" ) int status );

    boolean hasAllRole( @Param( "roleIds" ) Collection<String> roleIds, @Param( "status" ) int status );
}
