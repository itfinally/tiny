package top.itfinally.admin.repository.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.itfinally.admin.repository.mapper.UserDetailsMapper;
import top.itfinally.admin.repository.po.UserDetailsEntity;
import top.itfinally.admin.support.vue.MultiFunctionTableQuery;
import top.itfinally.core.repository.dao.AbstractDao;
import top.itfinally.security.repository.mapper.UserAuthorityMapper;
import top.itfinally.security.repository.po.UserAuthorityEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static top.itfinally.core.enumerate.DataStatusEnum.DELETE;
import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;

@Repository
public class UserDetailsDao extends AbstractDao<UserDetailsEntity, UserDetailsMapper> {

  private UserDetailsMapper userDetailsMapper;
  private UserAuthorityMapper userAuthorityMapper;
  private PasswordEncoder passwordEncoder;

  @Override
  @Autowired
  protected void setBaseMapper( UserDetailsMapper baseMapper ) {
    this.userDetailsMapper = baseMapper;
    super.setBaseMapper( baseMapper );
  }

  @Autowired
  public UserDetailsDao setUserAuthorityMapper( UserAuthorityMapper userAuthorityMapper ) {
    this.userAuthorityMapper = userAuthorityMapper;
    return this;
  }

  @Autowired
  public UserDetailsDao setPasswordEncoder( PasswordEncoder passwordEncoder ) {
    this.passwordEncoder = passwordEncoder;
    return this;
  }

  public UserDetailsEntity queryByAccount( String account ) {
    return userDetailsMapper.queryByAccount( account );
  }

  @Override
  @Transactional
  public int save( UserDetailsEntity entity ) {
    int effectiveRow = 0;

    if ( StringUtils.isBlank( entity.getAuthorityId() ) ) {
      UserAuthorityEntity authority = new UserAuthorityEntity();
      effectiveRow += userAuthorityMapper.save( authority );
      entity.setAuthorityId( authority.getId() );
    }

    effectiveRow += userDetailsMapper.save( entity );
    return effectiveRow;
  }

  @Override
  @Transactional
  public int saveAll( Collection<UserDetailsEntity> entities ) {
    List<UserAuthorityEntity> authorities = new ArrayList<>();
    int effectiveRow = 0;

    entities.forEach( entity -> {
      if ( StringUtils.isBlank( entity.getAuthorityId() ) ) {
        UserAuthorityEntity authority = new UserAuthorityEntity();
        entity.setAuthorityId( authority.getId() );
        authorities.add( authority );
      }
    } );

    effectiveRow += userAuthorityMapper.saveAll( authorities );
    effectiveRow += userDetailsMapper.saveAll( entities );

    return effectiveRow;
  }

  public List<UserDetailsEntity> queryByMultiCondition( Map<String, Object> condition, int beginRow, int row ) {
    MultiFunctionTableQuery.conditionValidator( condition );

    if ( beginRow < 0 || row < 0 ) {
      beginRow = row = NOT_PAGING.getVal();
    }

    return userDetailsMapper.queryByMultiCondition( condition, beginRow, row );
  }

  public int countByMultiCondition( Map<String, Object> condition ) {
    MultiFunctionTableQuery.conditionValidator( condition );

    return userDetailsMapper.countByMultiCondition( condition );
  }

  public int updateUserStatus( int status, List<String> userIds ) {
    long now = System.currentTimeMillis();
    return userDetailsMapper.updateUserStatus( userIds, status, now, DELETE.getStatus() == status ? now : -1 );
  }

  @Transactional
  public int register( String account, String nickname, String password ) {
    UserAuthorityEntity authority = new UserAuthorityEntity();
    int effectiveRow = 0;

    effectiveRow += userAuthorityMapper.save( authority );
    effectiveRow += userDetailsMapper.save( new UserDetailsEntity()
        .setAccount( account )
        .setNickname( nickname )
        .setPassword( passwordEncoder.encode( password ) )
        .setAuthorityId( authority.getId() ) );

    return effectiveRow;
  }
}
