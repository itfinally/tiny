package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.admin.repository.mapper.MenuRelationMapper;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.admin.repository.po.MenuRelationEntity;

@Repository
public class MenuRelationDao extends AbstractDao<MenuRelationEntity, MenuRelationMapper> {
    private MenuRelationMapper menuRelationMapper;

    @Override
    @Autowired
    protected void setBaseMapper( MenuRelationMapper baseMapper ) {
        this.menuRelationMapper = baseMapper;
        super.setBaseMapper( baseMapper );
    }
}
