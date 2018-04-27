package top.itfinally.console.web.vo;

import top.itfinally.console.repository.entity.AccessLogEntity;
import top.itfinally.core.web.BasicVoBean;

public class AccessLogVoBean extends BasicVoBean<AccessLogVoBean, AccessLogEntity> {
  private String requestMethod;
  private String requestPath;
  private String sourceIp;
  private String username;

  private boolean isException;
  private String result;

  public AccessLogVoBean() {
  }

  public AccessLogVoBean( AccessLogEntity entity ) {
    super( entity );

    requestMethod = entity.getRequestMethod();
    requestPath = entity.getRequestPath();
    sourceIp = entity.getSourceIp();
    username = entity.getUsername();

    isException = entity.isException();
    result = entity.getResult();
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public AccessLogVoBean setRequestMethod( String requestMethod ) {
    this.requestMethod = requestMethod;
    return this;
  }

  public String getRequestPath() {
    return requestPath;
  }

  public AccessLogVoBean setRequestPath( String requestPath ) {
    this.requestPath = requestPath;
    return this;
  }

  public String getSourceIp() {
    return sourceIp;
  }

  public AccessLogVoBean setSourceIp( String sourceIp ) {
    this.sourceIp = sourceIp;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public AccessLogVoBean setUsername( String username ) {
    this.username = username;
    return this;
  }

  public boolean isException() {
    return isException;
  }

  public AccessLogVoBean setException( boolean exception ) {
    isException = exception;
    return this;
  }

  public String getResult() {
    return result;
  }

  public AccessLogVoBean setResult( String result ) {
    this.result = result;
    return this;
  }
}
