package top.itfinally.security.web.vo;

import top.itfinally.core.vo.BaseVoBean;
import top.itfinally.security.repository.po.RolePermissionEntity;

public class RolePermissionVoBean extends BaseVoBean<RolePermissionVoBean> {
    private RoleVoBean role;
    private PermissionVoBean permission;

    public RolePermissionVoBean() {
    }

    public RolePermissionVoBean( RolePermissionEntity entity ) {
        super( entity );

        this.role = new RoleVoBean( entity.getRole() );
        this.permission = new PermissionVoBean( entity.getPermission() );
    }

    public RoleVoBean getRole() {
        return role;
    }

    public RolePermissionVoBean setRole( RoleVoBean role ) {
        this.role = role;
        return this;
    }

    public PermissionVoBean getPermission() {
        return permission;
    }

    public RolePermissionVoBean setPermission( PermissionVoBean permission ) {
        this.permission = permission;
        return this;
    }

    @Override
    public String toString() {
        return "RolePermissionVoBean{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", role=" + role +
                ", updateTime=" + updateTime +
                ", permission=" + permission +
                ", deleteTime=" + deleteTime +
                '}';
    }
}
