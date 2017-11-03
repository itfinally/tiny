package top.itfinally.builder.constant;

public class TemplateKeys {
    private TemplateKeys() {}

    // package name
    public static final String PACKAGE = "package";

    //
    public static final String OFFSET = "offset";

    // time type of entity
    public static final String TIME_TYPE = "timeType";

    // base name, use to generate dao and mapper name,
    // ${baseName}dao / ${baseName}Mapper etc.
    public static final String BASE_NAME = "baseName";

    // entity name, not included package name
    public static final String BASE_ENTITY_NAME = "baseEntityName";

    // entity name, included package name
    public static final String BASE_ENTITY_FULL_NAME = "baseEntityFullName";

    // table name
    public static final String TABLE_NAME = "tableName";

    //
    public static final String EXTEND_BASE_NAME = "extendBaseName";
}
