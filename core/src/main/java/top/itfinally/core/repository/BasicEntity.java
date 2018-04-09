package top.itfinally.core.repository;

import top.itfinally.core.EntityStatus;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import java.io.Serializable;
import java.util.Objects;

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;

@MappedSuperclass
@SuppressWarnings( "unchecked" )
public class BasicEntity<Entity extends BasicEntity<Entity>> implements Serializable {
  protected String id = randomUUID().toString().replace( "-", "" );
  protected long createTime = currentTimeMillis();
  protected long updateTime = createTime;
  protected long deleteTime = -1L;
  protected int status = EntityStatus.NORMAL.getCode();

  public BasicEntity() {
  }

  public BasicEntity( String id ) {
    this.id = id;
  }

  @Id
  @Column( columnDefinition = "varchar(64)" )
  public String getId() {
    return id;
  }

  public Entity setId( String id ) {
    this.id = id;
    return ( Entity ) this;
  }

  @Column( name = "create_time", columnDefinition = "bigint not null" )
  public long getCreateTime() {
    return createTime;
  }

  public Entity setCreateTime( long createTime ) {
    this.createTime = createTime;
    return ( Entity ) this;
  }

  @Column( name = "update_time", columnDefinition = "bigint not null" )
  public long getUpdateTime() {
    return updateTime;
  }

  public Entity setUpdateTime( long updateTime ) {
    this.updateTime = updateTime;
    return ( Entity ) this;
  }

  @Column( name = "delete_time", columnDefinition = "bigint not null" )
  public long getDeleteTime() {
    return deleteTime;
  }

  public Entity setDeleteTime( long deleteTime ) {
    this.deleteTime = deleteTime;
    return ( Entity ) this;
  }

  @Column( columnDefinition = "int(3) default 1 not null" )
  public int getStatus() {
    return status;
  }

  public Entity setStatus( int status ) {
    this.status = status;
    return ( Entity ) this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    BasicEntity<?> that = ( BasicEntity<?> ) o;
    return createTime == that.createTime &&
        updateTime == that.updateTime &&
        deleteTime == that.deleteTime &&
        status == that.status &&
        Objects.equals( id, that.id );
  }

  @Override
  public int hashCode() {
    return Objects.hash( id, createTime, updateTime, deleteTime, status );
  }

  @Override
  public String toString() {
    return "BasicEntity{" +
        "id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
