package top.itfinally.admin.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.admin.repository.mapper.MenuItemMapper;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.admin.repository.po.MenuItemEntity;

@Repository
public class MenuItemDao extends AbstractDao<MenuItemEntity, MenuItemMapper> {
    private MenuItemMapper menuItemMapper;

    @Override
    @Autowired
    protected void setBaseMapper( MenuItemMapper baseMapper ) {
        this.menuItemMapper = baseMapper;
        super.setBaseMapper( baseMapper );
    }
}
