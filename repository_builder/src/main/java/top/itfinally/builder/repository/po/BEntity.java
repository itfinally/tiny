package top.itfinally.builder.repository.po;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.MetaData;
import top.itfinally.builder.annotation.Table;

@MetaData
@Table( name = "t_b" )
public class BEntity extends BaseEntity {
    @Column
    private int b;

    public int getB() {
        return b;
    }

    public BEntity setB( int b ) {
        this.b = b;
        return this;
    }
}
