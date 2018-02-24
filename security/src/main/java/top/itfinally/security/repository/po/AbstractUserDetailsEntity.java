package top.itfinally.security.repository.po;

import top.itfinally.core.repository.po.BaseEntity;

public abstract class AbstractUserDetailsEntity<Entity extends AbstractUserDetailsEntity> extends BaseEntity<Entity> {

  public AbstractUserDetailsEntity() {
  }

  public AbstractUserDetailsEntity( String id ) {
    super( id );
  }

  public abstract String getAccount();

  public abstract String getPassword();

  public abstract String getNickname();

  public abstract String getAuthorityId();

  public abstract Entity setAuthorityId( String authorityId );
}
