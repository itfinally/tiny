package top.itfinally.builder.entity;

import java.util.List;

public class TableMetaData {
    private String tableName;
    private String entityName;
    private List<ColumnMetaData> columns;

    private MetaData metaData;

    public TableMetaData() {}

    public TableMetaData( TableMetaData meta, MetaData metaData ) {
        this.tableName = meta.getTableName();
        this.entityName = meta.getEntityName();
        this.columns = meta.getColumns();

        this.metaData = metaData;
    }

    public String getTableName() {
        return tableName;
    }

    public TableMetaData setTableName( String tableName ) {
        this.tableName = tableName;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public TableMetaData setEntityName( String entityName ) {
        this.entityName = entityName;
        return this;
    }

    public List<ColumnMetaData> getColumns() {
        return columns;
    }

    public TableMetaData setColumns( List<ColumnMetaData> columns ) {
        this.columns = columns;
        return this;
    }
}
