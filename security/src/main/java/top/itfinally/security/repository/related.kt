package top.itfinally.security.repository

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.itfinally.core.BasicRepository
import top.itfinally.core.EntityStatus
import top.itfinally.core.repository.BasicQuerySituation
import top.itfinally.security.repository.entity.*
import java.lang.System.currentTimeMillis

@Repository
@Transactional
open class DepartmentRoleRepository : BasicRepository<DepartmentRoleEntity>() {
  // Method parameter can use kotlin's default value, but can not use it here.
  // For the reason to compatible with java, There must use overloading to do the same thing.
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryDepartmentsByRoleIdIs(roleId: String): List<DepartmentEntity> {
    return queryDepartmentsByRoleIdIs(roleId, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryDepartmentsByRoleIdIs(roleId: String, situation: BasicQuerySituation<*>): List<DepartmentEntity> {
    return queryOneBySomethingIdIs(situation, "department", "role", roleId)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryRolesByDepartmentIdIs(departmentId: String): List<RoleEntity> {
    return queryRolesByDepartmentIdIs(departmentId, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryRolesByDepartmentIdIs(departmentId: String, situation: BasicQuerySituation<*>): List<RoleEntity> {
    return queryOneBySomethingIdIs(situation, "role", "department", departmentId)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryDepartmentsByRoleIdIn(roleIds: List<String>): List<DepartmentEntity> {
    return queryDepartmentsByRoleIdIn(roleIds, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryDepartmentsByRoleIdIn(roleIds: List<String>, situation: BasicQuerySituation<*>): List<DepartmentEntity> {
    return queryOneBySomethingIdIn(situation, "department", "role", roleIds)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryRolesByDepartmentIdIn(departmentIds: List<String>): List<RoleEntity> {
    return queryRolesByDepartmentIdIn(departmentIds, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryRolesByDepartmentIdIn(departmentIds: List<String>, situation: BasicQuerySituation<*>): List<RoleEntity> {
    return queryOneBySomethingIdIn(situation, "role", "department", departmentIds)
  }

  open fun addRolesToDepartment(departmentId: String, roleIds: List<String>) {
    if (roleIds.isEmpty()) {
      return
    }

    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table).where(builder.equal(table.get<DepartmentEntity>("department").get<String>("id"), departmentId))

    val updateDepartmentRoles = mutableListOf<DepartmentRoleEntity>()
    val oldDepartmentRoles = entityManager.createQuery(runtime.build()).resultList
        .map { Pair<String, DepartmentRoleEntity>(it.role.id, it) }.toMap()

    val newDepartmentRoleIds = roleIds.filter {
      return@filter if (oldDepartmentRoles.containsKey(it)) {
        updateDepartmentRoles.add(oldDepartmentRoles[it]!!)
        false

      } else {
        true
      }
    }

    if (!newDepartmentRoleIds.isEmpty()) {
      val department = DepartmentEntity(departmentId)
      saveAll(newDepartmentRoleIds.map { DepartmentRoleEntity().setRole(RoleEntity(it)).setDepartment(department) })
    }

    if (!updateDepartmentRoles.isEmpty()) {
      recoverAll(updateDepartmentRoles)
    }
  }

  open fun removeRoleFromDepartment(departmentId: String, roleIds: List<String>) {
    if (roleIds.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set(table.get("status"), EntityStatus.DELETE.code)
        .set(table.get("updateTime"), now)
        .set(table.get("deleteTime"), now)

        .where(builder.and(
            builder.equal(table.get<DepartmentEntity>("department").get<String>("id"), departmentId),
            table.get<RoleEntity>("role").get<String>("id").`in`(roleIds)))

    entityManager.createQuery(update).executeUpdate()
  }
}

@Repository
@Transactional
open class RolePermissionRepository : BasicRepository<RolePermissionEntity>() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryRolesByPermissionIdIs(permissionId: String): List<RoleEntity> {
    return queryRolesByPermissionIdIs(permissionId, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryRolesByPermissionIdIs(permissionId: String, situation: BasicQuerySituation<*>): List<RoleEntity> {
    return queryOneBySomethingIdIs(situation, "role", "permission", permissionId)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryPermissionsByRoleIdIs(roleId: String): List<PermissionEntity> {
    return queryPermissionsByRoleIdIs(roleId, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryPermissionsByRoleIdIs(roleId: String, situation: BasicQuerySituation<*>): List<PermissionEntity> {
    return queryOneBySomethingIdIs(situation, "permission", "role", roleId)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryPermissionsByRoleIdsIn(roleIds: List<String>): List<PermissionEntity> {
    return queryPermissionsByRoleIdsIn(roleIds, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryPermissionsByRoleIdsIn(roleIds: List<String>, situation: BasicQuerySituation<*>): List<PermissionEntity> {
    return queryOneBySomethingIdIn<PermissionEntity>(situation, "permission", "role", roleIds).distinct()
  }

  open fun addPermissionsToRole(roleId: String, permissionIds: List<String>) {
    if (permissionIds.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val query = builder.createQuery(genericType)
    val table = query.from(genericType)

    query.select(table).where(builder.equal(table.get<RoleEntity>("role").get<String>("id"), roleId))

    val updateRolePermissions = mutableListOf<RolePermissionEntity>()
    val oldRolePermissions = entityManager.createQuery(query).resultList
        .map { Pair<String, RolePermissionEntity>(it.permission.id, it) }.toMap()

    val newRolePermissionIds = permissionIds.filter {
      return@filter if (oldRolePermissions.containsKey(it)) {
        updateRolePermissions.add(oldRolePermissions[it]!!)
        false

      } else {
        true
      }
    }

    if (!newRolePermissionIds.isEmpty()) {
      val role = RoleEntity(roleId)
      saveAll(newRolePermissionIds.map { RolePermissionEntity().setPermission(PermissionEntity(it)).setRole(role) })
    }

    if (!updateRolePermissions.isEmpty()) {
      recoverAll(updateRolePermissions)
    }
  }

  open fun removePermissionsFromRole(roleId: String, permissionIds: List<String>) {
    if (permissionIds.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set(table.get("status"), EntityStatus.DELETE.code)
        .set(table.get("updateTime"), now)
        .set(table.get("deleteTime"), now)

        .where(builder.and(
            builder.equal(table.get<RoleEntity>("role").get<String>("id"), roleId),
            table.get<PermissionEntity>("permission").get<String>("id").`in`(permissionIds)))

    entityManager.createQuery(update).executeUpdate()
  }
}

@Repository
open class UserRoleRepository : BasicRepository<UserRoleEntity>() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryUserSecuritiesByRoleIdIs(roleId: String): List<UserSecurityEntity> {
    return queryUserSecuritiesByRoleIdIs(roleId, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryUserSecuritiesByRoleIdIs(roleId: String, situation: BasicQuerySituation<*>): List<UserSecurityEntity> {
    return queryOneBySomethingIdIs(situation, "userSecurity", "role", roleId)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryRolesByUserSecurityIdIs(userSecurityId: String): List<RoleEntity> {
    return queryRolesByUserSecurityIdIs(userSecurityId, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryRolesByUserSecurityIdIs(userSecurityId: String, situation: BasicQuerySituation<*>): List<RoleEntity> {
    return queryOneBySomethingIdIs(situation, "role", "userSecurity", userSecurityId)
  }

  open fun addRolesToUser(userSecurityId: String, roleIds: List<String>) {
    if (roleIds.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val query = builder.createQuery(genericType)
    val table = query.from(genericType)

    query.select(table).where(builder.equal(table.get<UserSecurityEntity>("userSecurity").get<String>("id"), userSecurityId))

    val updateUserRoles = mutableListOf<UserRoleEntity>()
    val oldUserRoles = entityManager.createQuery(query).resultList
        .map { Pair<String, UserRoleEntity>(it.role.id, it) }.toMap()

    val newRoleIds = roleIds.filter {
      return@filter if (oldUserRoles.containsKey(it)) {
        updateUserRoles.add(oldUserRoles[it]!!)
        false

      } else {
        true
      }
    }

    if (!newRoleIds.isEmpty()) {
      val userSecurity = UserSecurityEntity(userSecurityId)
      saveAll(newRoleIds.map { UserRoleEntity().setRole(RoleEntity(it)).setUserSecurity(userSecurity) })
    }

    if (!updateUserRoles.isEmpty()) {
      recoverAll(updateUserRoles)
    }
  }

  open fun removeRolesFromUser(userSecurityId: String, roleIds: List<String>) {
    if (roleIds.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set(table.get("status"), EntityStatus.DELETE.code)
        .set(table.get("updateTime"), now)
        .set(table.get("deleteTime"), now)

        .where(builder.and(
            builder.equal(table.get<UserSecurityEntity>("userSecurity").get<String>("id"), userSecurityId),
            table.get<RoleEntity>("role").get<String>("id").`in`(roleIds)))

    entityManager.createQuery(update).executeUpdate()
  }
}

@Repository
@Transactional
open class UserDepartmentRepository : BasicRepository<UserDepartmentEntity>() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryUserSecuritiesByDepartmentIdIs(departmentId: String): List<UserSecurityEntity> {
    return queryUserSecuritiesByDepartmentIdIs(departmentId, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryUserSecuritiesByDepartmentIdIs(departmentId: String, situation: BasicQuerySituation<*>): List<UserSecurityEntity> {
    return queryOneBySomethingIdIs(situation, "userSecurity", "department", departmentId)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryDepartmentsByUserSecurityIdIs(userSecurityId: String): List<DepartmentEntity> {
    return queryDepartmentsByUserSecurityIdIs(userSecurityId, BasicQuerySituation.It())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryDepartmentsByUserSecurityIdIs(userSecurityId: String, situation: BasicQuerySituation<*>): List<DepartmentEntity> {
    return queryOneBySomethingIdIs(situation, "department", "userSecurity", userSecurityId)
  }

  open fun addUsersToDepartment(departmentId: String, userSecurityIds: List<String>) {
    if (userSecurityIds.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val query = builder.createQuery(genericType)
    val table = query.from(genericType)

    query.select(table).where(builder.equal(table.get<DepartmentEntity>("department").get<String>("id"), departmentId))

    val updateUserDepartments = mutableListOf<UserDepartmentEntity>()
    val oldUserDepartments = entityManager.createQuery(query).resultList
        .map { Pair<String, UserDepartmentEntity>(it.userSecurity.id, it) }.toMap()

    val newUserSecurityIds = userSecurityIds.filter {
      return@filter if (oldUserDepartments.containsKey(it)) {
        updateUserDepartments.add(oldUserDepartments[it]!!)
        false

      } else {
        true
      }
    }

    if (!newUserSecurityIds.isEmpty()) {
      val department = DepartmentEntity(departmentId)
      saveAll(newUserSecurityIds.map { UserDepartmentEntity().setUserSecurity(UserSecurityEntity(it)).setDepartment(department) })
    }

    if (!updateUserDepartments.isEmpty()) {
      recoverAll(updateUserDepartments)
    }
  }

  open fun removeUsersFromDepartment(departmentId: String, userSecurityIds: List<String>) {
    if (userSecurityIds.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set(table.get("status"), EntityStatus.DELETE.code)
        .set(table.get("updateTime"), now)
        .set(table.get("deleteTime"), now)

        .where(builder.and(
            builder.equal(table.get<DepartmentEntity>("department").get<String>("id"), departmentId),
            table.get<UserSecurityEntity>("userSecurity").get<String>("id").`in`(userSecurityIds)))

    entityManager.createQuery(update).executeUpdate()
  }
}