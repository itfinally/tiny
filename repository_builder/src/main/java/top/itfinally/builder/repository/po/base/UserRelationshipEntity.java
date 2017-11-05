package top.itfinally.builder.repository.po.base;

public class UserRelationshipEntity extends BaseEntity<UserRelationshipEntity> {
    private UserDetailEntity u1;
    private UserDetailEntity u2;
    private int kind;

    public UserDetailEntity getU1() {
        return u1;
    }

    public UserRelationshipEntity setU1(UserDetailEntity u1) {
        this.u1 = u1;
        return this;
    }

    public UserDetailEntity getU2() {
        return u2;
    }

    public UserRelationshipEntity setU2(UserDetailEntity u2) {
        this.u2 = u2;
        return this;
    }

    public int getKind() {
        return kind;
    }

    public UserRelationshipEntity setKind(int kind) {
        this.kind = kind;
        return this;
    }

    @Override
    public String toString() {
        return "UserRelationshipEntity{" +
                "u1=" + u1 +
                ", u2=" + u2 +
                ", kind=" + kind +
                '}';
    }
}
