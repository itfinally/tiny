package top.itfinally.builder.repository.po.base;

public class UserLogEntity extends BaseEntity<UserLogEntity> {
    private UserDetailEntity user;
    private String content;
    private String kind;
    private String data;
    private String referId;

    public UserDetailEntity getUser() {
        return user;
    }

    public UserLogEntity setUser(UserDetailEntity user) {
        this.user = user;
        return this;
    }

    public String getContent() {
        return content;
    }

    public UserLogEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public String getKind() {
        return kind;
    }

    public UserLogEntity setKind(String kind) {
        this.kind = kind;
        return this;
    }

    public String getData() {
        return data;
    }

    public UserLogEntity setData(String data) {
        this.data = data;
        return this;
    }

    public String getReferId() {
        return referId;
    }

    public UserLogEntity setReferId(String referId) {
        this.referId = referId;
        return this;
    }

    @Override
    public String toString() {
        return "UserLogEntity{" +
                "user=" + user +
                ", content='" + content + '\'' +
                ", kind='" + kind + '\'' +
                ", data='" + data + '\'' +
                ", referId='" + referId + '\'' +
                '}';
    }
}
