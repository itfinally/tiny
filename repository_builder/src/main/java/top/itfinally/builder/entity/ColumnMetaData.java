package top.itfinally.builder.entity;

import java.util.Objects;

public class ColumnMetaData {
    private String column;
    private String property;
    private Class<?> javaType;

    private boolean isId;
    private String joinKey;
    private String joinType;

    public String getColumn() {
        return column;
    }

    public ColumnMetaData setColumn( String column ) {
        this.column = column;
        return this;
    }

    public String getProperty() {
        return property;
    }

    public ColumnMetaData setProperty( String property ) {
        this.property = property;
        return this;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public ColumnMetaData setJavaType( Class<?> javaType ) {
        this.javaType = javaType;
        return this;
    }

    public boolean isId() {
        return isId;
    }

    public ColumnMetaData setId( boolean id ) {
        isId = id;
        return this;
    }

    public String getJoinKey() {
        return joinKey;
    }

    public ColumnMetaData setJoinKey( String joinKey ) {
        this.joinKey = joinKey;
        return this;
    }

    public String getJoinType() {
        return joinType;
    }

    public ColumnMetaData setJoinType( String joinType ) {
        this.joinType = joinType;
        return this;
    }
}
