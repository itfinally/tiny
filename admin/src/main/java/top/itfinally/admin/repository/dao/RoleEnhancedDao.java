package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.admin.repository.mapper.RoleEnhancedMapper;
import top.itfinally.admin.support.vue.MultiFunctionTableQuery;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.RoleEntity;

import java.util.List;
import java.util.Map;

import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;

@Repository
public class RoleEnhancedDao extends AbstractDao<RoleEntity, RoleEnhancedMapper> {

  private RoleEnhancedMapper roleEnhancedMapper;

  @Autowired
  public RoleEnhancedDao setRoleEnhancedMapper( RoleEnhancedMapper roleEnhancedMapper ) {
    this.roleEnhancedMapper = roleEnhancedMapper;
    return this;
  }

  public List<RoleEntity> queryByMultiCondition( Map<String, Object> condition, int beginRow, int row ) {
    MultiFunctionTableQuery.conditionValidator( condition );

    if ( beginRow < 0 || row < 0 ) {
      beginRow = row = NOT_PAGING.getVal();
    }

    return roleEnhancedMapper.queryByMultiCondition( condition, beginRow, row );
  }

  public int countByMultiCondition( Map<String, Object> condition ) {
    MultiFunctionTableQuery.conditionValidator( condition );

    return roleEnhancedMapper.countByMultiCondition( condition );
  }

  public int updateRoleStatus( List<String> roleIds, int status ) {
    long now = System.currentTimeMillis();
    return roleEnhancedMapper.updateRoleStatus( roleIds, status, now, DELETE.getStatus() == status ? now : -1 );
  }
}
