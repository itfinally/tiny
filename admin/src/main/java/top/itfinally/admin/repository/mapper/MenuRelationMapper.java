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
    List<MenuRelationEntity> queryParentItem( @Param( "itemId" ) String itemId, @Param( "status" ) int status );

    MenuRelationEntity queryDirectParentItem( @Param( "itemId" ) String itemId, @Param( "status" ) int status );

    List<MenuRelationEntity> queryChildItem( @Param( "itemId" ) String itemId, @Param( "status" ) int status );

    List<MenuRelationEntity> queryDirectChildItem( @Param( "itemId" ) String itemId, @Param( "status" ) int status );

    int removeChildItem( @Param( "itemId" ) String itemId, @Param( "deleteTime" ) long deleteTime );

    int removeMultiChildItem( @Param( "parentIds" ) List<String> parentIds, @Param( "deleteTime" ) long deleteTime );
}