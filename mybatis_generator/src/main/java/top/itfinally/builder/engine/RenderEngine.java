package top.itfinally.builder.engine;

import org.apache.velocity.Template;
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.entity.*;

public interface RenderEngine {
  String TABLE = "table";
  String UTIL = "util";

  String render( Template template, TableInfo tableInfo );

  default void replaceBaseDao( BuilderConfigure configure, TableInfo tableInfo ) {
    EntityInfo extendEntity = tableInfo.getExtendEntity();
    if ( null == extendEntity ) {
      return;
    }

    String baseEntityName = configure.getBaseEntity().getName();
    if ( !tableInfo.getExtendEntity().getName().equals( baseEntityName ) ) {
      return;
    }

    if ( configure.getAbstractDaoCls() != null ) {
      tableInfo.getExtendEntity().setDaoName( configure.getAbstractDaoCls().getName() );
    }
  }

  default void replaceBaseMapper( BuilderConfigure configure, TableInfo tableInfo ) {
    EntityInfo extendEntity = tableInfo.getExtendEntity();
    if ( null == extendEntity ) {
      return;
    }

    String baseEntityName = configure.getBaseEntity().getName();
    if ( !tableInfo.getExtendEntity().getName().equals( baseEntityName ) ) {
      return;
    }

    if ( configure.getBaseMapperCls() != null ) {
      tableInfo.getExtendEntity().setMapperName( configure.getBaseMapperCls().getName() );
    }
  }
}
