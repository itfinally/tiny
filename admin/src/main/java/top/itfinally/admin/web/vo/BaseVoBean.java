package top.itfinally.admin.web.vo;

import top.itfinally.core.repository.po.BaseEntity;

import java.io.Serializable;

public class BaseVoBean implements Serializable {
    private String id;
    private int status;
    private long createTime;
    private long updateTime;
    private long deleteTime;

    public BaseVoBean() {
    }

    public BaseVoBean( BaseEntity<?> entity ) {
        this.id = entity.getId();
        this.status = entity.getStatus();
        this.createTime = entity.getCreateTime();
        this.updateTime = entity.getUpdateTime();
        this.deleteTime = entity.getDeleteTime();
    }

    public String getId() {
        return id;
    }

    public BaseVoBean setId( String id ) {
        this.id = id;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public BaseVoBean setStatus( int status ) {
        this.status = status;
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public BaseVoBean setCreateTime( long createTime ) {
        this.createTime = createTime;
        return this;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public BaseVoBean setUpdateTime( long updateTime ) {
        this.updateTime = updateTime;
        return this;
    }

    public long getDeleteTime() {
        return deleteTime;
    }

    public BaseVoBean setDeleteTime( long deleteTime ) {
        this.deleteTime = deleteTime;
        return this;
    }
}
