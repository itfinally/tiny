package top.itfinally.builder.entity;

public class EntityMetaData {

    // top.itfinally.builder.entity.EntityMetaData
    private String name;

    // The name
    private String offset;

    // The name without suffix.
    // For example, name: AEntity / suffix: Entity / baseName: A
    private String baseName;

    // EntityMetaData
    private String simpleName;

    private String path;

    // Entity class
    private Class<?> thisCls;

    public String getName() {
        return name;
    }

    public EntityMetaData setName( String name ) {
        this.name = name;
        return this;
    }

    public String getOffset() {
        return offset;
    }

    public EntityMetaData setOffset( String offset ) {
        this.offset = offset;
        return this;
    }

    public String getBaseName() {
        return baseName;
    }

    public EntityMetaData setBaseName( String baseName ) {
        this.baseName = baseName;
        return this;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public EntityMetaData setSimpleName( String simpleName ) {
        this.simpleName = simpleName;
        return this;
    }

    public String getPath() {
        return path;
    }

    public EntityMetaData setPath( String path ) {
        this.path = path;
        return this;
    }

    public Class<?> getThisCls() {
        return thisCls;
    }

    public EntityMetaData setThisCls( Class<?> thisCls ) {
        this.thisCls = thisCls;
        return this;
    }

    @Override
    public String toString() {
        return "EntityMetaData{" +
                "name='" + name + '\'' +
                ", offset='" + offset + '\'' +
                ", baseName='" + baseName + '\'' +
                ", simpleName='" + simpleName + '\'' +
                ", path='" + path + '\'' +
                ", thisCls=" + thisCls +
                '}';
    }
}
