package top.itfinally.admin.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.admin.repository.po.UserDetailsEntity;
import top.itfinally.core.repository.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface UserDetailsMapper extends BaseMapper<UserDetailsEntity> {
    UserDetailsEntity queryByAccount( @Param( "account" ) String account );

    List<UserDetailsEntity> queryByMultiCondition(
            @Param( "condition" ) Map<String, Object> condition,
            @Param( "beginRow" ) int beginRow,
            @Param( "row" ) int row
    );

    int countByMultiCondition( @Param( "condition" ) Map<String, Object> condition );

    int updateUserStatus(
            @Param( "status" ) int status,
            @Param( "updateTime" ) long updateTime,
            @Param( "deleteTime" ) long deleteTime,
            @Param( "userIds" ) List<String> userIds
    );
}
