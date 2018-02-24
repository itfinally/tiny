package top.itfinally.admin.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.admin.repository.po.MenuRelationEntity;

import java.util.List;

@Mapper
@Component
public interface MenuRelationMapper extends BaseMapper<MenuRelationEntity> {
  List<MenuRelationEntity> queryParentItems( @Param( "itemId" ) String itemId, @Param( "status" ) int status );

  List<MenuRelationEntity> queryMultiParentItems( @Param( "itemIds" ) List<String> itemIds, @Param( "status" ) int status );

  MenuRelationEntity queryDirectParentItem( @Param( "itemId" ) String itemId, @Param( "status" ) int status );

  List<MenuRelationEntity> queryMultiDirectParentItems( @Param( "itemIds" ) List<String> itemIds, @Param( "status" ) int status );


  List<MenuRelationEntity> queryChildItems( @Param( "itemId" ) String itemId, @Param( "status" ) int status );

  List<MenuRelationEntity> queryMultiChildItems( @Param( "itemIds" ) List<String> itemIds, @Param( "status" ) int status );

  List<MenuRelationEntity> queryDirectChildItems( @Param( "itemId" ) String itemId, @Param( "status" ) int status );

  List<MenuRelationEntity> queryMultiDirectChildItems( @Param( "itemIds" ) List<String> itemIds, @Param( "status" ) int status );
}