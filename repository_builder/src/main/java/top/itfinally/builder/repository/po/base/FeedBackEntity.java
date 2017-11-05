package top.itfinally.builder.repository.po.base;

import top.itfinally.builder.annotation.Table;

public class FeedBackEntity extends BaseEntity<FeedBackEntity> {
    private UserDetailEntity user;
    private String content;

    public UserDetailEntity getUser() {
        return user;
    }

    public FeedBackEntity setUser(UserDetailEntity user) {
        this.user = user;
        return this;
    }

    public String getContent() {
        return content;
    }

    public FeedBackEntity setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return "FeedBackEntity{" +
                "user=" + user +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
