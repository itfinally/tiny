package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.admin.repository.mapper.PermissionEnhancedMapper;
import top.itfinally.admin.support.vue.MultiFunctionTableQuery;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.mapper.PermissionMapper;
import top.itfinally.security.repository.po.PermissionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;

@Repository
public class PermissionEnhancedDao extends AbstractDao<PermissionEntity, PermissionEnhancedMapper> {

  private PermissionEnhancedMapper permissionEnhancedMapper;
  private PermissionMapper permissionMapper;

  @Autowired
  public PermissionEnhancedDao setPermissionEnhancedMapper( PermissionEnhancedMapper permissionEnhancedMapper ) {
    this.permissionEnhancedMapper = permissionEnhancedMapper;
    return this;
  }

  @Autowired
  public PermissionEnhancedDao setPermissionMapper( PermissionMapper permissionMapper ) {
    this.permissionMapper = permissionMapper;
    return this;
  }

  public List<PermissionEntity> queryByMultiCondition( Map<String, Object> condition, int beginRow, int row ) {
    MultiFunctionTableQuery.conditionValidator( condition );

    if ( beginRow < 0 || row < 0 ) {
      beginRow = row = NOT_PAGING.getVal();
    }

    return permissionEnhancedMapper.queryByMultiCondition( condition, beginRow, row );
  }

  public int countByMultiCondition( Map<String, Object> condition ) {
    MultiFunctionTableQuery.conditionValidator( condition );

    return permissionEnhancedMapper.countByMultiCondition( condition );
  }

  public int updatePermissionStatus( List<String> ids, int status ) {
    long now = System.currentTimeMillis();
    return permissionEnhancedMapper.updatePermissionStatus( ids, status, now, DELETE.getStatus() == status ? now : -1 );
  }

  public int permissionInitializing() {
    List<PermissionEntity> permissions = new ArrayList<>();
    permissions.add( new PermissionEntity().setName( "menu_write" ).setDescription( "菜单写权限" ) );
    permissions.add( new PermissionEntity().setName( "menu_read" ).setDescription( "菜单读权限" ) );

    return permissionMapper.saveAll( permissions );
  }
}
