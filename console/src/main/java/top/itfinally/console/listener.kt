package top.itfinally.console

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import top.itfinally.console.repository.MenuItemRepository
import top.itfinally.console.repository.entity.MenuItemEntity
import top.itfinally.security.repository.PermissionRepository
import top.itfinally.security.repository.entity.PermissionEntity

@Component
class ConsoleInitComponent : ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private lateinit var menuItemRepository: MenuItemRepository

  @Autowired
  private lateinit var permissionRepository: PermissionRepository

  @Transactional
  override fun onApplicationEvent(event: ContextRefreshedEvent) {
    if (menuItemRepository.existByNameIs("系统管理")) {
      return
    }

    val root = MenuItemEntity().setRoot(true).setLeaf(false).setName("系统管理")
    menuItemRepository.save(root, root.id)
    menuItemRepository.save(MenuItemEntity().setRoot(false).setLeaf(true).setName("菜单列表").setPath("/console/security/menu"), root.id)
    menuItemRepository.save(MenuItemEntity().setRoot(false).setLeaf(true).setName("角色列表").setPath("/console/security/role/:metadata?"), root.id)
    menuItemRepository.save(MenuItemEntity().setRoot(false).setLeaf(true).setName("部门列表").setPath("/console/security/department/:metadata?"), root.id)
    menuItemRepository.save(MenuItemEntity().setRoot(false).setLeaf(true).setName("权限列表").setPath("/console/security/permission/:metadata?"), root.id)
    menuItemRepository.save(MenuItemEntity().setRoot(false).setLeaf(true).setName("系统日记").setPath("/console/security/access_log/:metadata?"), root.id)

    permissionRepository.save(PermissionEntity().setName("access_log_read").setDescription("系统日记读权限"))
  }
}