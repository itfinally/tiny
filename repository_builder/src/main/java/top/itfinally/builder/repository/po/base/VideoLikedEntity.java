package top.itfinally.builder.repository.po.base;

public class VideoLikedEntity extends BaseEntity<VideoLikedEntity> {
    private UserDetailEntity user;
    private VideoDetailEntity video;
    private int kind;

    public UserDetailEntity getUser() {
        return user;
    }

    public VideoLikedEntity setUser(UserDetailEntity user) {
        this.user = user;
        return this;
    }

    public VideoDetailEntity getVideo() {
        return video;
    }

    public VideoLikedEntity setVideo(VideoDetailEntity video) {
        this.video = video;
        return this;
    }

    public int getKind() {
        return kind;
    }

    public VideoLikedEntity setKind(int kind) {
        this.kind = kind;
        return this;
    }
}
