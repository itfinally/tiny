package top.itfinally.builder.repository.po.auth;


import top.itfinally.builder.annotation.Association;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.repository.po.base.BaseEntity;

import java.io.Serializable;

@Table( name = "security_menu" )
public class MenuEntity extends BaseEntity<MenuEntity> implements Serializable {
    /**
     * 首先通过 MenuTree 构建出一颗完整的目录树
     * 其次获取用户的角色, 取出所有权限
     * 通过权限获取可用的 MenuEntity
     *
     * 最后迭代树, 检查所有叶节点的权限, 删除没有任何功能的父节点
     */

    private String name;
    private boolean isRoot;
    private boolean isLeaf;
    private AuthorityEntity authority;

    @Column
    public String getName() {
        return name;
    }

    public MenuEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Column
    public boolean isRoot() {
        return isRoot;
    }

    public MenuEntity setRoot(boolean root) {
        isRoot = root;
        return this;
    }

    @Column
    public boolean isLeaf() {
        return isLeaf;
    }

    public MenuEntity setLeaf(boolean leaf) {
        isLeaf = leaf;
        return this;
    }

    @Association( join = AuthorityEntity.class )
    public AuthorityEntity getAuthority() {
        return authority;
    }

    public MenuEntity setAuthority(AuthorityEntity authority) {
        this.authority = authority;
        return this;
    }

    @Override
    public String toString() {
        return "MenuEntity{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", name='" + name + '\'' +
                ", isRoot=" + isRoot +
                ", deleteTime=" + deleteTime +
                ", isLeaf=" + isLeaf +
                ", updateTime=" + updateTime +
                ", authority=" + authority +
                '}';
    }
}
