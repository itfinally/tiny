package top.itfinally.admin.repository;

import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.core.FileBuilder;
import top.itfinally.builder.core.TableScanner;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.core.repository.po.BaseEntity;

public class Generator {
  public static void main( String[] args ) {
    BuilderConfigure configure = new BuilderConfigure()
        .setBaseEntity( BaseEntity.class )
        .setAbstractDaoCls( AbstractDao.class )
        .setBaseMapperCls( BaseMapper.class )
        .setScanPackage( "top.itfinally.admin.repository.po" )
        .setPackageName( "top.itfinally.admin.repository" )
        .setTargetFolder( "/Users/finally/workbeach/idea/itfinally_project/admin/src/main/java/top/itfinally/admin/repository" )
        .setForceCreation( false );

    configure.checking();

    new FileBuilder( configure, new TableScanner( configure ).doScan() ).initialize().build();
  }
}
