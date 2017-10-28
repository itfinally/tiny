package top.itfinally.security.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.po.UserDetailsEntity;
import top.itfinally.security.repository.mapper.DefaultUserMapper;

@Repository
public class DefaultUserDao extends AbstractDao<UserDetailsEntity.Default, DefaultUserMapper> {

    private DefaultUserMapper defaultUserMapper;

    @Override
    @Autowired
    protected void setBaseMapper( DefaultUserMapper baseMapper ) {
        this.defaultUserMapper = baseMapper;
        super.setBaseMapper( baseMapper );
    }

    public UserDetailsEntity.Default queryByAccount( String account ) {
        return defaultUserMapper.queryByAccount( account );
    }
}
