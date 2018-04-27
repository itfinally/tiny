package top.itfinally.console.service

import top.itfinally.core.web.ResponseStatus.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import top.itfinally.console.repository.AccessLogRepository
import top.itfinally.console.repository.AccessLogQuerySituation
import top.itfinally.console.web.vo.AccessLogVoBean
import top.itfinally.core.web.ListResponse
import top.itfinally.core.web.SingleResponse

@Service
open class AccessLogService {
  @Autowired
  private lateinit var accessLogRepository: AccessLogRepository

  open fun queryByConditionsIs(conditions: AccessLogQuerySituation): ListResponse<AccessLogVoBean> {
    val permissions = accessLogRepository.queryByConditionsIs(conditions)
    val status = if (permissions.isEmpty()) EMPTY_RESULT else SUCCESS

    return ListResponse<AccessLogVoBean>(status).setResult(permissions.map { AccessLogVoBean(it) })
  }

  open fun countByConditionIs(conditions: AccessLogQuerySituation): SingleResponse<Long> {
    val total = accessLogRepository.countByConditionsIs(conditions)
    return SingleResponse<Long>(SUCCESS).setResult(total)
  }
}