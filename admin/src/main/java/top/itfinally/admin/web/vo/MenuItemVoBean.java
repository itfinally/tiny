package top.itfinally.admin.web.vo;

import top.itfinally.admin.repository.po.MenuItemEntity;
import top.itfinally.core.vo.BaseVoBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuItemVoBean extends BaseVoBean<MenuItemVoBean> {
  private String name;
  private String path;

  private boolean isLeaf;
  private boolean isRoot;

  private List<MenuItemVoBean> childes = new ArrayList<>();

  public MenuItemVoBean() {
  }

  public MenuItemVoBean( MenuItemEntity menuItem ) {
    super( menuItem );
    this.name = menuItem.getName();
    this.path = menuItem.getPath();
    this.isLeaf = menuItem.isLeaf();
    this.isRoot = menuItem.isRoot();
  }

  public String getName() {
    return name;
  }

  public MenuItemVoBean setName( String name ) {
    this.name = name;
    return this;
  }

  public String getPath() {
    return path;
  }

  public MenuItemVoBean setPath( String path ) {
    this.path = path;
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
