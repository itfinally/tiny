package top.itfinally.admin.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.admin.repository.po.MenuItemEntity;

import java.util.List;

@Mapper
@Component
public interface MenuItemMapper extends BaseMapper<MenuItemEntity> {
  List<MenuItemEntity> queryRootItem();

  MenuItemEntity queryByName( @Param( "name" ) String name );
}