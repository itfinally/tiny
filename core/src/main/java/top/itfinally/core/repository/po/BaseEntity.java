package top.itfinally.core.repository.po;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Id;
import top.itfinally.builder.annotation.MetaData;
import top.itfinally.core.enumerate.DataStatusEnum;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@MetaData
public class BaseEntity<Entity extends BaseEntity> implements Serializable {
    protected String id;
    protected int status;
    protected long createTime;
    protected long updateTime;
    protected long deleteTime;

    {
        id = UUID.randomUUID().toString().replaceAll( "-", "" );

        long now = System.currentTimeMillis();
        createTime = now;
        updateTime = now;

        status = DataStatusEnum.NORMAL.getStatus();
        deleteTime = -1L;
    }

    public BaseEntity() {
    }

    public BaseEntity( String id ) {
        this.id = id;
    }

    @Id
    @Column
    public String getId() {
        return id;
    }

    @SuppressWarnings( "unchecked" )
    public Entity setId( String id ) {
        this.id = id;
        return ( Entity ) this;
    }

    @Column
    public long getCreateTime() {
        return createTime;
    }

    @SuppressWarnings( "unchecked" )
    public Entity setCreateTime( long createTime ) {
        this.createTime = createTime;
        return ( Entity ) this;
    }

    @Column
    public long getUpdateTime() {
        return updateTime;
    }

    @SuppressWarnings( "unchecked" )
    public Entity setUpdateTime( long updateTime ) {
        this.updateTime = updateTime;
        return ( Entity ) this;
    }

    @Column
    public long getDeleteTime() {
        return deleteTime;
    }

    @SuppressWarnings( "unchecked" )
    public Entity setDeleteTime( long deleteTime ) {
        this.deleteTime = deleteTime;
        return ( Entity ) this;
    }

    @Column
    public int getStatus() {
        return status;
    }

    @SuppressWarnings( "unchecked" )
    public Entity setStatus( int status ) {
        this.status = status;
        return ( Entity ) this;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
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
        BaseEntity<?> that = ( BaseEntity<?> ) o;
        return status == that.status &&
                Objects.equals( id, that.id ) &&
                Objects.equals( createTime, that.createTime ) &&
                Objects.equals( updateTime, that.updateTime ) &&
                Objects.equals( deleteTime, that.deleteTime );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, status, createTime, updateTime, deleteTime );
    }
}
