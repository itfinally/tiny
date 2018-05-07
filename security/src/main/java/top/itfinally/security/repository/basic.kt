package top.itfinally.security.repository

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.itfinally.core.BasicRepository
import top.itfinally.core.repository.BasicQuerySituation
import top.itfinally.security.repository.entity.*
import javax.persistence.NoResultException


@Repository
@Transactional
open class RoleRepository : BasicRepository<RoleEntity>() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun existByNameIs(name: String): Boolean {
    val builder = entityManager.criteriaBuilder
    val query = builder.createQuery(genericType)
    val table = query.from(genericType)

    query.select(table.get("status")).where(builder.equal(table.get<String>("name"), name))
    return entityManager.createQuery(query).resultList.size > 0
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryByNameIs(name: String): RoleEntity? {
    return queryByNameIs(name, BasicQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryByNameIs(name: String, situation: BasicQuerySituation): RoleEntity? {
    val runtime = QueryRuntime()
    val builder = runtime.builder

    runtime.select(runtime.table).where(builder.equal(runtime.table.get<String>("name"), name.toUpperCase()))

    return try {
      withSituation(runtime, situation).singleResult

    } catch (exp: NoResultException) {
      null
    }
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryAllLowerRolesByPriorityIs(priority: Int): List<RoleEntity> {
    val runtime = QueryRuntime()
    val builder = runtime.builder
    val table = runtime.table

    runtime.select(table).where(builder.greaterThan(table.get<Int>("priority"), priority))
    return entityManager.createQuery(runtime.build()).resultList
  }
}

@Repository
@Transactional
open class DepartmentRepository : BasicRepository<DepartmentEntity>() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun existByName(name: String): Boolean {
    val builder = entityManager.criteriaBuilder
    val query = builder.createQuery(genericType)
    val table = query.from(genericType)

    query.select(table.get("status")).where(builder.equal(table.get<String>("name"), name))
    return entityManager.createQuery(query).resultList.size > 0
  }
}

@Repository
@Transactional
open class PermissionRepository : BasicRepository<PermissionEntity>() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun existByName(name: String): Boolean {
    val builder = entityManager.criteriaBuilder
    val query = builder.createQuery(genericType)
    val table = query.from(genericType)

    query.select(table.get("status")).where(builder.equal(table.get<String>("name"), name))
    return entityManager.createQuery(query).resultList.size > 0
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryByNameIs(name: String): PermissionEntity? {
    return queryByNameIs(name, BasicQuerySituation.Builder().build())
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryByNameIs(name: String, situation: BasicQuerySituation): PermissionEntity? {
    val runtime = QueryRuntime()
    val builder = runtime.builder
    val table = runtime.table

    runtime.select(table).where(builder.equal(table.get<String>("name"), name))

    return try {
      withSituation(runtime, situation).singleResult

    } catch (exp: NoResultException) {
      null
    }
  }
}

@Repository
@Transactional
open class UserSecurityRepository : BasicRepository<UserSecurityEntity>()