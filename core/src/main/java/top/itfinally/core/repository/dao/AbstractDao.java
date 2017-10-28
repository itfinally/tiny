package top.itfinally.core.repository.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import top.itfinally.core.repository.mapper.BaseMapper;
import top.itfinally.core.repository.po.BaseEntity;

import java.util.*;
import java.util.Collection;

public abstract class AbstractDao<Entity extends BaseEntity<Entity>, Mapper extends BaseMapper<Entity>>
        implements BaseMapper<Entity> {
    private Logger logger = LoggerFactory.getLogger( getClass() );

    private Mapper baseMapper;

    private SqlSessionFactory sessionFactory;

    protected void setBaseMapper( Mapper baseMapper ) {
        this.baseMapper = baseMapper;
    }

    @Autowired
    public void setSessionFactory( SqlSessionFactory sessionFactory ) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Entity query( String id ) {
        return baseMapper.query( id );
    }

    @Override
    public List<Entity> queryAll( int beginRow, int row, int status ) {
        return baseMapper.queryAll( beginRow, row, status );
    }

    @Override
    public List<Entity> queryBySpecificId( Collection<String> ids, int beginRow, int row, int status ) {
        return baseMapper.queryBySpecificId( ids, beginRow, row, status );
    }

    @Override
    public int save( Entity entity ) {
        return baseMapper.save( entity );
    }

    @Override
    public int saveAll( Collection<Entity> entities ) {
        return baseMapper.saveAll( entities );
    }

    @Override
    public int update( Entity entity ) {
        return baseMapper.update( entity.setUpdateTime( System.currentTimeMillis() ) );
    }

    @SuppressWarnings( "unchecked" )
    public int updateAll( Collection<Entity> entities ) {
        SqlSession session = sessionFactory.openSession();
        Mapper mapper = session.getMapper( ( Class<Mapper> ) baseMapper.getClass() );

        try {
            for ( Entity entity : entities ) {
                mapper.update( entity.setUpdateTime( System.currentTimeMillis() ) );
            }

            session.commit();
            return session.flushStatements().size();

        } catch ( Exception e ) {
            logger.error( "batch update failure.", e );
            session.rollback();

        } finally {
            session.clearCache();
            session.close();
        }

        return 0;
    }

    @Override
    public int specificUpdate( String id, long updateTime, Map<String, Object> fieldAndValues ) {
        return baseMapper.specificUpdate( id, updateTime, fieldAndValues );
    }

    @Override
    public int remove( String id, long deleteTime ) {
        return baseMapper.remove( id, deleteTime );
    }

    @Override
    public int removeAll( Collection<String> ids, long deleteTime ) {
        return baseMapper.removeAll( ids, deleteTime );
    }

    @Override
    public int physicalDelete( String id ) {
        throw new UnsupportedOperationException( "PhysicalDelete function already banned. ( If you want to open please modify the source code )" );
    }

    @Override
    public int physicalDeleteAll( Collection<String> ids ) {
        throw new UnsupportedOperationException( "PhysicalDeleteAll function already banned. ( If you want to open please modify the source code )" );
    }
}
