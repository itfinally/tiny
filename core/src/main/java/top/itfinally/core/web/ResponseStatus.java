package top.itfinally.core.web;

public enum ResponseStatus {
  SUCCESS(200, "请求成功"),
  EMPTY_RESULT(204, "请求成功, 但没有数据"),

  BAD_REQUEST(400, "用户请求错误或请求缺少参数"),
  UNAUTHORIZED(401, "未登录"),
  FORBIDDEN(403, "没有权限访问"),
  TOO_MARY_REQUEST(429, "当前 ip 或用户请求过多"),
  CONFLICT(409, "请求的资源存在冲突"),

  // 430 在 aop 异常处理器拦截到 BaseBusinessException 及其子类时使用, 即用户非法操作时使用
  ILLEGAL_REQUEST(430, "用户非法操作, 暂无更多细节"),

  SERVER_ERROR(500, "服务器异常");

  private int code;
  private String message;

  ResponseStatus( int code, String message ) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public ResponseStatus setCode( int code ) {
    this.code = code;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public ResponseStatus setMessage( String message ) {
    this.message = message;
    return this;
  }
}
