package top.itfinally.core

import com.google.common.reflect.TypeToken
import org.hibernate.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.itfinally.core.repository.BasicEntity
import top.itfinally.core.repository.BasicQuerySituation
import java.lang.System.currentTimeMillis
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.persistence.criteria.*

enum class EntityStatus(val code: Int, val description: String) {
  NORMAL(1, "正常"),
  DELETE(-1, "已删除");

  companion object {
    fun existByCodeIs(code: Int): Boolean {
      return values().singleOrNull { it.code == code } != null
    }
  }
}

enum class QueryStatus(val code: Int) {
  NOT_PAGING(-999),
  NOT_STATUS(-998);
}

interface BasicRuntime<Entity : BasicEntity<Entity>> {
  val builder: CriteriaBuilder
  val query: CriteriaQuery<Entity>
  val table: Root<Entity>

  fun select(vararg selection: Selection<*>): BasicRuntime<Entity>

  fun where(vararg predicate: Predicate): BasicRuntime<Entity>

  fun clear()

  fun build(): CriteriaQuery<Entity>
}

/**
 * CRUD基础抽象类, 继承该类需要注意下列问题:
 *  * 默认拥有 entityManager 对象, 该对象用法请查阅 Hibernate5 文档
 *  * 默认拥有 genericType 对象, 即当前实体的 Class 对象
 *  * QueryRuntime 内部类仅用于子类, 解决 entityManager 无法按需加入条件的问题
 *  * withSituation 方法仅用于子类, 配合 BasicQuerySituation 使用, 内含分页及状态条件
 *
 * @since 1.0
 * @author itfinally
 */
@Transactional
abstract class BasicRepository<Entity : BasicEntity<Entity>> {
  private val batchSize = 64

  protected val genericType: Class<Entity>

  @Autowired
  protected lateinit var entityManager: EntityManager

  init {
    val token = object : TypeToken<Entity>(javaClass) {}

    @Suppress("UNCHECKED_CAST")
    genericType = token.rawType as? Class<Entity>
        ?: throw NullPointerException("Can not get generic type from class ${javaClass.name}")
  }

  protected fun getSession(): Session {
    return entityManager.unwrap(Session::class.java)
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryByIdIs(id: String): Entity? {
    val entity = entityManager.find(genericType, id)

    entityManager.clear()
    return entity
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryAllByIdIn(ids: List<String>): List<Entity> {
    if (ids.isEmpty()) {
      return listOf()
    }

    val runtime = QueryRuntime()
    val query = runtime.query
    val table = runtime.table

    runtime.select(table)

    if (ids.size > 1) runtime.where(table.get<String>("id").`in`(ids))
    else runtime.where(runtime.builder.equal(table.get<String>("id"), ids[0]))

    val entities = entityManager.createQuery(query).resultList

    entityManager.clear()
    return entities
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  fun queryAll(situation: BasicQuerySituation): List<Entity> {
    val entities = withSituation(QueryRuntime(), situation).resultList

    entityManager.clear()
    return entities
  }

  fun existsById(id: String): Boolean {
    val runtime = QueryRuntime()
    val table = runtime.table

    runtime.select(table).where(runtime.builder.equal(table.get<String>("id"), id))
    return entityManager.createQuery(runtime.build()).resultList.size > 0
  }

  fun save(entity: Entity): Entity {
    entityManager.persist(entity)
    return entity
  }

  fun saveAll(entities: List<Entity>): Iterator<Entity> {
    entities.forEachIndexed { index, entity ->
      entityManager.persist(entity)

      if (index != 0 && 0 == index % batchSize) {
        entityManager.flush()
      }
    }

    entityManager.flush()
    return entities.iterator()
  }

  fun update(entity: Entity): Entity {
    return entityManager.merge(entity)
  }

  fun updateAll(entities: List<Entity>): Iterator<Entity> {
    val now = currentTimeMillis()

    val newEntities = entities.mapIndexed { index, entity ->
      val newEntity = entityManager.merge(entity.setUpdateTime(now))

      if (index != 0 && 0 == index % batchSize) {
        entityManager.flush()
      }

      return@mapIndexed newEntity
    }

    entityManager.flush()
    return newEntities.iterator()
  }

  fun removeByIdIs(id: String) {
    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set("status", EntityStatus.DELETE.code).set("updateTime", now).set("deleteTime", now)
        .where(builder.equal(table.get<String>("id"), id))

    entityManager.createQuery(update).executeUpdate()
  }

  fun removeAllByIdIn(ids: List<String>) {
    if (ids.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set("status", EntityStatus.DELETE.code).set("updateTime", now).set("deleteTime", now)

    if (ids.size > 1) update.where(table.get<String>("id").`in`(ids))
    else update.where(builder.equal(table.get<String>("id"), ids[0]))

    entityManager.createQuery(update).executeUpdate()
  }

  fun remove(entity: Entity) {
    val now = currentTimeMillis()
    removeByIdIs(entity.id)

    entity.setStatus(EntityStatus.DELETE.code).setDeleteTime(now).updateTime = now
  }

  fun removeAll(entities: List<Entity>) {
    val now = currentTimeMillis()
    removeAllByIdIn(entities.map { it.id })

    entities.forEach { it.setStatus(EntityStatus.DELETE.code).setDeleteTime(now).setUpdateTime(now) }
  }

  fun recoverByIdIs(id: String) {
    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set("status", EntityStatus.NORMAL.code).set("updateTime", now).set("deleteTime", -1)
        .where(builder.equal(table.get<String>("id"), id))

    entityManager.createQuery(update).executeUpdate()
  }

  fun recoverAllByIdIn(ids: List<String>) {
    if (ids.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val update = builder.createCriteriaUpdate(genericType)
    val table = update.from(genericType)
    val now = currentTimeMillis()

    update.set("status", EntityStatus.NORMAL.code).set("updateTime", now).set("deleteTime", -1)

    if (ids.size > 1) update.where(table.get<String>("id").`in`(ids))
    else update.where(builder.equal(table.get<String>("id"), ids[0]))

    entityManager.createQuery(update).executeUpdate()
  }

  fun recover(entity: Entity) {
    val now = currentTimeMillis()
    recoverByIdIs(entity.id)

    entity.setUpdateTime(now).setDeleteTime(-1).status = EntityStatus.NORMAL.code
  }

  fun recoverAll(entities: List<Entity>) {
    val now = currentTimeMillis()
    recoverAllByIdIn(entities.map { it.id })

    entities.forEach { it.setUpdateTime(now).setDeleteTime(-1).setStatus(EntityStatus.NORMAL.code) }
  }

  fun deleteByIdIs(id: String) {
    val builder = entityManager.criteriaBuilder
    val delete = builder.createCriteriaDelete(genericType)
    val table = delete.from(genericType)

    delete.where(builder.equal(table.get<String>("id"), id))
    entityManager.createQuery(delete).executeUpdate()
  }

  fun deleteAllByIdIn(ids: List<String>) {
    if (ids.isEmpty()) {
      return
    }

    val builder = entityManager.criteriaBuilder
    val delete = builder.createCriteriaDelete(genericType)
    val table = delete.from(genericType)

    if (ids.size > 1) delete.where(table.get<String>("id").`in`(ids))
    else delete.where(builder.equal(table.get<String>("id"), ids[0]))

    entityManager.createQuery(delete).executeUpdate()
  }

  fun delete(entity: Entity) {
    deleteByIdIs(entity.id)
  }

  fun deleteAll(entities: List<Entity>) {
    deleteAllByIdIn(entities.map { it.id })
  }

  protected fun <Return> queryOneBySomethingIdIs(situation: BasicQuerySituation, selectField: String,
                                                      conditionField: String, conditionValue: Any): List<Return> {

    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table.get<Any>(selectField)).where(builder.equal(table.get<Entity>(conditionField).get<String>("id"), conditionValue))

    @Suppress("UNCHECKED_CAST")
    val entities = withSituation(runtime, situation).resultList as List<Return>

    entityManager.clear()
    return entities
  }

  protected fun <Return> queryOneBySomethingIdIn(situation: BasicQuerySituation, selectField: String,
                                                      conditionField: String, conditionValues: List<Any>): List<Return> {
    if (conditionValues.isEmpty()) {
      return listOf()
    }

    val runtime = QueryRuntime()
    val table = runtime.table

    runtime.select(table.get<Any>(selectField)).where(table.get<Entity>(conditionField).get<String>("id").`in`(conditionValues))

    @Suppress("UNCHECKED_CAST")
    val entities = withSituation(runtime, situation).resultList as List<Return>

    entityManager.clear()
    return entities
  }

  protected fun withSituation(runtime: BasicRuntime<Entity>, situation: BasicQuerySituation): TypedQuery<Entity> {
    if (situation.hasStatus()) {
      runtime.where(runtime.builder.equal(runtime.table.get<Int>("status"), situation.status))
    }

    val readyToQuery = entityManager.createQuery(runtime.build())

    if (situation.isPaging) {
      readyToQuery.setFirstResult(situation.beginRow).maxResults = situation.row
    }

    return readyToQuery
  }

  protected inner class QueryRuntime : BasicRuntime<Entity> {
    override val builder: CriteriaBuilder = entityManager.criteriaBuilder
    override val query: CriteriaQuery<Entity> = builder.createQuery(genericType)
    override val table: Root<Entity> = query.from(genericType)

    private val conditions = mutableListOf<Predicate>()
    private val selections = mutableListOf<Selection<*>>()

    override fun select(vararg selection: Selection<*>): QueryRuntime {
      selections.addAll(selection)
      return this
    }

    override fun where(vararg predicate: Predicate): QueryRuntime {
      conditions.addAll(predicate)
      return this
    }

    override fun clear() {
      selections.clear()
      conditions.clear()
    }

    override fun build(): CriteriaQuery<Entity> {
      if (!selections.isEmpty()) {
        if (1 == selections.size) {
          @Suppress("UNCHECKED_CAST")
          query.select(selections[0] as Selection<out Entity>)

        } else {
          query.multiselect(*selections.toTypedArray())
        }
      }

      if (!conditions.isEmpty()) {
        query.where(builder.and(*conditions.toTypedArray()))
      }

      return query
    }
  }
}