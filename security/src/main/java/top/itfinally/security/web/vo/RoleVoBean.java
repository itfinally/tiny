package top.itfinally.security.web.vo;

import top.itfinally.core.vo.BaseVoBean;
import top.itfinally.security.repository.po.RoleEntity;

public class RoleVoBean extends BaseVoBean<RoleVoBean> {
    private String name;
    private String description;

    public RoleVoBean() {
    }

    public RoleVoBean( RoleEntity entity ) {
        super( entity );

        this.name = entity.getName();
        this.description = entity.getDescription();
    }

    public String getName() {
        return name;
    }

    public RoleVoBean setName( String name ) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RoleVoBean setDescription( String description ) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "RoleVoBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleteTime=" + deleteTime +
                '}';
    }
}
