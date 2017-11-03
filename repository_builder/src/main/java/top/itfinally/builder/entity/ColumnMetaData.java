package top.itfinally.builder.entity;

import java.util.Objects;

public class ColumnMetaData {
    private String column;
    private String property;
    private Class<?> javaType;
    private Class<?> collection;
    private Class<?> association;

    public ColumnMetaData() {
    }

    public ColumnMetaData( String property, String column ) {
        this.property = property;
        this.column = column;
    }

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

    public Class<?> getCollection() {
        return collection;
    }

    public ColumnMetaData setCollection( Class<?> collection ) {
        this.collection = collection;
        return this;
    }

    public Class<?> getAssociation() {
        return association;
    }

    public ColumnMetaData setAssociation( Class<?> association ) {
        this.association = association;
        return this;
    }
}
