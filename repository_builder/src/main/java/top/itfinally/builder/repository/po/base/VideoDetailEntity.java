package top.itfinally.builder.repository.po.base;

public class VideoDetailEntity extends BaseEntity<VideoDetailEntity> {
    private String title;
    private String description;
    private String address;
    private String tags;// 标签
    private UserDetailEntity user;
    private VideoCategoryEntity category;
    private ResourceEntity largeThumb;
    private ResourceEntity resource;// 视频文件
    private int play;
    private int sysPlay;
    private int share;
    private int sysShare;
    private int comment;
    private int likes;
    private int sysLikes;
    private int auditStatus;// 审核状态：AuditStatus

    private long videoTime;
    private int width;
    private int height;

    public String getTitle() {
        return title;
    }

    public VideoDetailEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VideoDetailEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public VideoDetailEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public VideoDetailEntity setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public UserDetailEntity getUser() {
        return user;
    }

    public VideoDetailEntity setUser(UserDetailEntity user) {
        this.user = user;
        return this;
    }

    public VideoCategoryEntity getCategory() {
        return category;
    }

    public VideoDetailEntity setCategory(VideoCategoryEntity category) {
        this.category = category;
        return this;
    }

    public ResourceEntity getLargeThumb() {
        return largeThumb;
    }

    public VideoDetailEntity setLargeThumb(ResourceEntity largeThumb) {
        this.largeThumb = largeThumb;
        return this;
    }

    public ResourceEntity getResource() {
        return resource;
    }

    public VideoDetailEntity setResource(ResourceEntity resource) {
        this.resource = resource;
        return this;
    }

    public int getPlay() {
        return play;
    }

    public VideoDetailEntity setPlay(int play) {
        this.play = play;
        return this;
    }

    public int getSysPlay() {
        return sysPlay;
    }

    public VideoDetailEntity setSysPlay(int sysPlay) {
        this.sysPlay = sysPlay;
        return this;
    }

    public int getShare() {
        return share;
    }

    public VideoDetailEntity setShare(int share) {
        this.share = share;
        return this;
    }

    public int getSysShare() {
        return sysShare;
    }

    public VideoDetailEntity setSysShare(int sysShare) {
        this.sysShare = sysShare;
        return this;
    }

    public int getComment() {
        return comment;
    }

    public VideoDetailEntity setComment(int comment) {
        this.comment = comment;
        return this;
    }

    public int getLikes() {
        return likes;
    }

    public VideoDetailEntity setLikes(int likes) {
        this.likes = likes;
        return this;
    }

    public int getSysLikes() {
        return sysLikes;
    }

    public VideoDetailEntity setSysLikes(int sysLikes) {
        this.sysLikes = sysLikes;
        return this;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public VideoDetailEntity setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
        return this;
    }

    public long getVideoTime() {
        return videoTime;
    }

    public VideoDetailEntity setVideoTime(long videoTime) {
        this.videoTime = videoTime;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public VideoDetailEntity setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public VideoDetailEntity setHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        return "VideoDetailEntity{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", tags='" + tags + '\'' +
                ", user=" + user +
                ", category=" + category +
                ", largeThumb=" + largeThumb +
                ", resource=" + resource +
                ", play=" + play +
                ", sysPlay=" + sysPlay +
                ", share=" + share +
                ", sysShare=" + sysShare +
                ", comment=" + comment +
                ", likes=" + likes +
                ", sysLikes=" + sysLikes +
                ", auditStatus=" + auditStatus +
                ", videoTime=" + videoTime +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
