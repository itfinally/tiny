package top.itfinally.builder.repository.po.notification;


import top.itfinally.builder.repository.po.base.BaseEntity;

public class NotificationTypeEntity extends BaseEntity<NotificationTypeEntity> {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public NotificationTypeEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public NotificationTypeEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "NotificationTypeEntity{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
