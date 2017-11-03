package top.itfinally.builder.repository.po.ttt;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.MetaData;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.repository.po.BEntity;
import top.itfinally.builder.repository.po.BaseEntity;

import java.util.Date;

@Table
@MetaData
public class CEntity extends BEntity {
    private long c;

    public long getC() {
        return c;
    }

    @Column
    public CEntity setC( long c ) {
        this.c = c;
        return this;
    }
}
