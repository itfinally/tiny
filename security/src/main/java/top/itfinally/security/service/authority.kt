package top.itfinally.security.service

import com.google.common.eventbus.EventBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.itfinally.core.EntityStatus
import top.itfinally.core.repository.BasicQuerySituation
import top.itfinally.core.web.BasicResponse
import top.itfinally.core.web.ListResponse
import top.itfinally.core.web.ResponseStatus.*
import top.itfinally.security.AccountChangeEvent
import top.itfinally.security.RolePermissionRefreshEvent
import top.itfinally.security.repository.*
import top.itfinally.security.repository.entity.DepartmentEntity
import top.itfinally.security.repository.entity.PermissionEntity
import top.itfinally.security.repository.entity.RoleEntity
import top.itfinally.security.repository.entity.UserSecurityEntity
import top.itfinally.security.web.vo.PermissionVoBean
import top.itfinally.security.web.vo.RoleVoBean

@Service
open class RoleService {

  @Autowired
  @Qualifier("securityEventBus")
  private
  lateinit var eventBus: EventBus

  @Autowired
  private
  lateinit var roleRepository: RoleRepository

  @Autowired
  private
  lateinit var rolePermissionRepository: RolePermissionRepository

  open fun addRole(entity: RoleEntity): BasicResponse.It {
    return if (roleRepository.existByNameIs(entity.name)) {
      BasicResponse.It(CONFLICT).setMessage("Role '${entity.name}' already exist.")

    } else {
      if (entity.status == EntityStatus.DELETE.code) {
        entity.deleteTime = entity.updateTime
      }

      roleRepository.save(entity)
      BasicResponse.It(SUCCESS)
    }
  }

  open fun removeRoleByIdIs(roleId: String): BasicResponse.It {
    val role = roleRepository.queryByIdIs(roleId)

    return if (null == role || role.status == EntityStatus.DELETE.code) {
      BasicResponse.It(CONFLICT).setMessage("Role is not exist.")

    } else {
      roleRepository.remove(role)
      BasicResponse.It(SUCCESS)
    }
  }

  open fun addPermissionsToRole(roleId: String, permissionIds: List<String>): BasicResponse.It {
    val targetRole = roleRepository.queryByIdIs(roleId)
        ?: return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Illegal role.")

    verifyCurrentOperatorRolesPriority(targetRole.priority)

    rolePermissionRepository.addPermissionsToRole(roleId, permissionIds)
    eventBus.post(RolePermissionRefreshEvent(roleId))

    return BasicResponse.It(SUCCESS)
  }

  open fun removePermissionsFromRole(roleId: String, permissionIds: List<String>): BasicResponse.It {
    val targetRole = roleRepository.queryByIdIs(roleId)
        ?: return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Illegal role.")

    verifyCurrentOperatorRolesPriority(targetRole.priority)

    rolePermissionRepository.removePermissionsFromRole(roleId, permissionIds)
    eventBus.post(RolePermissionRefreshEvent(roleId))

    return BasicResponse.It(SUCCESS)
  }

  open fun queryPermissionsByRoleIdIs(roleId: String): ListResponse<PermissionVoBean> {
    val permissions = rolePermissionRepository.queryPermissionsByRoleIdIs(roleId)
    val status = if (permissions.isEmpty()) EMPTY_RESULT else SUCCESS

    return ListResponse<PermissionVoBean>(status).setResult(permissions.map { PermissionVoBean(it) })
  }

  open fun queryAvailableAssignRoles(): ListResponse<RoleVoBean> {
    val userSecurity = SecurityContextHolder.getContext().authentication.principal
        as? UserSecurityEntity.UserSecurityDelegateEntity<*> ?: return ListResponse(ILLEGAL_REQUEST)

    val maxRolePriority = userSecurity.getRoleEntities().map { it.priority }.max()
        ?: return ListResponse<RoleVoBean>(EMPTY_RESULT).setMessage("This account does not have any role.")

    val roles = roleRepository.queryAllLowerRolesByPriorityIs(maxRolePriority)
    val status = if (roles.isEmpty()) EMPTY_RESULT else SUCCESS

    return ListResponse<RoleVoBean>(status).setResult(roles.map { RoleVoBean(it) })
  }

  protected fun verifyCurrentOperatorRolesPriority(targetPriority: Int) {
    val userSecurity = SecurityContextHolder.getContext().authentication.principal
        as? UserSecurityEntity.UserSecurityDelegateEntity<*>

    if (null == userSecurity || userSecurity.getRoleEntities().map { it.priority }.all { it >= targetPriority }) {
      throw AccessDeniedException("Can not reset permissions for a higher priority role.")
    }
  }
}

@Service
open class PermissionService {

  @Autowired
  private
  lateinit var permissionRepository: PermissionRepository

  @Autowired
  private
  lateinit var rolePermissionRepository: RolePermissionRepository

  open fun addPermission(entity: PermissionEntity): BasicResponse.It {
    return if (permissionRepository.existByName(entity.name)) {
      BasicResponse.It(CONFLICT).setMessage("Permission '${entity.name}' already exist.")

    } else {
      if (entity.status == EntityStatus.DELETE.code) {
        entity.deleteTime = entity.updateTime
      }

      permissionRepository.save(entity)
      BasicResponse.It(SUCCESS)
    }
  }

  open fun removePermissionByIdIs(permissionId: String): BasicResponse.It {
    val permission = permissionRepository.queryByIdIs(permissionId)

    return if (null == permission || permission.status == EntityStatus.DELETE.code) {
      BasicResponse.It(CONFLICT).setMessage("Permission is not exist.")

    } else {
      permissionRepository.remove(permission)
      BasicResponse.It(SUCCESS)
    }
  }

  open fun queryOwnPermissions(): ListResponse<PermissionVoBean> {
    val userSecurity = SecurityContextHolder.getContext().authentication.principal
        as? UserSecurityEntity.UserSecurityDelegateEntity<*> ?: return ListResponse(EMPTY_RESULT)

    val permissions = if (userSecurity.getRoleEntities().any { it.name.equals("admin", true) }) {
      permissionRepository.queryAll(BasicQuerySituation.It())

    } else {
      rolePermissionRepository.queryPermissionsByRoleIdsIn(userSecurity.getRoleEntities().map { it.id })
    }

    val status = if (permissions.isEmpty()) EMPTY_RESULT else SUCCESS
    return ListResponse<PermissionVoBean>(status).setResult(permissions.map { PermissionVoBean(it) })
  }
}


private fun setAccountChange(eventBus: EventBus) {
  val userSecurity = SecurityContextHolder.getContext()
      .authentication.principal as? UserSecurityEntity.UserSecurityDelegateEntity<*>

  if (userSecurity != null) {
    eventBus.post(AccountChangeEvent(userSecurity.getUsername()))
  }
}


@Service
open class DepartmentService {

  @Autowired
  @Qualifier("securityEventBus")
  private
  lateinit var eventBus: EventBus

  @Autowired
  private
  lateinit var departmentRepository: DepartmentRepository

  @Autowired
  private
  lateinit var departmentRoleRepository: DepartmentRoleRepository

  open fun addDepartment(entity: DepartmentEntity): BasicResponse.It {
    return if (departmentRepository.existByName(entity.name)) {
      BasicResponse.It(CONFLICT).setMessage("Department '${entity.name}' already exist.")

    } else {
      if (entity.status == EntityStatus.DELETE.code) {
        entity.deleteTime = entity.updateTime
      }

      departmentRepository.save(entity)
      BasicResponse.It(SUCCESS)
    }
  }

  open fun removeDepartmentByIdIs(departmentId: String): BasicResponse.It {
    val department = departmentRepository.queryByIdIs(departmentId)

    return if (null == department || department.status == EntityStatus.DELETE.code) {
      BasicResponse.It(CONFLICT).setMessage("Department is not exist.")

    } else {
      departmentRepository.remove(department)
      BasicResponse.It(SUCCESS)
    }
  }

  open fun addRolesToDepartment(departmentId: String, roleIds: List<String>): BasicResponse.It {
    departmentRoleRepository.addRolesToDepartment(departmentId, roleIds)
    setAccountChange(eventBus)

    return BasicResponse.It(SUCCESS)
  }

  open fun removeRolesFromDepartment(departmentId: String, roleIds: List<String>): BasicResponse.It {
    departmentRoleRepository.removeRoleFromDepartment(departmentId, roleIds)
    setAccountChange(eventBus)

    return BasicResponse.It(SUCCESS)
  }

  open fun queryRolesByDepartmentIdIs(departmentId: String): ListResponse<RoleEntity> {
    val roles = departmentRoleRepository.queryRolesByDepartmentIdIs(departmentId)
    val status = if (roles.isEmpty()) EMPTY_RESULT else SUCCESS

    return ListResponse<RoleEntity>(status).setResult(roles)
  }
}

@Service
open class UserSecurityService {

  @Autowired
  @Qualifier("securityEventBus")
  private
  lateinit var eventBus: EventBus

  @Autowired
  private
  lateinit var userRoleRepository: UserRoleRepository

  open fun addRolesToUser(userSecurityId: String, roleIds: List<String>): BasicResponse.It {
    userRoleRepository.addRolesToUser(userSecurityId, roleIds)
    setAccountChange(eventBus)

    return BasicResponse.It(SUCCESS)
  }

  open fun removeRolesFromUser(userSecurityId: String, roleIds: List<String>): BasicResponse.It {
    userRoleRepository.removeRolesFromUser(userSecurityId, roleIds)
    setAccountChange(eventBus)

    return BasicResponse.It(SUCCESS)
  }
}