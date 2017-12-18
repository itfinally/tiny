package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.admin.repository.mapper.MenuRelationMapper;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.admin.repository.po.MenuRelationEntity;

import java.util.List;

@Repository
public class MenuRelationDao extends AbstractDao<MenuRelationEntity, MenuRelationMapper> {
    private MenuRelationMapper menuRelationMapper;

    @Override
    @Autowired
    protected void setBaseMapper( MenuRelationMapper baseMapper ) {
        this.menuRelationMapper = baseMapper;
        super.setBaseMapper( baseMapper );
    }

    public List<MenuRelationEntity> queryParentItem( String itemId, int status ) {
        return menuRelationMapper.queryParentItem( itemId, status );
    }

    public MenuRelationEntity queryDirectParentItem( String itemId, int status ) {
        return menuRelationMapper.queryDirectParentItem( itemId, status );
    }

    public List<MenuRelationEntity> queryChildItem( String itemId, int status ) {
        return menuRelationMapper.queryChildItem( itemId, status );
    }

    public List<MenuRelationEntity> queryDirectChildItem( String itemId, int status ) {
        return menuRelationMapper.queryDirectChildItem( itemId, status );
    }
}
