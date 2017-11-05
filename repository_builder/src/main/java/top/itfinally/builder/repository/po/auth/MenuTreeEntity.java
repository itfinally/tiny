package top.itfinally.builder.repository.po.auth;


import top.itfinally.builder.repository.po.base.BaseEntity;

import java.util.List;

public class MenuTreeEntity extends BaseEntity<MenuTreeEntity> {
    private MenuEntity menu;
    private MenuTreeEntity parent;
    private List<MenuTreeEntity> childrens;

    public MenuTreeEntity(MenuEntity menu, MenuTreeEntity parent) {
        this.menu = menu;
        this.parent = parent;
    }

    public MenuEntity getMenu() {
        return menu;
    }

    public MenuTreeEntity setMenu(MenuEntity menu) {
        this.menu = menu;
        return this;
    }

    public MenuTreeEntity getParent() {
        return parent;
    }

    public MenuTreeEntity setParent(MenuTreeEntity parent) {
        this.parent = parent;
        return this;
    }

    public List<MenuTreeEntity> getChildrens() {
        return childrens;
    }

    public MenuTreeEntity setChildrens(List<MenuTreeEntity> childrens) {
        this.childrens = childrens;
        return this;
    }

    @Override
    public String toString() {
        return "MenuTreeEntity{" +
                "menu=" + menu +
                ", parent=" + parent +
                ", childrens=" + childrens +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
