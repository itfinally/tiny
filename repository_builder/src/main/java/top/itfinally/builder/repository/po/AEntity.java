package top.itfinally.builder.repository.po;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;

@Table( name = "t_a" )
public class AEntity extends BaseEntity {
    private String a;

    @Column
    public String getA() {
        return a;
    }

    public void setA( String a ) {
        this.a = a;
    }
}
