package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.admin.repository.mapper.PermissionEnhancedMapper;
import top.itfinally.admin.support.vue.MultiFunctionTableQuery;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.PermissionEntity;

import java.util.List;
import java.util.Map;

import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;

@Repository
public class PermissionEnhancedDao extends AbstractDao<PermissionEntity, PermissionEnhancedMapper> {

    private PermissionEnhancedMapper permissionEnhancedMapper;

    @Autowired
    public PermissionEnhancedDao setPermissionEnhancedMapper( PermissionEnhancedMapper permissionEnhancedMapper ) {
        this.permissionEnhancedMapper = permissionEnhancedMapper;
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
}
