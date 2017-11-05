package top.itfinally.builder.repository.po.base;

public class VideoShareHistoryEntity extends BaseEntity<VideoShareHistoryEntity> {
    private UserDetailEntity user;
    private VideoDetailEntity video;
    private int type;

    public UserDetailEntity getUser() {
        return user;
    }

    public VideoShareHistoryEntity setUser(UserDetailEntity user) {
        this.user = user;
        return this;
    }

    public VideoDetailEntity getVideo() {
        return video;
    }

    public VideoShareHistoryEntity setVideo(VideoDetailEntity video) {
        this.video = video;
        return this;
    }

    public int getType() {
        return type;
    }

    public VideoShareHistoryEntity setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "VideoShareHistoryEntity{" +
                "user=" + user +
                ", video=" + video +
                ", type=" + type +
                '}';
    }
}
