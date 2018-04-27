package top.itfinally.security.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.util.StringUtils.isEmpty
import org.springframework.web.bind.annotation.*
import top.itfinally.core.EntityStatus
import top.itfinally.core.web.BasicResponse
import top.itfinally.core.web.ListResponse
import top.itfinally.core.web.ResponseStatus.ILLEGAL_REQUEST
import top.itfinally.security.repository.entity.DepartmentEntity
import top.itfinally.security.repository.entity.PermissionEntity
import top.itfinally.security.repository.entity.RoleEntity
import top.itfinally.security.service.DepartmentService
import top.itfinally.security.service.PermissionService
import top.itfinally.security.service.RoleService
import top.itfinally.security.web.vo.PermissionVoBean
import top.itfinally.security.web.vo.RoleVoBean

@RestController
@RequestMapping("/permission")
open class PermissionController {

  @Autowired
  private
  lateinit var permissionService: PermissionService

  @PostMapping("/add_permission")
  @PreAuthorize("hasPermission(null, 'permission_write')")
  open fun addPermission(@RequestParam("name") name: String,
                         @RequestParam("description", required = false, defaultValue = "") description: String,
                         @RequestParam("status") status: Int): BasicResponse.It {

    if (isEmpty(name) || isEmpty(description)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require name and description.")
    }

    if (!EntityStatus.existByCodeIs(status)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require status.")
    }

    return permissionService.addPermission(PermissionEntity().setName(name).setDescription(description).setStatus(status))
  }

  @DeleteMapping("/remove_permission/{permissionId}")
  @PreAuthorize("hasPermission(null, 'permission_write')")
  open fun removePermission(@PathVariable("permissionId") permissionId: String): BasicResponse.It {
    if (isEmpty(permissionId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission id.")
    }

    return permissionService.removePermission(permissionId)
  }

  @GetMapping("/query_own_permissions")
  open fun queryOwnPermissions(): ListResponse<PermissionVoBean> {
    return permissionService.queryOwnPermissions()
  }
}

@RestController
@RequestMapping("/role")
open class RoleController {

  @Autowired
  private
  lateinit var roleService: RoleService

  @PostMapping("/add_role")
  @PreAuthorize("hasPermission(null, 'role_write')")
  open fun addRole(@RequestParam("name") name: String, @RequestParam("description") description: String,
                   @RequestParam("priority") priority: Int,
                   @RequestParam("status") status: Int): BasicResponse.It {

    if (isEmpty(name) || isEmpty(description)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require name and description.")
    }

    if (priority <= 0) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Priority must greater than zero.")
    }

    if (!EntityStatus.existByCodeIs(status)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require status.")
    }

    return roleService.addRole(RoleEntity().setName(name).setDescription(description).setPriority(priority).setStatus(status))
  }

  @DeleteMapping("/remove_role/{roleId}")
  @PreAuthorize("hasPermission(null, 'role_write')")
  open fun removeRole(@PathVariable("roleId") roleId: String): BasicResponse.It {
    if (isEmpty(roleId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role id.")
    }

    return roleService.removeRoleByIdIs(roleId)
  }

  @PostMapping("/add_permissions_to_role/{roleId}")
  @PreAuthorize("hasPermission(null, 'grant')")
  open fun addPermissionsToRole(@PathVariable("roleId") roleId: String, @RequestBody permissionsIds: List<String>?): BasicResponse.It {
    if (isEmpty(roleId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role id.")
    }

    if (null == permissionsIds || permissionsIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission ids.")
    }

    return roleService.addPermissionsToRole(roleId, permissionsIds)
  }

  @DeleteMapping("/remove_permissions_from_role/{roleId}")
  @PreAuthorize("hasPermission(null, 'grant')")
  open fun removePermissionsFromRole(@PathVariable("roleId") roleId: String, @RequestBody permissionsIds: List<String>?): BasicResponse.It {
    if (isEmpty(roleId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role id.")
    }

    if (null == permissionsIds || permissionsIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission ids.")
    }

    return roleService.removePermissionsFromRole(roleId, permissionsIds)
  }

  @GetMapping("/query_permissions_by_role_id_is/{roleId}")
  open fun queryPermissionsByRoleIdIs(@PathVariable("roleId") roleId: String): ListResponse<PermissionVoBean> {
    if (isEmpty(roleId)) {
      return ListResponse<PermissionVoBean>(ILLEGAL_REQUEST).setMessage("Require role id.")
    }

    return roleService.queryPermissionsByRoleIdIs(roleId)
  }

  @GetMapping("/query_available_assign_roles")
  open fun queryAvailableAssignRoles(): ListResponse<RoleVoBean> {
    return roleService.queryAvailableAssignRoles()
  }

  @GetMapping("/query_own_roles")
  open fun queryOwnRoles(): ListResponse<RoleVoBean> {
    return roleService.queryOwnRoles()
  }
}

@RestController
@RequestMapping("/department")
open class DepartmentController {

  @Autowired
  private lateinit var departmentService: DepartmentService

  @PostMapping("/add_department")
  @PreAuthorize("hasPermission(null, 'department_write')")
  open fun addDepartment(@RequestParam("name") name: String,
                    @RequestParam("description", required = false, defaultValue = "") description: String,
                    @RequestParam("status") status: Int): BasicResponse.It {
    if (isEmpty(name)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require name and description.")
    }

    if (!EntityStatus.existByCodeIs(status)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require status.")
    }

    return departmentService.addDepartment(DepartmentEntity().setName(name).setDescription(description).setStatus(status))
  }

  @DeleteMapping("/remove_department/{departmentId}")
  @PreAuthorize("hasPermission(null, 'department_write')")
  open fun removeDepartment(@RequestParam("departmentId") departmentId: String): BasicResponse.It {
    if (isEmpty(departmentId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require department id.")
    }

    return departmentService.removeDepartmentByIdIs(departmentId)
  }

  @PostMapping("/add_roles_to_department/{departmentId}")
  @PreAuthorize("hasPermission(null, 'grant')")
  open fun addRolesToDepartment(@PathVariable("departmentId") departmentId: String, @RequestBody roleIds: List<String>?): BasicResponse.It {
    if (isEmpty(departmentId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require department id.")
    }

    if (null == roleIds || roleIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role ids.")
    }

    return departmentService.addRolesToDepartment(departmentId, roleIds)
  }

  @DeleteMapping("/remove_roles_from_department/{departmentId}")
  @PreAuthorize("hasPermission(null, 'grant')")
  open fun removeRolesFromDepartment(@PathVariable("departmentId") departmentId: String, @RequestBody roleIds: List<String>?): BasicResponse.It {
    if (isEmpty(departmentId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require department id.")
    }

    if (null == roleIds || roleIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role ids.")
    }

    return departmentService.removeRolesFromDepartment(departmentId, roleIds)
  }

  @GetMapping("/query_roles_by_department_id_is/{departmentId}")
  open fun queryRolesByDepartmentIdIs(@PathVariable("departmentId") departmentId: String): ListResponse<RoleEntity> {
    if (isEmpty(departmentId)) {
      return ListResponse<RoleEntity>(ILLEGAL_REQUEST).setMessage("Require department id.")
    }

    return departmentService.queryRolesByDepartmentIdIs(departmentId)
  }
}