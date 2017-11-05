package top.itfinally.builder.repository.po.base;

public class VideoUserCommentEntity extends BaseEntity<VideoUserCommentEntity> {
    private VideoDetailEntity video;
    private UserDetailEntity from;
    private UserDetailEntity to;
    private String comment;
    private int replys;

    public VideoDetailEntity getVideo() {
        return video;
    }

    public VideoUserCommentEntity setVideo(VideoDetailEntity video) {
        this.video = video;
        return this;
    }

    public UserDetailEntity getFrom() {
        return from;
    }

    public VideoUserCommentEntity setFrom(UserDetailEntity from) {
        this.from = from;
        return this;
    }

    public UserDetailEntity getTo() {
        return to;
    }

    public VideoUserCommentEntity setTo(UserDetailEntity to) {
        this.to = to;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public VideoUserCommentEntity setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public int getReplys() {
        return replys;
    }

    public VideoUserCommentEntity setReplys(int replys) {
        this.replys = replys;
        return this;
    }

    @Override
    public String toString() {
        return "VideoUserCommentEntity{" +
                "video=" + video +
                ", from=" + from +
                ", to=" + to +
                ", comment='" + comment + '\'' +
                ", replys=" + replys +
                '}';
    }
}
