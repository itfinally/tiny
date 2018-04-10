package top.itfinally.security

import com.google.common.eventbus.EventBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import top.itfinally.security.component.AbstractUserDetailCachingComponent
import top.itfinally.security.component.PermissionValidationComponent
import top.itfinally.security.repository.PermissionRepository
import top.itfinally.security.repository.RoleRepository
import top.itfinally.security.repository.entity.PermissionEntity
import top.itfinally.security.repository.entity.RoleEntity

@Component
open class SecurityInitComponent : ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private
  lateinit var roleRepository: RoleRepository

  @Autowired
  private
  lateinit var permissionRepository: PermissionRepository

  @Transactional
  override fun onApplicationEvent(event: ContextRefreshedEvent) {
    if (roleRepository.existByNameIs("ADMIN")) {
      return
    }

    roleRepository.save(RoleEntity().setName("ADMIN").setDescription("超级管理员").setPriority(0))

    val permissions = mutableListOf<PermissionEntity>()
    permissions.add(PermissionEntity().setName("permission_write").setDescription("权限写权限"))
    permissions.add(PermissionEntity().setName("permission_read").setDescription("权限读权限"))
    permissions.add(PermissionEntity().setName("user_write").setDescription("用户写权限"))
    permissions.add(PermissionEntity().setName("user_read").setDescription("用户读权限"))
    permissions.add(PermissionEntity().setName("role_write").setDescription("角色写权限"))
    permissions.add(PermissionEntity().setName("role_read").setDescription("角色读权限"))
    permissions.add(PermissionEntity().setName("grant").setDescription("授权权限"))

    permissionRepository.saveAll(permissions)
  }
}

@Component
open class EventBusInitComponent : ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  @Qualifier("securityEventBus")
  private
  lateinit var eventBus: EventBus

  @Autowired
  private
  lateinit var permissionValidationComponent: PermissionValidationComponent

  @Autowired
  private
  lateinit var userDetailCachingComponent: AbstractUserDetailCachingComponent

  override fun onApplicationEvent(event: ContextRefreshedEvent) {
    try {
      eventBus.unregister(permissionValidationComponent)
    } catch (ignore: Exception) {
    }

    try {
      eventBus.unregister(userDetailCachingComponent)
    } catch (ignore: Exception) {
    }

    EventManager.register(eventBus, permissionValidationComponent)
    EventManager.register(eventBus, userDetailCachingComponent)
  }
}