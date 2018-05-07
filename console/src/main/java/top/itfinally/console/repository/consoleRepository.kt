package top.itfinally.console.repository

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.itfinally.console.repository.entity.AccessLogEntity
import top.itfinally.core.BasicRepository

@Repository
@Transactional
class AccessLogRepository : BasicRepository<AccessLogEntity>() {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByConditionsIs(conditions: AccessLogQuerySituation): List<AccessLogEntity> {
    val runtime = conditions.build(QueryRuntime())
    return withSituation(runtime, conditions).resultList
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun countByConditionsIs(conditions: AccessLogQuerySituation): Long {
    val runtime = conditions.build(QueryRuntime())
    val builder = runtime.builder

    runtime.select(builder.count(runtime.table))

    @Suppress("CAST_NEVER_SUCCEEDS")
    return entityManager.createQuery(runtime.build()).singleResult as Long
  }
}