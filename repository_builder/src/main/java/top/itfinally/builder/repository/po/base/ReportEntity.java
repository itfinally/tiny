package top.itfinally.builder.repository.po.base;

public class ReportEntity extends BaseEntity<ReportEntity> {
    private UserDetailEntity from;
    private UserDetailEntity to;
    private String referId;
    private String content;
    private int kind;

    public UserDetailEntity getFrom() {
        return from;
    }

    public ReportEntity setFrom(UserDetailEntity from) {
        this.from = from;
        return this;
    }

    public UserDetailEntity getTo() {
        return to;
    }

    public ReportEntity setTo(UserDetailEntity to) {
        this.to = to;
        return this;
    }

    public String getReferId() {
        return referId;
    }

    public ReportEntity setReferId(String referId) {
        this.referId = referId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ReportEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public int getKind() {
        return kind;
    }

    public ReportEntity setKind(int kind) {
        this.kind = kind;
        return this;
    }

    @Override
    public String toString() {
        return "ReportEntity{" +
                "from=" + from +
                ", to=" + to +
                ", referId='" + referId + '\'' +
                ", content='" + content + '\'' +
                ", kind=" + kind +
                '}';
    }
}
