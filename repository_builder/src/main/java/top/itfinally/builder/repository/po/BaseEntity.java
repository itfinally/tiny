package top.itfinally.builder.repository.po;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.MetaData;

@MetaData
public class BaseEntity {
    private int id;

    @Column
    public int getId() {
        return id;
    }

    public BaseEntity setId( int id ) {
        this.id = id;
        return this;
    }
}
