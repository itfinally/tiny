package top.itfinally.builder.entity;

import java.util.List;
import java.util.Objects;

public class TableMetaData {
    private boolean isMeta;
    private boolean isTable;

    private String tableName;

    private EntityMetaData thisEntity;
    private EntityMetaData extendEntity;
    private List<ColumnMetaData> columns;

    public boolean isMeta() {
        return isMeta;
    }

    public TableMetaData setMeta( boolean meta ) {
        isMeta = meta;
        return this;
    }

    public boolean isTable() {
        return isTable;
    }

    public TableMetaData setTable( boolean table ) {
        isTable = table;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public TableMetaData setTableName( String tableName ) {
        this.tableName = tableName;
        return this;
    }

    public EntityMetaData getThisEntity() {
        return thisEntity;
    }

    public TableMetaData setThisEntity( EntityMetaData thisEntity ) {
        this.thisEntity = thisEntity;
        return this;
    }

    public EntityMetaData getExtendEntity() {
        return extendEntity;
    }

    public TableMetaData setExtendEntity( EntityMetaData extendEntity ) {
        this.extendEntity = extendEntity;
        return this;
    }

    public List<ColumnMetaData> getColumns() {
        return columns;
    }

    public TableMetaData setColumns( List<ColumnMetaData> columns ) {
        this.columns = columns;
        return this;
    }

    @Override
    public String toString() {
        return "TableMetaData{" +
                "isMeta=" + isMeta +
                ", isTable=" + isTable +
                ", tableName='" + tableName + '\'' +
                ", thisEntity=" + thisEntity +
                ", extendEntity=" + extendEntity +
                ", columns=" + columns +
                '}';
    }
}
