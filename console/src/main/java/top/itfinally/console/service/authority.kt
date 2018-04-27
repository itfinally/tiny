package top.itfinally.console.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import top.itfinally.console.repository.*
import top.itfinally.core.EntityStatus.*
import top.itfinally.core.web.BasicResponse
import top.itfinally.core.web.ListResponse
import top.itfinally.core.web.ResponseStatus.*
import top.itfinally.core.web.SingleResponse
import top.itfinally.security.component.UserSecurityHolder
import top.itfinally.security.web.vo.DepartmentVoBean
import top.itfinally.security.web.vo.PermissionVoBean
import top.itfinally.security.web.vo.RoleVoBean
import java.lang.System.currentTimeMillis

@Service
class PermissionServiceExtended {

  @Autowired
  private lateinit var permissionRepository: PermissionRepositoryExtended

  fun queryByConditionsIs(conditions: ConditionQuerySituation): ListResponse<PermissionVoBean> {
    val permissions = permissionRepository.queryByConditionsIs(conditions)
    val status = if (permissions.isEmpty()) EMPTY_RESULT else SUCCESS

    return ListResponse<PermissionVoBean>(status).setResult(permissions.map { PermissionVoBean(it) })
  }

  fun countByConditionIs(conditions: ConditionQuerySituation): SingleResponse<Long> {
    val total = permissionRepository.countByConditionsIs(conditions)
    return SingleResponse<Long>(SUCCESS).setResult(total)
  }

  fun recoverByIdIs(permissionId: String): BasicResponse.It {
    permissionRepository.recoverByIdIs(permissionId)
    return BasicResponse.It(SUCCESS)
  }

  fun removeAllByIdIn(permissionIds: List<String>): BasicResponse.It {
    permissionRepository.removeAllByIdIn(permissionIds)
    return BasicResponse.It(SUCCESS)
  }

  fun recoverAllByIdIn(permissionIds: List<String>): BasicResponse.It {
    permissionRepository.recoverAllByIdIn(permissionIds)
    return BasicResponse.It(SUCCESS)
  }

  fun update(id: String, status: Int, name: String, description: String): BasicResponse.It {
    val entity = permissionRepository.queryByIdIs(id)
        ?: return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Illegal permission id.")

    val now = currentTimeMillis()
    entity.setStatus(status).setName(name).description = description

    when (status) {
      NORMAL.code -> entity.setUpdateTime(now).deleteTime = -1
      DELETE.code -> entity.setUpdateTime(now).deleteTime = now
      else -> return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Illegal status.")
    }

    permissionRepository.update(entity)
    return BasicResponse.It(SUCCESS)
  }
}

@Service
class RoleServiceExtended {

  @Autowired
  private lateinit var roleRepository: RoleRepositoryExtended

  @Autowired
  private lateinit var roleMenuRepository: RoleMenuRepository

  fun queryByConditionsIs(conditions: ConditionQuerySituation): ListResponse<RoleVoBean> {
    val roles = roleRepository.queryByConditionsIs(conditions)
    val status = if (roles.isEmpty()) EMPTY_RESULT else SUCCESS

    return ListResponse<RoleVoBean>(status).setResult(roles.map { RoleVoBean(it) })
  }

  fun countByConditionIs(conditions: ConditionQuerySituation): SingleResponse<Long> {
    val total = roleRepository.countByConditionsIs(conditions)
    return SingleResponse<Long>(SUCCESS).setResult(total)
  }

  fun recoverByIdIs(roleIds: String): BasicResponse.It {
    roleRepository.recoverByIdIs(roleIds)
    return BasicResponse.It(SUCCESS)
  }

  fun removeAllByIdIn(roleIds: List<String>): BasicResponse.It {
    roleRepository.removeAllByIdIn(roleIds)
    return BasicResponse.It(SUCCESS)
  }

  fun recoverAllByIdIn(roleIds: List<String>): BasicResponse.It {
    roleRepository.recoverAllByIdIn(roleIds)
    return BasicResponse.It(SUCCESS)
  }

  fun update(id: String, status: Int, name: String, description: String, priority: Int): BasicResponse.It {
    val entity = roleRepository.queryByIdIs(id)
        ?: return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Illegal role id.")

    if (!"admin".equals(entity.name, true) && 0 == priority) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Only admin can have a priority of zero.")
    }

    if (UserSecurityHolder.getContext().priorityLowerThan(entity.priority)) {
      throw AccessDeniedException("Can not update for a higher priority role.")
    }

    val now = currentTimeMillis()
    entity.setStatus(status).setName(name).setPriority(priority).description = description

    when (status) {
      NORMAL.code -> entity.setUpdateTime(now).deleteTime = -1
      DELETE.code -> entity.setUpdateTime(now).deleteTime = now
      else -> return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Illegal status.")
    }

    roleRepository.update(entity)
    return BasicResponse.It(SUCCESS)
  }

  fun queryRolesByMenuIdIs(menuId: String): ListResponse<RoleVoBean> {
    val roles = roleMenuRepository.queryRolesByMenuItemIdIs(menuId)
    val status = if (roles.isEmpty()) EMPTY_RESULT else SUCCESS

    return ListResponse<RoleVoBean>(status).setResult(roles.map { RoleVoBean(it) })
  }
}

@Service
class DepartmentServiceExtended {

  @Autowired
  private lateinit var departmentRepository: DepartmentRepositoryExtended

  fun queryByConditionsIs(conditions: ConditionQuerySituation): ListResponse<DepartmentVoBean> {
    val departments = departmentRepository.queryByConditionsIs(conditions)
    val status = if (departments.isEmpty()) EMPTY_RESULT else SUCCESS

    return ListResponse<DepartmentVoBean>(status).setResult(departments.map { DepartmentVoBean(it) })
  }

  fun countByConditionIs(conditions: ConditionQuerySituation): SingleResponse<Long> {
    val total = departmentRepository.countByConditionsIs(conditions)
    return SingleResponse<Long>(SUCCESS).setResult(total)
  }

  fun recoverByIdIs(departmentId: String): BasicResponse.It {
    departmentRepository.recoverByIdIs(departmentId)
    return BasicResponse.It(SUCCESS)
  }

  fun removeAllByIdIn(departmentIds: List<String>): BasicResponse.It {
    departmentRepository.removeAllByIdIn(departmentIds)
    return BasicResponse.It(SUCCESS)
  }

  fun recoverAllByIdIn(departmentIds: List<String>): BasicResponse.It {
    departmentRepository.recoverAllByIdIn(departmentIds)
    return BasicResponse.It(SUCCESS)
  }

  fun update(id: String, status: Int, name: String, description: String): BasicResponse.It {
    val entity = departmentRepository.queryByIdIs(id)
        ?: return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Illegal department id.")

    val now = currentTimeMillis()
    entity.setStatus(status).setName(name).description = description

    when (status) {
      NORMAL.code -> entity.setUpdateTime(now).deleteTime = -1
      DELETE.code -> entity.setUpdateTime(now).deleteTime = now
      else -> return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Illegal status.")
    }

    departmentRepository.update(entity)
    return BasicResponse.It(SUCCESS)
  }
}