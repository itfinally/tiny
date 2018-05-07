package top.itfinally.console.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.util.StringUtils.isEmpty
import org.springframework.web.bind.annotation.*
import top.itfinally.console.repository.AccessLogQuerySituation
import top.itfinally.console.repository.ConditionQuerySituation
import top.itfinally.console.repository.entity.MenuItemEntity
import top.itfinally.console.service.*
import top.itfinally.console.web.vo.AccessLogVoBean
import top.itfinally.console.web.vo.MenuItemVoBean
import top.itfinally.core.EntityStatus
import top.itfinally.core.web.BasicResponse
import top.itfinally.core.web.ListResponse
import top.itfinally.core.web.ResponseStatus.ILLEGAL_REQUEST
import top.itfinally.core.web.SingleResponse
import top.itfinally.security.web.vo.DepartmentVoBean
import top.itfinally.security.web.vo.PermissionVoBean
import top.itfinally.security.web.vo.RoleVoBean

@RestController
@RequestMapping("/menu")
class MenuController {

  @Autowired
  private lateinit var menuService: MenuService

  @Autowired
  private lateinit var roleMenuService: RoleMenuService

  @GetMapping("/get_menus")
  fun getMenus(): ListResponse<MenuItemVoBean> {
    return menuService.getMenus()
  }

  @DeleteMapping("/remove_menus")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  fun removeMenus(@RequestBody menuIds: List<String>?): BasicResponse.It {
    if (null == menuIds || menuIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require menu ids.")
    }

    return menuService.removeAllMenuByIdIn(menuIds)
  }

  @PostMapping("/recover_menus")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  fun recoverMenus(@RequestBody menuIds: List<String>?): BasicResponse.It {
    if (null == menuIds || menuIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require menu ids.")
    }

    return menuService.recoverAllMenuByIdIn(menuIds)
  }

  @PostMapping("/add_root_menu")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  fun addRootMenu(@RequestParam("name") name: String, @RequestParam("path") path: String,
                       @RequestParam("isLeaf") isLeaf: Boolean): SingleResponse<MenuItemVoBean> {
    if (isEmpty(name)) {
      return SingleResponse<MenuItemVoBean>(ILLEGAL_REQUEST).setMessage("Require menu name")
    }

    if (isLeaf && isEmpty(path)) {
      return SingleResponse<MenuItemVoBean>(ILLEGAL_REQUEST).setMessage("Require menu path")
    }

    val menu = MenuItemEntity().setName(name).setPath(if (isLeaf) path else "").setRoot(true).setLeaf(isLeaf)
    return menuService.addMenu(menu, menu.id)
  }

  @PostMapping("/add_menu")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  fun addMenu(@RequestParam("name") name: String, @RequestParam("path") path: String,
                   @RequestParam("parentId") parentId: String, @RequestParam("isLeaf") isLeaf: Boolean): SingleResponse<MenuItemVoBean> {
    if (isEmpty(name)) {
      return SingleResponse<MenuItemVoBean>(ILLEGAL_REQUEST).setMessage("Require menu name")
    }

    if (isEmpty(parentId)) {
      return SingleResponse<MenuItemVoBean>(ILLEGAL_REQUEST).setMessage("Require parent id")
    }

    if (isLeaf && isEmpty(path)) {
      return SingleResponse<MenuItemVoBean>(ILLEGAL_REQUEST).setMessage("Require menu path")
    }

    val menu = MenuItemEntity().setName(name).setPath(if (isLeaf) path else "").setRoot(false).setLeaf(isLeaf)
    return menuService.addMenu(menu, parentId)
  }

  @PostMapping("/add_roles_to_menu/{menuId}")
  @PreAuthorize("hasPermission(null, 'grant')")
  fun addRolesToMenu(@PathVariable("menuId") menuId: String, @RequestBody roleIds: List<String>?): BasicResponse.It {
    if (isEmpty(menuId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require menu id.")
    }

    if (null == roleIds || roleIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role ids.")
    }

    return roleMenuService.addRolesToMenuItem(menuId, roleIds)
  }

  @DeleteMapping("/remove_roles_from_menu/{menuId}")
  @PreAuthorize("hasPermission(null, 'grant')")
  fun removeRolesFromMenu(@PathVariable("menuId") menuId: String, @RequestBody roleIds: List<String>?): BasicResponse.It {
    if (isEmpty(menuId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require menu id.")
    }

    if (null == roleIds || roleIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role ids.")
    }

    return roleMenuService.removeRolesFromMenuItem(menuId, roleIds)
  }

  @PostMapping("/update_menu")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  fun updateMenu(@RequestParam("menuId") menuId: String, @RequestParam("name") name: String,
                      @RequestParam("path") path: String): BasicResponse.It {
    if (isEmpty(menuId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require menu id.")
    }

    if (isEmpty(name)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require menu name.")
    }

    return menuService.updateMenu(menuId, name, path)
  }
}

@RestController
@RequestMapping("/permission")
class PermissionControllerExtended {

  @Autowired
  private lateinit var permissionService: PermissionServiceExtended

  @PostMapping("/query_by_conditions_is")
  @PreAuthorize("hasPermission(null, 'permission_read')")
  fun queryByConditionsIs(@RequestBody conditions: Map<String, Any>): ListResponse<PermissionVoBean> {
    return permissionService.queryByConditionsIs(ConditionQuerySituation.Builder(conditions).build())
  }

  @PostMapping("/count_by_conditions_is")
  @PreAuthorize("hasPermission(null, 'permission_read')")
  fun countByConditionsIs(@RequestBody conditions: Map<String, Any>): SingleResponse<Long> {
    return permissionService.countByConditionIs(ConditionQuerySituation.Builder(conditions).build())
  }

  @PostMapping("/recover_by_id_is/{permissionId}")
  @PreAuthorize("hasPermission(null, 'permission_write')")
  fun recoverByIdIs(@PathVariable("permissionId") permissionId: String): BasicResponse.It {
    if (isEmpty(permissionId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission id.")
    }

    return permissionService.recoverByIdIs(permissionId)
  }

  @DeleteMapping("/remove_all_by_id_in")
  @PreAuthorize("hasPermission(null, 'permission_write')")
  fun removeAllByIdIn(@RequestBody permissionIds: List<String>?): BasicResponse.It {
    if (null == permissionIds || permissionIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission ids.")
    }

    return permissionService.removeAllByIdIn(permissionIds)
  }

  @PostMapping("/recover_all_by_id_in")
  @PreAuthorize("hasPermission(null, 'permission_write')")
  fun recoverAllByIdIn(@RequestBody permissionIds: List<String>?): BasicResponse.It {
    if (null == permissionIds || permissionIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission ids.")
    }

    return permissionService.recoverAllByIdIn(permissionIds)
  }

  @PostMapping("/update/{permissionId}")
  @PreAuthorize("hasPermission(null, 'permission_write')")
  fun update(@PathVariable("permissionId") id: String,
                  @RequestParam("status") status: Int,
                  @RequestParam("name") name: String,
                  @RequestParam("description") description: String): BasicResponse.It {
    if (isEmpty(id)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission id.")
    }

    if (!EntityStatus.existByCodeIs(status)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require status.")
    }

    if (isEmpty(name)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission name.")
    }

    return permissionService.update(id, status, name, description)
  }
}

@RestController
@RequestMapping("/role")
class RoleControllerExtended {
  @Autowired
  private lateinit var roleService: RoleServiceExtended

  @PostMapping("/query_by_conditions_is")
  @PreAuthorize("hasPermission(null, 'role_read')")
  fun queryByConditionsIs(@RequestBody conditions: Map<String, Any>): ListResponse<RoleVoBean> {
    return roleService.queryByConditionsIs(ConditionQuerySituation.Builder(conditions).build())
  }

  @PostMapping("/count_by_conditions_is")
  @PreAuthorize("hasPermission(null, 'role_read')")
  fun countByConditionsIs(@RequestBody conditions: Map<String, Any>): SingleResponse<Long> {
    return roleService.countByConditionIs(ConditionQuerySituation.Builder(conditions).build())
  }

  @PostMapping("/recover_by_id_is/{roleId}")
  @PreAuthorize("hasPermission(null, 'role_write')")
  fun recoverByIdIs(@PathVariable("roleId") roleId: String): BasicResponse.It {
    if (isEmpty(roleId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission id.")
    }

    return roleService.recoverByIdIs(roleId)
  }

  @DeleteMapping("/remove_all_by_id_in")
  @PreAuthorize("hasPermission(null, 'role_write')")
  fun removeAllByIdIn(@RequestBody roleIds: List<String>?): BasicResponse.It {
    if (null == roleIds || roleIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission ids.")
    }

    return roleService.removeAllByIdIn(roleIds)
  }

  @PostMapping("/recover_all_by_id_in")
  @PreAuthorize("hasPermission(null, 'role_write')")
  fun recoverAllByIdIn(@RequestBody roleIds: List<String>?): BasicResponse.It {
    if (null == roleIds || roleIds.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require permission ids.")
    }

    return roleService.recoverAllByIdIn(roleIds)
  }

  @PostMapping("/d/{roleId}")
  @PreAuthorize("hasPermission(null, 'role_write')")
  fun update(@PathVariable("roleId") id: String,
                  @RequestParam("status") status: Int,
                  @RequestParam("name") name: String,
                  @RequestParam("description") description: String,
                  @RequestParam("priority") priority: Int): BasicResponse.It {
    if (isEmpty(id)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role id.")
    }

    if (!EntityStatus.existByCodeIs(status)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require status.")
    }

    if (isEmpty(name)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require role name.")
    }

    if (priority < 0) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require priority.")
    }

    return roleService.update(id, status, name, description, priority)
  }

  @GetMapping("/query_roles_by_menu_id_is/{menuId}")
  fun queryRolesByMenuIdIs(@PathVariable("menuId") menuId: String): ListResponse<RoleVoBean> {
    if (isEmpty(menuId)) {
      return ListResponse<RoleVoBean>(ILLEGAL_REQUEST).setMessage("Require menu id.")
    }

    return roleService.queryRolesByMenuIdIs(menuId)
  }
}

@RestController
@RequestMapping("/department")
class DepartmentControllerExtended {
  @Autowired
  private lateinit var departmentService: DepartmentServiceExtended

  @PostMapping("/query_by_conditions_is")
  @PreAuthorize("hasPermission(null, 'department_read')")
  fun queryByConditionsIs(@RequestBody conditions: Map<String, Any>): ListResponse<DepartmentVoBean> {
    return departmentService.queryByConditionsIs(ConditionQuerySituation.Builder(conditions).build())
  }

  @PostMapping("/count_by_conditions_is")
  @PreAuthorize("hasPermission(null, 'department_read')")
  fun countByConditionsIs(@RequestBody conditions: Map<String, Any>): SingleResponse<Long> {
    return departmentService.countByConditionIs(ConditionQuerySituation.Builder(conditions).build())
  }

  @PostMapping("/recover_by_id_is/{departmentId}")
  @PreAuthorize("hasPermission(null, 'department_write')")
  fun recoverByIdIs(@PathVariable("departmentId") departmentId: String): BasicResponse.It {
    if (isEmpty(departmentId)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require department id.")
    }

    return departmentService.recoverByIdIs(departmentId)
  }

  @DeleteMapping("/remove_all_by_id_in")
  @PreAuthorize("hasPermission(null, 'department_write')")
  fun removeAllByIdIn(@RequestBody departmentId: List<String>?): BasicResponse.It {
    if (null == departmentId || departmentId.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require department ids.")
    }

    return departmentService.removeAllByIdIn(departmentId)
  }

  @PostMapping("/recover_all_by_id_in")
  @PreAuthorize("hasPermission(null, 'department_write')")
  fun recoverAllByIdIn(@RequestBody departmentId: List<String>?): BasicResponse.It {
    if (null == departmentId || departmentId.isEmpty()) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require department ids.")
    }

    return departmentService.recoverAllByIdIn(departmentId)
  }

  @PostMapping("/update/{departmentId}")
  @PreAuthorize("hasPermission(null, 'department_write')")
  fun update(@PathVariable("departmentId") id: String,
                  @RequestParam("status") status: Int,
                  @RequestParam("name") name: String,
                  @RequestParam("description") description: String): BasicResponse.It {
    if (isEmpty(id)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require department id.")
    }

    if (!EntityStatus.existByCodeIs(status)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require status.")
    }

    if (isEmpty(name)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require department name.")
    }

    return departmentService.update(id, status, name, description)
  }
}

@RestController
@RequestMapping("/access_log")
class AccessLogController {
  @Autowired
  private lateinit var accessLogService: AccessLogService

  @PostMapping("/query_by_conditions_is")
  @PreAuthorize("hasPermission(null, 'access_log_read')")
  fun queryByConditionsIs(@RequestBody conditions: Map<String, Any>): ListResponse<AccessLogVoBean> {
    return accessLogService.queryByConditionsIs(AccessLogQuerySituation.Builder(conditions).build())
  }

  @PostMapping("/count_by_conditions_is")
  @PreAuthorize("hasPermission(null, 'access_log_read')")
  fun countByConditionsIs(@RequestBody conditions: Map<String, Any>): SingleResponse<Long> {
    return accessLogService.countByConditionIs(AccessLogQuerySituation.Builder(conditions).build())
  }
}