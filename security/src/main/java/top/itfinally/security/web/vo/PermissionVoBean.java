package top.itfinally.security.web.vo;

import top.itfinally.core.vo.BaseVoBean;
import top.itfinally.security.repository.po.PermissionEntity;

public class PermissionVoBean extends BaseVoBean<PermissionVoBean> {
    private String name;
    private String description;

    public PermissionVoBean() {
    }

    public PermissionVoBean( PermissionEntity entity ) {
        super( entity );

        this.name = entity.getName();
        this.description = entity.getDescription();
    }

    public String getName() {
        return name;
    }

    public PermissionVoBean setName( String name ) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PermissionVoBean setDescription( String description ) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "PermissionVoBean{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", description='" + description + '\'' +
                ", updateTime=" + updateTime +
                ", deleteTime=" + deleteTime +
                '}';
    }
}
