package top.itfinally.builder.bootstrap;

public class BuilderConfigure {

    // The Builder scanning path
    private String scanBasePackage;

    // Generate with this package name
    private String packageName;

    // Write file to ${rooPath}
    private String rootPath;

    public String getBackScanPath() {
        return backScanPath;
    }

    public BuilderConfigure setBackScanPath( String backScanPath ) {
        this.backScanPath = backScanPath;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public BuilderConfigure setPackageName( String packageName ) {
        this.packageName = packageName;
        return this;
    }

    public String getBaseEntity() {
        return baseEntity;
    }

    public BuilderConfigure setBaseEntity( String baseEntity ) {
        this.baseEntity = baseEntity;
        return this;
    }

    public String getRootPath() {
        return rootPath;
    }

    public BuilderConfigure setRootPath( String rootPath ) {
        this.rootPath = rootPath;
        return this;
    }
}
