package top.itfinally.core.web;

import top.itfinally.core.repository.BasicEntity;

import java.io.Serializable;

@SuppressWarnings( "unchecked" )
public class BasicVoBean<Vo extends BasicVoBean<Vo, Entity>, Entity extends BasicEntity<Entity>> implements Serializable {
  private String id;
  private int status;
  private long createTime;
  private long updateTime;
  private long deleteTime;

  public BasicVoBean() {
  }

  public BasicVoBean( Entity entity ) {
    id = entity.getId();
    status = entity.getStatus();
    createTime = entity.getCreateTime();
    updateTime = entity.getUpdateTime();
    deleteTime = entity.getDeleteTime();
  }

  public String getId() {
    return id;
  }

  public Vo setId( String id ) {
    this.id = id;
    return ( Vo ) this;
  }

  public int getStatus() {
    return status;
  }

  public Vo setStatus( int status ) {
    this.status = status;
    return ( Vo ) this;
  }

  public long getCreateTime() {
    return createTime;
  }

  public Vo setCreateTime( long createTime ) {
    this.createTime = createTime;
    return ( Vo ) this;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public Vo setUpdateTime( long updateTime ) {
    this.updateTime = updateTime;
    return ( Vo ) this;
  }

  public long getDeleteTime() {
    return deleteTime;
  }

  public Vo setDeleteTime( long deleteTime ) {
    this.deleteTime = deleteTime;
    return ( Vo ) this;
  }
}
