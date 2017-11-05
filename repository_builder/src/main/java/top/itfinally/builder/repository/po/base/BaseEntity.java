package top.itfinally.builder.repository.po.base;


import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Id;
import top.itfinally.builder.annotation.MetaData;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@MetaData
@SuppressWarnings( "unchecked" )
public class BaseEntity<T extends BaseEntity> implements Serializable {
    protected String id;
    protected int status;
    protected String sortNo;

    protected Date createTime;
    protected Date deleteTime;
    protected Date updateTime;

    {
        // default initialize
        id = UUID.randomUUID().toString().replace( "-", "" );
        status = 0;

        Date now = new Date();
        createTime = now;
        updateTime = now;
    }

    @Id
    @Column
    public String getId() {
        return id;
    }

    public T setId(String id) {
        this.id = id;
        return ( T ) this;
    }

    @Column
    public int getStatus() {
        return status;
    }

    public T setStatus(int status) {
        this.status = status;
        return ( T ) this;
    }

    @Column
    public String getSortNo() {
        return sortNo;
    }

    public T setSortNo(String sortNo) {
        this.sortNo = sortNo;
        return ( T ) this;
    }

    @Column
    public Date getCreateTime() {
        return createTime;
    }

    public T setCreateTime(Date createTime) {
        this.createTime = createTime;
        return ( T ) this;
    }

    @Column
    public Date getDeleteTime() {
        return deleteTime;
    }

    public T setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
        return ( T ) this;
    }

    @Column
    public Date getUpdateTime() {
        return updateTime;
    }

    public T setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return ( T ) this;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
