package top.itfinally.console

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import top.itfinally.console.repository.entity.AccessLogEntity
import top.itfinally.core.BasicRepository

@Repository
@Transactional
open class AccessLogRepository : BasicRepository<AccessLogEntity>()