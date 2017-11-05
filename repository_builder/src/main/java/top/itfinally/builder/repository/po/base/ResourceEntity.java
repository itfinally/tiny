package top.itfinally.builder.repository.po.base;

public class ResourceEntity extends BaseEntity<ResourceEntity> {
    private String title;
    private String description;
    private String url;
    private String realPath;
    private String name;
    private int kind;   // 片头,片尾,音乐,照片
    private long size;  // 文件大小

    /** 自定义数据 **/
    private String customData;
    private String bucket;
    private String fileKey;
    private String hash;
    private String mime;

    public String getTitle() {
        return title;
    }

    public ResourceEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ResourceEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ResourceEntity setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getRealPath() {
        return realPath;
    }

    public ResourceEntity setRealPath(String realPath) {
        this.realPath = realPath;
        return this;
    }

    public String getName() {
        return name;
    }

    public ResourceEntity setName(String name) {
        this.name = name;
        return this;
    }

    public int getKind() {
        return kind;
    }

    public ResourceEntity setKind(int kind) {
        this.kind = kind;
        return this;
    }

    public long getSize() {
        return size;
    }

    public ResourceEntity setSize(long size) {
        this.size = size;
        return this;
    }

    public String getCustomData() {
        return customData;
    }

    public ResourceEntity setCustomData(String customData) {
        this.customData = customData;
        return this;
    }

    public String getBucket() {
        return bucket;
    }

    public ResourceEntity setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getFileKey() {
        return fileKey;
    }

    public ResourceEntity setFileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public ResourceEntity setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getMime() {
        return mime;
    }

    public ResourceEntity setMime(String mime) {
        this.mime = mime;
        return this;
    }

    @Override
    public String toString() {
        return "ResourceEntity{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", realPath='" + realPath + '\'' +
                ", name='" + name + '\'' +
                ", kind=" + kind +
                ", size=" + size +
                ", customData='" + customData + '\'' +
                ", bucket='" + bucket + '\'' +
                ", fileKey='" + fileKey + '\'' +
                ", hash='" + hash + '\'' +
                ", mime='" + mime + '\'' +
                '}';
    }
}
