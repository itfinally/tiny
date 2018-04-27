package top.itfinally.console.repository.entity;

import top.itfinally.core.repository.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table( name = "v1_access_log" )
public class AccessLogEntity extends BasicEntity<AccessLogEntity> {
  private String requestMethod;
  private String requestPath;
  private String sourceIp;
  private String username;

  private boolean isException;
  private String result;

  @Column( name = "request_method" )
  public String getRequestMethod() {
    return requestMethod;
  }

  public AccessLogEntity setRequestMethod( String requestMethod ) {
    this.requestMethod = requestMethod;
    return this;
  }

  @Column( name = "request_path" )
  public String getRequestPath() {
    return requestPath;
  }

  public AccessLogEntity setRequestPath( String requestPath ) {
    this.requestPath = requestPath;
    return this;
  }

  @Column( name = "source_ip" )
  public String getSourceIp() {
    return sourceIp;
  }

  public AccessLogEntity setSourceIp( String sourceIp ) {
    this.sourceIp = sourceIp;
    return this;
  }

  @Column( name = "username" )
  public String getUsername() {
    return username;
  }

  public AccessLogEntity setUsername( String username ) {
    this.username = username;
    return this;
  }

  @Column( name = "is_exception" )
  public boolean isException() {
    return isException;
  }

  public AccessLogEntity setException( boolean exception ) {
    isException = exception;
    return this;
  }

  @Column( name = "result", columnDefinition = "text" )
  public String getResult() {
    return result;
  }

  public AccessLogEntity setResult( String result ) {
    this.result = result;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    AccessLogEntity that = ( AccessLogEntity ) o;
    return isException == that.isException &&
        Objects.equals( requestMethod, that.requestMethod ) &&
        Objects.equals( requestPath, that.requestPath ) &&
        Objects.equals( sourceIp, that.sourceIp ) &&
        Objects.equals( username, that.username ) &&
        Objects.equals( result, that.result );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), requestMethod, requestPath, sourceIp, username, isException, result );
  }

  @Override
  public String toString() {
    return "AccessLogEntity{" +
        "requestMethod='" + requestMethod + '\'' +
        ", requestPath='" + requestPath + '\'' +
        ", sourceIp='" + sourceIp + '\'' +
        ", username='" + username + '\'' +
        ", isException=" + isException +
        ", result='" + result + '\'' +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }
}
