package top.itfinally.core.repository.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.itfinally.core.repository.po.BaseEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
public interface BaseMapper<Entity extends BaseEntity> {
    Entity query( @Param( "id" ) String id );

    List<Entity> queryAll( @Param( "beginRow" ) int beginRow, @Param( "row" ) int row, @Param( "status" ) int status );

    List<Entity> queryBySpecificId( @Param( "ids" ) Collection<String> ids, @Param( "beginRow" ) int beginRow, @Param( "row" ) int row, @Param( "status" ) int status );

    int save( Entity entity );

    int saveAll( @Param( "entities" ) Collection<Entity> entities );

    int update( Entity entity );

    int specificUpdate( @Param( "id" ) String id, @Param( "updateTime" ) long updateTime, @Param( "fieldAndValues" ) Map<String, Object> fieldAndValues );

    int remove( @Param( "id" ) String id, @Param( "deleteTime" ) long deleteTime );

    int removeAll( @Param( "ids" ) Collection<String> ids, @Param( "deleteTime" ) long deleteTime );

    int physicalDelete( @Param( "id" ) String id );

    int physicalDeleteAll( @Param( "ids" ) Collection<String> ids );
}
