package top.itfinally.security.component

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.isEmpty
import top.itfinally.core.EntityStatus
import top.itfinally.core.repository.BasicQuerySituation
import top.itfinally.security.RolePermissionRefreshEvent
import top.itfinally.security.repository.*
import top.itfinally.security.repository.entity.AbstractUserDetail
import top.itfinally.security.repository.entity.PermissionEntity
import top.itfinally.security.repository.entity.UserSecurityEntity
import java.io.Serializable
import java.lang.Runtime.getRuntime
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors.toList
import java.util.stream.Stream

abstract class BasicUserSecurityComponent<Entity : AbstractUserDetail<Entity>> : UserDetailsService {

  private lateinit var departmentRoleRepository: DepartmentRoleRepository
  private lateinit var userDepartmentRepository: UserDepartmentRepository
  private lateinit var userSecurityRepository: UserSecurityRepository
  private lateinit var userRoleRepository: UserRoleRepository

  @Autowired
  fun departmentRoleRepository(departmentRoleRepository: DepartmentRoleRepository) {
    this.departmentRoleRepository = departmentRoleRepository
  }

  @Autowired
  fun userDepartmentRepository(userDepartmentRepository: UserDepartmentRepository) {
    this.userDepartmentRepository = userDepartmentRepository
  }

  @Autowired
  fun userSecurityRepository(userSecurityRepository: UserSecurityRepository) {
    this.userSecurityRepository = userSecurityRepository
  }

  @Autowired
  fun userRoleRepository(userRoleRepository: UserRoleRepository) {
    this.userRoleRepository = userRoleRepository
  }

  override fun loadUserByUsername(username: String?): UserDetails {
    if (null == username || username.isBlank()) {
      throw UsernameNotFoundException("Failed to login, user name is empty.")
    }

    @Suppress("UNCHECKED_CAST")
    val entity = loadUserByAccount(username) as? Entity
        ?: throw throw UsernameNotFoundException("Failed to login, not match for this user.")


    if (isEmpty(entity.userSecurityId)) {
      throw UsernameNotFoundException("Failed to login, user security id is empty.")
    }

    val userSecurity = userSecurityRepository.queryByIdIs(entity.userSecurityId)
        ?: throw UsernameNotFoundException("Failed to login, not match for this user.")

    if (!userSecurity.isNonLocked) {
      throw LockedException(String.format("Account '%s' has been locked.", username))
    }

    val personalRoles = userRoleRepository.queryRolesByUserSecurityIdIs(userSecurity.id, BasicQuerySituation.Builder().build())
    val departments = userDepartmentRepository.queryDepartmentsByUserSecurityIdIs(userSecurity.id, BasicQuerySituation.Builder().build())
    val departmentRoles = departmentRoleRepository.queryRolesByDepartmentIdIn(departments.map { it.id }, BasicQuerySituation.Builder().build())

    return userSecurity.UserSecurityDelegateEntity<Entity>(entity, Stream.concat(personalRoles.stream(), departmentRoles.stream()).distinct().collect(toList()))
  }

  abstract fun loadUserByAccount(username: String): AbstractUserDetail<Entity>?
}

@Component
class PermissionValidationComponent : PermissionEvaluator {

  @Autowired
  lateinit var rolePermissionRepository: RolePermissionRepository

  private val logger = LoggerFactory.getLogger(javaClass)
  private val rolePermissionMappers: LoadingCache<String, Set<String>> = CacheBuilder.newBuilder()
      .concurrencyLevel(getRuntime().availableProcessors())
      .expireAfterWrite(5, TimeUnit.DAYS)
      .initialCapacity(64)
      .maximumSize(4096)
      .build(object : CacheLoader<String, Set<String>>() {
        override fun load(roleId: String?): Set<String> {
          if (null == roleId || roleId.isBlank()) {
            return setOf()
          }

          return rolePermissionRepository.queryPermissionsByRoleIdIs(roleId)
              .filter { it.status == EntityStatus.NORMAL.code }.map { it.name }.toSet()
        }
      })

  override fun hasPermission(authentication: Authentication, targetDomainObject: Any?, permission: Any?): Boolean {
    if ("anonymousUser" == authentication.principal) {
      return false
    }

    if (null == permission || permission.toString().isBlank()) {
      throw IllegalArgumentException("Permission must not be null, Please check your code!")
    }

    val security = authentication.principal as UserSecurityEntity.UserSecurityDelegateEntity<*>

    return try {
      security.getAuthorities().any {
        return@any if (UserSecurityHolder.getContext().isAdmin) true
        else rolePermissionMappers.get(it.id).any { permission.toString().equals(it, true) }
      }

    } catch (exp: Exception) {
      logger.error("Interrupted by exception.", exp)
      false
    }
  }

  // Do not use this method
  override fun hasPermission(authentication: Authentication?, targetId: Serializable?, targetType: String?, permission: Any?): Boolean {
    return false
  }

  @Subscribe
  fun refreshRolePermission(event: RolePermissionRefreshEvent) {
    rolePermissionMappers.refresh(event.roleId)
  }
}