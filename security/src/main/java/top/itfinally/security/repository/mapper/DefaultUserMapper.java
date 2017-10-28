package top.itfinally.security.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.security.repository.po.UserDetailsEntity;

@Mapper
@Component
public interface DefaultUserMapper extends BaseMapper<UserDetailsEntity.Default> {
    UserDetailsEntity.Default queryByAccount( @Param( "account" ) String account );
}
