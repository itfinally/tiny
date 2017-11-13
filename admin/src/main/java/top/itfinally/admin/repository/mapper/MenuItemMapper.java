package top.itfinally.admin.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.admin.repository.po.MenuItemEntity;

@Mapper
@Component
public interface MenuItemMapper extends BaseMapper<MenuItemEntity> {
}