package top.itfinally.builder.bootstrap;

import java.io.File;
import java.net.URL;

public class BuilderConfigure {
  private String scanPackage;
  private String packageName;
  private String targetFolder;
  private String entityEndWith = "Entity";

  private Class<?> abstractDaoCls;
  private Class<?> baseMapperCls;
  private Class<?> baseEntity;
  private Class<?> timeUnit = long.class;

  private boolean forceCreation = true;
  private boolean mapUnderscoreToCamelCase = true;

  public String getScanPackage() {
    return scanPackage;
  }

  public BuilderConfigure setScanPackage( String scanPackage ) {
    this.scanPackage = scanPackage;
    return this;
  }

  public String getPackageName() {
    return packageName;
  }

  public BuilderConfigure setPackageName( String packageName ) {
    this.packageName = packageName;
    return this;
  }

  public String getTargetFolder() {
    return targetFolder;
  }

  public BuilderConfigure setTargetFolder( String targetFolder ) {
    this.targetFolder = targetFolder;
    return this;
  }

  public String getEntityEndWith() {
    return entityEndWith;
  }

  public BuilderConfigure setEntityEndWith( String entityEndWith ) {
    this.entityEndWith = entityEndWith;
    return this;
  }

  public Class<?> getTimeUnit() {
    return timeUnit;
  }

  public BuilderConfigure setTimeUnit( Class<?> timeUnit ) {
    this.timeUnit = timeUnit;
    return this;
  }

  public Class<?> getAbstractDaoCls() {
    return abstractDaoCls;
  }

  public BuilderConfigure setAbstractDaoCls( Class<?> abstractDaoCls ) {
    this.abstractDaoCls = abstractDaoCls;
    return this;
  }

  public Class<?> getBaseMapperCls() {
    return baseMapperCls;
  }

  public BuilderConfigure setBaseMapperCls( Class<?> baseMapperCls ) {
    this.baseMapperCls = baseMapperCls;
    return this;
  }

  public Class<?> getBaseEntity() {
    return baseEntity;
  }

  public BuilderConfigure setBaseEntity( Class<?> baseEntity ) {
    this.baseEntity = baseEntity;
    return this;
  }

  public boolean isForceCreation() {
    return forceCreation;
  }

  public BuilderConfigure setForceCreation( boolean forceCreation ) {
    this.forceCreation = forceCreation;
    return this;
  }

  public boolean isMapUnderscoreToCamelCase() {
    return mapUnderscoreToCamelCase;
  }

  public BuilderConfigure setMapUnderscoreToCamelCase( boolean mapUnderscoreToCamelCase ) {
    this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    return this;
  }

  public void checking() {
    this.checkingBaseEntity()
        .checkingTargetFolder()
        .checkingPackage( scanPackage, "scanPackage" )
        .checkingPackage( packageName, "packageName" );
  }

  private BuilderConfigure checkingBaseEntity() {
    if ( null == baseEntity ) {
      throw new NullPointerException( "BaseEntity is not found." );
    }

    return this;
  }

  private BuilderConfigure checkingTargetFolder() {
    File file = new File( targetFolder );
    if ( file.exists() && !file.isDirectory() ) {
      throw new IllegalArgumentException( String.format( "Path %s is not a directory.", targetFolder ) );
    }

    return this;
  }

  private BuilderConfigure checkingPackage( String name, String field ) {
    URL url = Thread.currentThread()
        .getContextClassLoader()
        .getResource( name.replaceAll( "\\.", File.separator ) );

    if ( null == url ) {
      throw new IllegalArgumentException( String.format( "Package '%s' not available, check your '%s' in config.", name, field ) );
    }

    if ( !new File( url.getPath() ).isDirectory() ) {
      throw new IllegalArgumentException( String.format( "Package '%s' is not a directory, check your '%s' in config.", name, field ) );
    }

    return this;
  }
}
