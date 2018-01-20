package top.itfinally.admin.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.security.repository.po.PermissionEntity;

import java.util.List;
import java.util.Map;

@Mapper
@Component

// Cannot use this mapper to call base mapper method.
public interface PermissionEnhancedMapper extends BaseMapper<PermissionEntity> {
    List<PermissionEntity> queryByMultiCondition(
            @Param( "condition" ) Map<String, Object> condition,
            @Param( "beginRow" ) int beginRow,
            @Param( "row" ) int row
    );

    int countByMultiCondition( @Param( "condition" ) Map<String, Object> condition );

    int updatePermissionStatus(
            @Param( "permissionIds" ) List<String> permissionIds, @Param( "status" ) int status,
            @Param( "updateTime" ) long updateTime, @Param( "deleteTime" ) long deleteTime );
}
