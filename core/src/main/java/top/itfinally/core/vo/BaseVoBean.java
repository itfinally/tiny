package top.itfinally.core.vo;

import top.itfinally.core.repository.po.BaseEntity;

import java.io.Serializable;
import java.util.Objects;

public class BaseVoBean<Vo extends BaseVoBean<Vo>> implements Serializable {
  protected String id;
  protected int status;
  protected long createTime;
  protected long updateTime;
  protected long deleteTime;

  public BaseVoBean() {
  }

  public BaseVoBean( BaseEntity entity ) {
    this.id = entity.getId();
    this.status = entity.getStatus();
    this.createTime = entity.getCreateTime();
    this.updateTime = entity.getUpdateTime();
    this.deleteTime = entity.getDeleteTime();
  }

  public String getId() {
    return id;
  }

  @SuppressWarnings( "unchecked" )
  public Vo setId( String id ) {
    this.id = id;
    return ( Vo ) this;
  }

  public int getStatus() {
    return status;
  }

  @SuppressWarnings( "unchecked" )
  public Vo setStatus( int status ) {
    this.status = status;
    return ( Vo ) this;
  }

  public long getCreateTime() {
    return createTime;
  }

  @SuppressWarnings( "unchecked" )
  public Vo setCreateTime( long createTime ) {
    this.createTime = createTime;
    return ( Vo ) this;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  @SuppressWarnings( "unchecked" )
  public Vo setUpdateTime( long updateTime ) {
    this.updateTime = updateTime;
    return ( Vo ) this;
  }

  public long getDeleteTime() {
    return deleteTime;
  }

  @SuppressWarnings( "unchecked" )
  public Vo setDeleteTime( long deleteTime ) {
    this.deleteTime = deleteTime;
    return ( Vo ) this;
  }

  @Override
  public String toString() {
    return "BaseVoBean{" +
        "id='" + id + '\'' +
        ", status=" + status +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        '}';
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    BaseVoBean<?> that = ( BaseVoBean<?> ) o;
    return status == that.status &&
        createTime == that.createTime &&
        updateTime == that.updateTime &&
        deleteTime == that.deleteTime &&
        Objects.equals( id, that.id );
  }

  @Override
  public int hashCode() {
    return Objects.hash( id, status, createTime, updateTime, deleteTime );
  }
}
