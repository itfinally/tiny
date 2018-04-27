package top.itfinally.security.component;

import org.springframework.security.core.context.SecurityContextHolder;
import top.itfinally.security.repository.entity.AbstractUserDetail;
import top.itfinally.security.repository.entity.RoleEntity;
import top.itfinally.security.repository.entity.UserSecurityEntity;

import java.util.Collections;
import java.util.List;

public class UserSecurityHolder {
  private static final ThreadLocal<Member> members = new ThreadLocal<>();
  private static final List defaultList = Collections.emptyList();

  private UserSecurityHolder() {
  }

  /**
   * 虽然 priority 直译的意思就是优先级, 但在本系统内其含义是倒转的
   * 即 priority 的值越大, 优先级越低, 反之, 值越小, 优先级最高
   * 因此 priority 为 0 是最高权限, 只能被 ADMIN 角色拥有
   * <p>
   * 以及, 本类只作为一个对角色判断的补充工具, 对于判断是否有某角色, 是否有某权限
   * 应该交给 @PreAuthorize 注解配合 spEl 进行判断, 其中的逻辑在 PermissionValidationComponent 中实现
   */
  public static class Member {
    // 因为 UserSecurityDelegateEntity 没有附带也不可能附带泛型, 导致编译时无法识别, 但不影响运行
    private final UserSecurityEntity.UserSecurityDelegateEntity userSecurity;

    private Member( UserSecurityEntity.UserSecurityDelegateEntity userSecurity ) {
      this.userSecurity = userSecurity;
    }

    @SuppressWarnings( "unchecked" )
    private List<RoleEntity> getRoles() {
      return userSecurity != null ? ( List<RoleEntity> ) userSecurity.getRoleEntities() : defaultList;
    }

    @SuppressWarnings( "unchecked" )
    private List<RoleEntity.RoleDelegateEntity> getAuthorities() {
      return userSecurity != null ? ( List<RoleEntity.RoleDelegateEntity> ) userSecurity.getAuthorities() : defaultList;
    }

    public boolean priorityIsGreaterThan( int priority ) {
      return getRoles().stream().anyMatch( it -> it.getPriority() < priority );
    }

    public boolean priorityIsGreaterOrEqual( int priority ) {
      return getRoles().stream().anyMatch( it -> it.getPriority() <= priority );
    }

    public boolean priorityLowerThan( int priority ) {
      return getRoles().stream().allMatch( it -> it.getPriority() > priority );
    }

    public boolean priorityLowerOrEqual( int priority ) {
      return getRoles().stream().allMatch( it -> it.getPriority() >= priority );
    }

    @SuppressWarnings( "unchecked" )
    public <E extends AbstractUserDetail<E>> UserSecurityEntity.UserSecurityDelegateEntity<E> getUserSecurity() {
      return userSecurity;
    }

    public boolean isAdmin() {
      return getRoles().stream().anyMatch( it -> "admin".equalsIgnoreCase( it.getName() ) );
    }
  }

  public static void removeContext() {
    members.remove();
  }

  public static void initContext() {
    Object target = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UserSecurityEntity.UserSecurityDelegateEntity usd = target instanceof UserSecurityEntity.UserSecurityDelegateEntity
        ? ( UserSecurityEntity.UserSecurityDelegateEntity ) target
        : null;

    members.set( new Member( usd ) );
  }

  public static Member getContext() {
    return members.get();
  }
}
