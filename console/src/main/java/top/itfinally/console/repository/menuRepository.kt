package top.itfinally.console.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.itfinally.console.repository.entity.MenuItemEntity
import top.itfinally.console.repository.entity.MenuRelationEntity
import top.itfinally.console.repository.entity.RoleMenuEntity
import top.itfinally.core.BasicRepository
import top.itfinally.core.EntityStatus
import top.itfinally.core.repository.BasicQuerySituation
import top.itfinally.security.repository.entity.RoleEntity
import java.lang.System.currentTimeMillis

@Repository
@Transactional
@Suppress("UNCHECKED_CAST")
class MenuRelationRepository : BasicRepository<MenuRelationEntity>() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByChildIdIs(childId: String): MutableList<MenuRelationEntity> {
    return queryByChildIdIs(childId, MenuQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByChildIdIs(childId: String, situation: MenuQuerySituation): MutableList<MenuRelationEntity> {
    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table).where(builder.equal(table.get<MenuItemEntity>("child").get<String>("id"), childId))

    if (situation.isDirect) {
      runtime.where(builder.equal(table.get<Int>("gap"), 1))
    }

    return withSituation(runtime, situation).resultList
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByParentIdIs(parentId: String): List<MenuRelationEntity> {
    return queryByParentIdIs(parentId, MenuQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByParentIdIs(parentId: String, situation: MenuQuerySituation): List<MenuRelationEntity> {
    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table).where(builder.equal(table.get<MenuItemEntity>("parent").get<String>("id"), parentId))

    if (situation.isDirect) {
      runtime.where(builder.equal(table.get<Int>("gap"), 1))
    }

    return withSituation(runtime, situation).resultList
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryChildesByParentIdIs(parentId: String): List<MenuItemEntity> {
    return queryChildesByParentIdIs(parentId, MenuQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryChildesByParentIdIs(parentId: String, situation: MenuQuerySituation): List<MenuItemEntity> {
    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table.get<MenuItemEntity>("child")).where(builder.equal(table.get<MenuItemEntity>("parent").get<String>("id"), parentId))

    if (situation.isDirect) {
      runtime.where(builder.equal(table.get<Int>("gap"), 1))
    }

    return withSituation(runtime, situation).resultList as List<MenuItemEntity>
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryParentsByChildIdIs(childId: String): List<MenuItemEntity> {
    return queryParentsByChildIdIs(childId, MenuQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryParentsByChildIdIs(childId: String, situation: MenuQuerySituation): List<MenuItemEntity> {
    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table.get<MenuItemEntity>("parent")).where(builder.equal(table.get<MenuItemEntity>("child").get<String>("id"), childId))

    if (situation.isPaging) {
      runtime.where(builder.equal(table.get<Int>("gap"), 1))
    }

    return withSituation(runtime, situation).resultList as List<MenuItemEntity>
  }

  override fun removeByIdIs(id: String) {
    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set(table.get("status"), EntityStatus.DELETE.code)
        .set(table.get("updateTime"), now)
        .set(table.get("deleteTime"), now)

        .where(builder.equal(table.get<MenuItemEntity>("child").get<String>("id"), id))

    entityManager.createQuery(update).executeUpdate()
  }

  override fun removeAllByIdIn(ids: List<String>) {
    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set(table.get("status"), EntityStatus.DELETE.code)
        .set(table.get("updateTime"), now)
        .set(table.get("deleteTime"), now)

        .where(table.get<MenuItemEntity>("child").get<String>("id").`in`(ids))

    entityManager.createQuery(update).executeUpdate()
  }

  override fun recoverByIdIs(id: String) {
    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)

    update.set(table.get("status"), EntityStatus.NORMAL.code)
        .set(table.get("updateTime"), currentTimeMillis())
        .set(table.get("deleteTime"), -1)

        .where(builder.equal(table.get<MenuItemEntity>("child").get<String>("id"), id))

    entityManager.createQuery(update).executeUpdate()
  }

  override fun recoverAllByIdIn(ids: List<String>) {
    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)

    update.set(table.get("status"), EntityStatus.NORMAL.code)
        .set(table.get("updateTime"), currentTimeMillis())
        .set(table.get("deleteTime"), -1)

        .where(table.get<MenuItemEntity>("child").get<String>("id").`in`(ids))

    entityManager.createQuery(update).executeUpdate()
  }
}

@Repository
@Transactional
class MenuItemRepository : BasicRepository<MenuItemEntity>() {

  @Autowired
  private
  lateinit var menuRelationRepository: MenuRelationRepository

  override fun save(entity: MenuItemEntity): MenuItemEntity {
    throw UnsupportedOperationException()
  }

  override fun saveAll(entities: List<MenuItemEntity>): Iterator<MenuItemEntity> {
    throw UnsupportedOperationException()
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun existByNameIs(name: String): Boolean {
    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table.get<Int>("status")).where(builder.equal(table.get<String>("name"), name))
    return entityManager.createQuery(runtime.build()).resultList.size > 0
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryRootMenuItems(): List<MenuItemEntity> {
    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table).where(builder.equal(table.get<Boolean>("root"), true))
    return entityManager.createQuery(runtime.build()).resultList
  }

  fun save(menuItem: MenuItemEntity, parentId: String): MenuItemEntity? {
    if (existByNameIs(menuItem.name)) {
      return null
    }

    val grandParent = menuRelationRepository.queryByChildIdIs(parentId, MenuQuerySituation.Builder().build())
    val relations = mutableListOf<MenuRelationEntity>()

    if (menuItem.isRoot && !grandParent.isEmpty()) {
      throw IllegalStateException("Can not add root menu item as child item.")
    }

    if (!grandParent.isEmpty()) {
      val parent = grandParent.single { parentId == it.parent.id }.parent

      if (parent.isLeaf) {
        throw IllegalStateException("Can not add menu item under the leaf item.")
      }

      relations.addAll(grandParent.map {
        MenuRelationEntity()
            .setStatus(it.status).setDeleteTime(it.deleteTime)
            .setParent(it.parent).setChild(menuItem).setGap(it.gap + 1)
      })
    }

    relations.add(MenuRelationEntity().setParent(menuItem).setChild(menuItem).setGap(0))

    val item = super.save(menuItem)
    menuRelationRepository.saveAll(relations)

    return item
  }
}

@Repository
@Transactional
class RoleMenuRepository : BasicRepository<RoleMenuEntity>() {

  @Autowired
  private
  lateinit var menuRelationRepository: MenuRelationRepository

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryMenusByRoleIdIs(roleId: String): List<MenuItemEntity> {
    return queryMenusByRoleIdIs(roleId, BasicQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryMenusByRoleIdIs(roleId: String, situation: BasicQuerySituation): List<MenuItemEntity> {
    return queryOneBySomethingIdIs(situation, "menuItem", "role", roleId)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryMenusByRoleIdIn(roleIds: List<String>): List<MenuItemEntity> {
    return queryMenusByRoleIdIn(roleIds, BasicQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryMenusByRoleIdIn(roleIds: List<String>, situation: BasicQuerySituation): List<MenuItemEntity> {
    return queryOneBySomethingIdIn(situation, "menuItem", "role", roleIds)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryRolesByMenuItemIdIs(menuItemId: String): List<RoleEntity> {
    return queryRolesByMenuItemIdIs(menuItemId, BasicQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryRolesByMenuItemIdIs(menuItemId: String, situation: BasicQuerySituation): List<RoleEntity> {
    return queryOneBySomethingIdIs(situation, "role", "menuItem", menuItemId)
  }

  fun addRolesToMenuItem(menuId: String, roleIds: List<String>) {
    if (roleIds.isEmpty()) {
      return
    }

    val parents = menuRelationRepository.queryParentsByChildIdIs(menuId)
    val parentIds = parents.mapNotNull { it.id }.toSet()

    val runtime = QueryRuntime()
    val table = runtime.table

    runtime.select(table).where(table.get<MenuItemEntity>("menuItem").get<String>("id").`in`(parentIds))

    val oldRoleMenuMapping = entityManager.createQuery(runtime.build())
        .resultList.groupBy { it.role.id }

    val updateRoleMenus = mutableListOf<RoleMenuEntity>()
    val newRoleMenus = mutableListOf<RoleMenuEntity>()

    // 检查所有指定角色是否与当前节点及其父节点有关联实体, 有则恢复, 无则添加
    roleIds.forEach {
      if (!oldRoleMenuMapping.containsKey(it)) {
        val roleId = it
        newRoleMenus.addAll(parentIds.map { RoleMenuEntity().setRole(RoleEntity(roleId)).setMenuItem(MenuItemEntity(it)) })

        return@forEach
      }

      val parentCopier = parentIds.toMutableSet()
      oldRoleMenuMapping[it]!!.forEach {
        if (parentCopier.contains(it.menuItem.id)) {
          updateRoleMenus.add(it)
          parentCopier.remove(it.menuItem.id)
        }
      }

      if (!parentCopier.isEmpty()) {
        val roleId = it
        newRoleMenus.addAll(parentCopier.map { RoleMenuEntity().setRole(RoleEntity(roleId)).setMenuItem(MenuItemEntity(it)) })
      }
    }

    if (!newRoleMenus.isEmpty()) {
      saveAll(newRoleMenus)
    }

    if (!updateRoleMenus.isEmpty()) {
      recoverAll(updateRoleMenus)
    }
  }

  fun removeRolesFromMenuItem(menuId: String, roleIds: List<String>) {
    if (roleIds.isEmpty()) {
      return
    }

    val childes = menuRelationRepository.queryChildesByParentIdIs(menuId)
    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set(table.get("status"), EntityStatus.DELETE.code)
        .set(table.get("updateTime"), now)
        .set(table.get("deleteTime"), now)

        .where(builder.and(
            table.get<MenuItemEntity>("menuItem").get<String>("id").`in`(childes.mapNotNull { it.id }),
            table.get<RoleEntity>("role").get<String>("id").`in`(roleIds)
        ))

    entityManager.createQuery(update).executeUpdate()
  }
}