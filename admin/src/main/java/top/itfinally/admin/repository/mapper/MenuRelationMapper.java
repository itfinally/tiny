package top.itfinally.admin.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.admin.repository.po.MenuRelationEntity;

@Mapper
@Component
public interface MenuRelationMapper extends BaseMapper<MenuRelationEntity> {
}