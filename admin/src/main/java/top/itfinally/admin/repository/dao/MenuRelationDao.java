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

  public List<MenuRelationEntity> queryParentItems( String itemId, int status ) {
    return menuRelationMapper.queryParentItems( itemId, status );
  }

  public List<MenuRelationEntity> queryMultiParentItems( List<String> itemIds, int status ) {
    return menuRelationMapper.queryMultiParentItems( itemIds, status );
  }

  public MenuRelationEntity queryDirectParentItem( String itemId, int status ) {
    return menuRelationMapper.queryDirectParentItem( itemId, status );
  }

  public List<MenuRelationEntity> queryMultiDirectParentItems( List<String> itemIds, int status ) {
    return menuRelationMapper.queryMultiDirectParentItems( itemIds, status );
  }


  public List<MenuRelationEntity> queryChildItems( String itemId, int status ) {
    return menuRelationMapper.queryChildItems( itemId, status );
  }

  public List<MenuRelationEntity> queryMultiChildItems( List<String> itemIds, int status ) {
    return menuRelationMapper.queryMultiChildItems( itemIds, status );
  }

  public List<MenuRelationEntity> queryDirectChildItems( String itemId, int status ) {
    return menuRelationMapper.queryDirectChildItems( itemId, status );
  }

  public List<MenuRelationEntity> queryMultiDirectChildItems( List<String> itemIds, int status ) {
    return menuRelationMapper.queryMultiDirectChildItems( itemIds, status );
  }
}
