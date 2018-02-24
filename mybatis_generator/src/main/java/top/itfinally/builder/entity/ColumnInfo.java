package top.itfinally.builder.entity;

public class ColumnInfo {
  private String column;
  private String property;
  private String javaType;

  private String joinType;
  private String joinEntityName;
  private String joinMapperName;

  public String getColumn() {
    return column;
  }

  public ColumnInfo setColumn( String column ) {
    this.column = column;
    return this;
  }

  public String getProperty() {
    return property;
  }

  public ColumnInfo setProperty( String property ) {
    this.property = property;
    return this;
  }

  public String getJavaType() {
    return javaType;
  }

  public ColumnInfo setJavaType( String javaType ) {
    this.javaType = javaType;
    return this;
  }

  public String getJoinType() {
    return joinType;
  }

  public ColumnInfo setJoinType( String joinType ) {
    this.joinType = joinType;
    return this;
  }

  public String getJoinEntityName() {
    return joinEntityName;
  }

  public ColumnInfo setJoinEntityName( String joinEntityName ) {
    this.joinEntityName = joinEntityName;
    return this;
  }

  public String getJoinMapperName() {
    return joinMapperName;
  }

  public ColumnInfo setJoinMapperName( String joinMapperName ) {
    this.joinMapperName = joinMapperName;
    return this;
  }
}
