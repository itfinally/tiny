package top.itfinally.admin.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.admin.repository.po.RoleMenuItemEntity;

import java.util.List;

@Mapper
@Component
public interface RoleMenuItemMapper extends BaseMapper<RoleMenuItemEntity> {
    List<RoleMenuItemEntity> queryRoleMenuItem( @Param( "roleId" ) String roleId );

    List<RoleMenuItemEntity> queryRoleMenuItemChain(
            @Param( "status" ) int status,
            @Param( "roleId" ) String roleId,
            @Param( "menuItemIds" ) List<String> menuItemIds
    );
}