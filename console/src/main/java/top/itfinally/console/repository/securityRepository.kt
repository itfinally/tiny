package top.itfinally.console.repository

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.itfinally.security.repository.DepartmentRepository
import top.itfinally.security.repository.PermissionRepository
import top.itfinally.security.repository.RoleRepository
import top.itfinally.security.repository.entity.DepartmentEntity
import top.itfinally.security.repository.entity.PermissionEntity
import top.itfinally.security.repository.entity.RoleEntity

@Repository
@Transactional
class PermissionRepositoryExtended : PermissionRepository() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByConditionsIs(conditions: ConditionQuerySituation): List<PermissionEntity> {
    val runtime = conditions.build(QueryRuntime())
    return withSituation(runtime, conditions).resultList
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun countByConditionsIs(conditions: ConditionQuerySituation): Long {
    val runtime = conditions.build(QueryRuntime())
    val builder = runtime.builder

    runtime.select(builder.count(runtime.table))

    @Suppress("CAST_NEVER_SUCCEEDS")
    return entityManager.createQuery(runtime.build()).singleResult as Long
  }
}

@Repository
@Transactional
class RoleRepositoryExtended: RoleRepository() {
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByConditionsIs(conditions: ConditionQuerySituation): List<RoleEntity> {
    val runtime = conditions.build(QueryRuntime())
    return withSituation(runtime, conditions).resultList
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun countByConditionsIs(conditions: ConditionQuerySituation): Long {
    val runtime = conditions.build(QueryRuntime())
    val builder = runtime.builder

    runtime.select(builder.count(runtime.table))

    @Suppress("CAST_NEVER_SUCCEEDS")
    return entityManager.createQuery(runtime.build()).singleResult as Long
  }
}

@Repository
@Transactional
class DepartmentRepositoryExtended: DepartmentRepository() {
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByConditionsIs(conditions: ConditionQuerySituation): List<DepartmentEntity> {
    val runtime = conditions.build(QueryRuntime())
    return withSituation(runtime, conditions).resultList
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun countByConditionsIs(conditions: ConditionQuerySituation): Long {
    val runtime = conditions.build(QueryRuntime())
    val builder = runtime.builder

    runtime.select(builder.count(runtime.table))

    @Suppress("CAST_NEVER_SUCCEEDS")
    return entityManager.createQuery(runtime.build()).singleResult as Long
  }
}