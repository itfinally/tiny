package top.itfinally.admin.web.vo;

import top.itfinally.admin.repository.po.MenuItemEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuItemVoBean implements Serializable {
    private String id;
    private String name;

    private int status;

    private boolean isLeaf;
    private boolean isRoot;

    private List<MenuItemVoBean> childes = new ArrayList<>();

    public MenuItemVoBean() {
    }

    public MenuItemVoBean( MenuItemEntity menuItem ) {
        this.id = menuItem.getId();
        this.name = menuItem.getName();
        this.status = menuItem.getStatus();
        this.isLeaf = menuItem.isLeaf();
        this.isRoot = menuItem.isRoot();
    }

    public String getId() {
        return id;
    }

    public MenuItemVoBean setId( String id ) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MenuItemVoBean setName( String name ) {
        this.name = name;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public MenuItemVoBean setStatus( int status ) {
        this.status = status;
        return this;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public MenuItemVoBean setLeaf( boolean leaf ) {
        isLeaf = leaf;
        return this;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public MenuItemVoBean setRoot( boolean root ) {
        isRoot = root;
        return this;
    }

    public List<MenuItemVoBean> getChildes() {
        return childes;
    }

    public MenuItemVoBean setChildes( List<MenuItemVoBean> childes ) {
        this.childes = childes;
        return this;
    }
}
