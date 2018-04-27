package top.itfinally.console.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils.isEmpty
import top.itfinally.console.repository.MenuItemRepository
import top.itfinally.console.repository.MenuQuerySituation
import top.itfinally.console.repository.MenuRelationRepository
import top.itfinally.console.repository.RoleMenuRepository
import top.itfinally.console.repository.entity.MenuItemEntity
import top.itfinally.console.web.vo.MenuItemVoBean
import top.itfinally.core.web.BasicResponse
import top.itfinally.core.web.ListResponse
import top.itfinally.core.web.ResponseStatus.*
import top.itfinally.core.web.SingleResponse
import top.itfinally.security.repository.entity.RoleEntity
import top.itfinally.security.repository.entity.UserSecurityEntity

@Service
open class MenuService {

  @Autowired
  private lateinit var menuItemRepository: MenuItemRepository

  @Autowired
  private lateinit var roleMenuRepository: RoleMenuRepository

  @Autowired
  private lateinit var menuRelationRepository: MenuRelationRepository

  open fun addMenu(menu: MenuItemEntity, parentId: String): SingleResponse<MenuItemVoBean> {
    val localMenu = menuItemRepository.save(menu, parentId)

    return if (null == localMenu) SingleResponse<MenuItemVoBean>(ILLEGAL_REQUEST).setMessage("Menu name ${menu.name} already exists.")
    else SingleResponse<MenuItemVoBean>(SUCCESS).setResult(MenuItemVoBean(localMenu))
  }

  open fun updateMenu(menuId: String, name: String, path: String): BasicResponse.It {
    val menu = menuItemRepository.queryByIdIs(menuId)
        ?: return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Menu is not found.")

    if (menu.name != name) {
      if (!menuItemRepository.existByNameIs(name)) menu.name = name
      else return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Menu name $name already exists.")
    }

    if (menu.isLeaf && isEmpty(path)) {
      return BasicResponse.It(ILLEGAL_REQUEST).setMessage("Require menu path.")
    }

    menuItemRepository.update(menu.setPath(path))
    return BasicResponse.It(SUCCESS)
  }

  open fun getMenus(): ListResponse<MenuItemVoBean> {
    val roles = getCurrentOperatorRoles()
    if (roles.isEmpty()) {
      return ListResponse(EMPTY_RESULT)
    }

    val isAdmin = roles.any { it.name.equals("admin", true) }

    val roleMenuItems = if (isAdmin) setOf()
    else roleMenuRepository.queryMenusByRoleIdIn(roles.map { it.id }).mapNotNull { it.id }.toSet()

    if (roleMenuItems.isEmpty() && !isAdmin) {
      return ListResponse(EMPTY_RESULT)
    }

    val rootMenuItems = menuItemRepository.queryRootMenuItems()
    val menuItemFilter = fun(menuItemId: String): Boolean { return if (isAdmin) true else roleMenuItems.contains(menuItemId) }
    val menuItemMapping = breadthTraversal(rootMenuItems, menuItemFilter)

    return ListResponse<MenuItemVoBean>().setResult(rootMenuItems.mapNotNull { menuItemMapping[it.id] }.sortedBy { it.name })
  }

  // Combine all menu item to tree.
  private fun breadthTraversal(currentRound: List<MenuItemEntity>, hasMenuItem: (String) -> Boolean,
                               relations: MutableMap<String, MenuItemVoBean> = mutableMapOf()): MutableMap<String, MenuItemVoBean> {

    val nextRound = mutableListOf<MenuItemEntity>()

    currentRound.forEach {
      if (!hasMenuItem(it.id)) {
        return@forEach
      }

      when {
        it.isRoot -> relations[it.id] = MenuItemVoBean(it)
        relations.containsKey(it.id) -> {
          val that = it

          // In here, previous round of children turn mapping itId-parent to itId-it.
          // Then the round of children can found they parent.
          relations[it.id] = relations[it.id]!!.childes.single { it.id == that.id }

        }
        else -> throw IllegalStateException("Menu item '${it.name}' has been broken.")
      }

      val childes = menuRelationRepository.queryByParentIdIs(it.id, MenuQuerySituation().setDirect(true)).filter { it.gap > 0 }

      if (childes.isEmpty()) {
        return@forEach
      }

      val parent = relations[it.id]!!
      childes.forEach innerForEach@ {
        if (!hasMenuItem(it.child.id)) {
          return@innerForEach
        }

        parent.childes.add(MenuItemVoBean(it.child))

        // In here, relation is mapping as childId-parent
        relations[it.child.id] = parent
      }

      nextRound.addAll(childes.map { it.child }.filter { !it.isLeaf })
    }

    return if (nextRound.isEmpty()) relations else breadthTraversal(nextRound, hasMenuItem, relations)
  }

  private fun getCurrentOperatorRoles(): List<RoleEntity> {
    val delegateSecurity = SecurityContextHolder.getContext().authentication
        .principal as? UserSecurityEntity.UserSecurityDelegateEntity<*> ?: return listOf()

    return delegateSecurity.getRoleEntities()
  }

  fun removeAllMenuByIdIn(menuIds: List<String>): BasicResponse.It {
    menuItemRepository.removeAllByIdIn(menuIds)
    return BasicResponse.It(SUCCESS)
  }

  fun recoverAllMenuByIdIn(menuIds: List<String>): BasicResponse.It {
    menuItemRepository.recoverAllByIdIn(menuIds)
    return BasicResponse.It(SUCCESS)
  }
}

@Service
open class RoleMenuService {
  @Autowired
  private
  lateinit var roleMenuRepository: RoleMenuRepository

  fun addRolesToMenuItem(menuId: String, roleIds: List<String>): BasicResponse.It {
    roleMenuRepository.addRolesToMenuItem(menuId, roleIds)
    return BasicResponse.It(SUCCESS)
  }

  fun removeRolesFromMenuItem(menuId: String, roleIds: List<String>): BasicResponse.It {
    roleMenuRepository.removeRolesFromMenuItem(menuId, roleIds)
    return BasicResponse.It(SUCCESS)
  }
}