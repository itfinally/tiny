package top.itfinally.builder.repository.po.auth;

import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.repository.po.base.BaseEntity;

@Table( name = "security_authority" )
public class AuthorityEntity extends BaseEntity<AuthorityEntity> {
    private String name;
    private String url;

    @Column
    public String getName() {
        return name;
    }

    public AuthorityEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Column
    public String getUrl() {
        return url;
    }

    public AuthorityEntity setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return "AuthorityEntity{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
