package top.itfinally.admin.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.security.repository.po.RoleEntity;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface RoleEnhancedMapper extends BaseMapper<RoleEntity> {
  List<RoleEntity> queryByMultiCondition(
      @Param( "condition" ) Map<String, Object> condition,
      @Param( "beginRow" ) int beginRow, @Param( "row" ) int row );

  int countByMultiCondition( @Param( "condition" ) Map<String, Object> condition );

  int updateRoleStatus(
      @Param( "roleIds" ) List<String> roleIds, @Param( "status" ) int status,
      @Param( "updateTime" ) long updateTime, @Param( "deleteTime" ) long deleteTime );
}
