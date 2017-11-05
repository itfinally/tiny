package top.itfinally.builder.repository.po.base;

public class VideoPlayHistoryEntity extends BaseEntity<VideoPlayHistoryEntity> {
    private UserDetailEntity user;
    private VideoDetailEntity video;

    public UserDetailEntity getUser() {
        return user;
    }

    public VideoPlayHistoryEntity setUser(UserDetailEntity user) {
        this.user = user;
        return this;
    }

    public VideoDetailEntity getVideo() {
        return video;
    }

    public VideoPlayHistoryEntity setVideo(VideoDetailEntity video) {
        this.video = video;
        return this;
    }

    @Override
    public String toString() {
        return "VideoPlayHistoryEntity{" +
                "user=" + user +
                ", video=" + video +
                '}';
    }
}
