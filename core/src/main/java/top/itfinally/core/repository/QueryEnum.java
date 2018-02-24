package top.itfinally.core.repository;

public enum QueryEnum {
  NOT_PAGING( -1000, "取消分页" ),
  NOT_STATUS_FLAG( -1001, "取消状态" ),
  NOT_DATE_LIMIT( -1002, "取消时间限制" );

  private int val;
  private String name;

  QueryEnum( int val, String name ) {
    this.val = val;
    this.name = name;
  }

  public int getVal() {
    return val;
  }

  public QueryEnum setVal( int val ) {
    this.val = val;
    return this;
  }

  public String getName() {
    return name;
  }

  public QueryEnum setName( String name ) {
    this.name = name;
    return this;
  }
}
