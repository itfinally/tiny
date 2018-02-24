package top.itfinally.builder.entity;

import java.util.Arrays;

public class EntityInfo {
  private String name;
  private String simpleName;

  private String offset;

  private String daoName;
  private String mapperName;

  public EntityInfo() {
  }

  public EntityInfo( EntityInfo entityInfo ) {
    this.name = entityInfo.name;
    this.simpleName = entityInfo.simpleName;
    this.offset = entityInfo.offset;
    this.daoName = entityInfo.daoName;
    this.mapperName = entityInfo.mapperName;
  }

  public String getName() {
    return name;
  }

  public EntityInfo setName( String name ) {
    this.name = name;
    return this;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public EntityInfo setSimpleName( String simpleName ) {
    this.simpleName = simpleName;
    return this;
  }

  public String getOffset() {
    return offset;
  }

  public EntityInfo setOffset( String offset ) {
    this.offset = offset;
    return this;
  }

  public String getDaoName() {
    return daoName;
  }

  public EntityInfo setDaoName( String daoName ) {
    this.daoName = daoName;
    return this;
  }

  public String getMapperName() {
    return mapperName;
  }

  public EntityInfo setMapperName( String mapperName ) {
    this.mapperName = mapperName;
    return this;
  }

  public String getDaoSimpleName() {
    String[] items = daoName.split( "\\." );
    return items[ items.length - 1 ];
  }

  public String getMapperSimpleName() {
    String[] items = mapperName.split( "\\." );
    return items[ items.length - 1 ];
  }

  public String getDaoPackage() {
    String[] items = daoName.split( "\\." );
    return String.join( ".", Arrays.copyOf( items, items.length - 1 ) );
  }

  public String getMapperPackage() {
    String[] items = mapperName.split( "\\." );
    return String.join( ".", Arrays.copyOf( items, items.length - 1 ) );
  }
}
