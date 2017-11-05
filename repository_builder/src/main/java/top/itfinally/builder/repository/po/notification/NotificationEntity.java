package top.itfinally.builder.repository.po.notification;


import top.itfinally.builder.repository.po.base.BaseEntity;
import top.itfinally.builder.repository.po.base.UserDetailEntity;
import top.itfinally.builder.repository.po.base.VideoDetailEntity;

public class NotificationEntity extends BaseEntity<NotificationEntity> {
    private UserDetailEntity to;
    private UserDetailEntity from;
    private NotificationTypeEntity type;
    private VideoDetailEntity video;
    private String description;
    private boolean isPush;

    public UserDetailEntity getTo() {
        return to;
    }

    public NotificationEntity setTo( UserDetailEntity to ) {
        this.to = to;
        return this;
    }

    public UserDetailEntity getFrom() {
        return from;
    }

    public NotificationEntity setFrom( UserDetailEntity from ) {
        this.from = from;
        return this;
    }

    public NotificationTypeEntity getType() {
        return type;
    }

    public NotificationEntity setType( NotificationTypeEntity type ) {
        this.type = type;
        return this;
    }

    public VideoDetailEntity getVideo() {
        return video;
    }

    public NotificationEntity setVideo( VideoDetailEntity video ) {
        this.video = video;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public NotificationEntity setDescription( String description ) {
        this.description = description;
        return this;
    }

    public boolean isPush() {
        return isPush;
    }

    public NotificationEntity setPush( boolean push ) {
        isPush = push;
        return this;
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "id='" + id + '\'' +
                ", to=" + to +
                ", status=" + status +
                ", from=" + from +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", type=" + type +
                ", deleteTime=" + deleteTime +
                ", video=" + video +
                ", updateTime=" + updateTime +
                ", description='" + description + '\'' +
                ", isPush=" + isPush +
                '}';
    }
}
