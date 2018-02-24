package top.itfinally.builder.entity;

import java.util.List;

public class TableInfo {
  private boolean isMeta;
  private boolean isTable;

  private String tableName;
  private ColumnInfo idColumn;

  private EntityInfo thisEntity;
  private EntityInfo extendEntity;
  private List<ColumnInfo> columnInfoList;

  public TableInfo() {
  }

  public TableInfo( TableInfo tableInfo ) {
    this.isMeta = tableInfo.isMeta;
    this.isTable = tableInfo.isTable;
    this.idColumn = tableInfo.idColumn;
    this.tableName = tableInfo.tableName;
    this.columnInfoList = tableInfo.columnInfoList;

    this.thisEntity = new EntityInfo( tableInfo.thisEntity );
    this.extendEntity = null == tableInfo.extendEntity ? null : new EntityInfo( tableInfo.extendEntity );
  }

  public boolean isMeta() {
    return isMeta;
  }

  public TableInfo setMeta( boolean meta ) {
    isMeta = meta;
    return this;
  }

  public boolean isTable() {
    return isTable;
  }

  public TableInfo setTable( boolean table ) {
    isTable = table;
    return this;
  }

  public String getTableName() {
    return tableName;
  }

  public TableInfo setTableName( String tableName ) {
    this.tableName = tableName;
    return this;
  }

  public ColumnInfo getIdColumn() {
    return idColumn;
  }

  public TableInfo setIdColumn( ColumnInfo idColumn ) {
    this.idColumn = idColumn;
    return this;
  }

  public EntityInfo getThisEntity() {
    return thisEntity;
  }

  public TableInfo setThisEntity( EntityInfo thisEntity ) {
    this.thisEntity = thisEntity;
    return this;
  }

  public EntityInfo getExtendEntity() {
    return extendEntity;
  }

  public TableInfo setExtendEntity( EntityInfo extendEntity ) {
    this.extendEntity = extendEntity;
    return this;
  }

  public List<ColumnInfo> getColumnInfoList() {
    return columnInfoList;
  }

  public TableInfo setColumnInfoList( List<ColumnInfo> columnInfoList ) {
    this.columnInfoList = columnInfoList;
    return this;
  }
}
