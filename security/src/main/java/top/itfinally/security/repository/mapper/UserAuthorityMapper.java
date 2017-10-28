package top.itfinally.security.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.security.repository.po.UserAuthorityEntity;

@Mapper
@Component
public interface UserAuthorityMapper extends BaseMapper<UserAuthorityEntity> {
}
