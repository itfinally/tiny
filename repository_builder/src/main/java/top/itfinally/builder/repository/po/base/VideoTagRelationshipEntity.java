package top.itfinally.builder.repository.po.base;

public class VideoTagRelationshipEntity extends BaseEntity<VideoTagRelationshipEntity> {
    private VideoDetailEntity video;
    private VideoTagEntity tag;
    private UserDetailEntity user;

    public VideoDetailEntity getVideo() {
        return video;
    }

    public VideoTagRelationshipEntity setVideo(VideoDetailEntity video) {
        this.video = video;
        return this;
    }

    public VideoTagEntity getTag() {
        return tag;
    }

    public VideoTagRelationshipEntity setTag(VideoTagEntity tag) {
        this.tag = tag;
        return this;
    }

    public UserDetailEntity getUser() {
        return user;
    }

    public VideoTagRelationshipEntity setUser(UserDetailEntity user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return "VideoTagRelationshipEntity{" +
                "video=" + video +
                ", tag=" + tag +
                ", user=" + user +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
