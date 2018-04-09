package top.itfinally.security.repository.entity;

import org.springframework.security.core.GrantedAuthority;
import top.itfinally.core.repository.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

@Entity
@Table( name = "v1_security_role", uniqueConstraints =
@UniqueConstraint( name = "role_name", columnNames = "name" ) )
public class RoleEntity extends BasicEntity<RoleEntity> {
  private String name;
  private String description = "";

  // 默认值必须为 1, 因为 0 优先级只能是 ADMIN 角色
  private int priority = 1;

  public RoleEntity() {
  }

  public RoleEntity( String id ) {
    super( id );
  }

  @Column( columnDefinition = "varchar(32) not null" )
  public String getName() {
    return name;
  }

  public RoleEntity setName( String name ) {
    this.name = name.toUpperCase();
    return this;
  }

  @Column( columnDefinition = "varchar(512) default ''" )
  public String getDescription() {
    return description;
  }

  public RoleEntity setDescription( String description ) {
    this.description = description;
    return this;
  }

  @Column( columnDefinition = "int(11) not null" )
  public int getPriority() {
    return priority;
  }

  public RoleEntity setPriority( int priority ) {
    this.priority = priority;
    return this;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( o == null || getClass() != o.getClass() ) return false;
    if ( !super.equals( o ) ) return false;
    RoleEntity that = ( RoleEntity ) o;
    return priority == that.priority &&
        Objects.equals( name, that.name ) &&
        Objects.equals( description, that.description );
  }

  @Override
  public int hashCode() {
    return Objects.hash( super.hashCode(), name, description, priority );
  }

  @Override
  public String toString() {
    return "RoleEntity{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", priority=" + priority +
        ", id='" + id + '\'' +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", deleteTime=" + deleteTime +
        ", status=" + status +
        '}';
  }

  public class RoleDelegateEntity implements GrantedAuthority {
    public String getId() {
      return id;
    }

    public long getCreateTime() {
      return createTime;
    }

    public long getUpdateTime() {
      return updateTime;
    }

    public long getDeleteTime() {
      return deleteTime;
    }

    public int getStatus() {
      return status;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    // Use default prefix for security, and ignore serializable
    // Only spring security use it.
    @Override
    public String getAuthority() {
      return "ROLE_" + name.toUpperCase();
    }
  }
}
