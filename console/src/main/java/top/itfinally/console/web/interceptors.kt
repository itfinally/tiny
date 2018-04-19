package top.itfinally.console.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import top.itfinally.console.AccessLogRepository
import top.itfinally.console.repository.entity.AccessLogEntity
import top.itfinally.core.web.BasicResponse
import top.itfinally.core.web.ResponseStatus.SERVER_ERROR
import top.itfinally.security.component.AccessForbiddenCallback
import top.itfinally.security.repository.entity.UserSecurityEntity
import java.lang.Exception
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AccessLoggerInterceptor : OncePerRequestFilter(), AccessForbiddenCallback, ApplicationListener<ContextClosedEvent> {
  // (Hibernate batch)[http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html]
  // JDBC 批处理的推荐大小为 10-50
  private val bufferSize = 32

  private val sentinelThread = Executors.newScheduledThreadPool(1)
  private val loggerContainer = ArrayBlockingQueue<AccessLogEntity>(512)
  private val lock = ReentrantLock()

  // 解决抛出 spring-security 异常时, 被专用的异常处理组件拦截导致本组件认为请求成功
  private val isRaiseExceptionWithSecurity = ThreadLocal<Boolean>()

  @Volatile
  private var currentScheduledTask = Task().start()

  @Autowired
  private lateinit var accessLogRepository: AccessLogRepository

  private inner class Task : Runnable {
    override fun run() {
      if (!loggerContainer.isEmpty()) {
        if (lock.tryLock()) {
          try {
            if (!loggerContainer.isEmpty()) {
              val logs = getLogs()
              sentinelThread.execute {
                try {
                  accessLogRepository.saveAll(logs)

                } catch (exp: Exception) {
                  logs.forEach { logger.info(it) }
                }
              }

              currentScheduledTask = start()
            }

          } finally {
            lock.unlock()
          }
        }
      }
    }

    fun start(): ScheduledFuture<*> {
      return sentinelThread.schedule(this, 5, TimeUnit.MINUTES)
    }
  }

  private fun submitLogger(request: HttpServletRequest, isException: Boolean, exp: Exception?) {
    val userSecurity = SecurityContextHolder.getContext()
        ?.authentication?.principal as? UserSecurityEntity.UserSecurityDelegateEntity<*>

    loggerContainer.offer(AccessLogEntity()
        .setSourceIp(request.remoteAddr)
        .setRequestPath(request.requestURI)
        .setUsername(if (null == userSecurity) "Anonymous" else userSecurity.getUsername())
        .setException(isException)
        .setResult(if (null == exp) "Request successful" else "${exp::class.java.name} -> ${exp.message}"))

    if (loggerContainer.size > bufferSize) {
      if (lock.tryLock()) {
        try {
          if (loggerContainer.size > bufferSize) {
            sentinelThread.execute {
              val logs = getLogs()
              try {
                accessLogRepository.saveAll(logs)

              } catch (exp: Exception) {
                logs.forEach { logger.info(it) }
              }
            }

            currentScheduledTask.cancel(false)
            currentScheduledTask = Task().start()
          }

        } finally {
          lock.unlock()
        }
      }
    }
  }

  private fun getLogs(): List<AccessLogEntity> {
    val logs = mutableListOf<AccessLogEntity>()
    var elem: AccessLogEntity?

    while (logs.size < bufferSize) {
      elem = loggerContainer.poll(500, TimeUnit.MILLISECONDS)
      if (null == elem) {
        break
      }

      logs.add(elem)
    }

    return logs
  }

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    try {
      filterChain.doFilter(request, response)

      if (null == isRaiseExceptionWithSecurity.get() || !isRaiseExceptionWithSecurity.get()) {
        submitLogger(request, false, null)
      }

      isRaiseExceptionWithSecurity.remove()

    } catch (exp: Exception) {
      submitLogger(request, true, exp)

      // 继续往上抛出异常
      throw exp
    }
  }

  // 出现由于权限问题拒绝访问而导致异常时, 该方法的调用会先于 doFilterInternal 的 isRaiseExceptionWithSecurity.get
  override fun handle(request: HttpServletRequest, response: HttpServletResponse, authException: RuntimeException) {
    submitLogger(request, true, authException)
    isRaiseExceptionWithSecurity.set(true)
  }

  override fun onApplicationEvent(event: ContextClosedEvent) {
    sentinelThread.shutdownNow()
    accessLogRepository.saveAll(loggerContainer.toList())
  }
}

@Component
class SystemErrorInterceptor : OncePerRequestFilter() {
  @Autowired
  private lateinit var objectMapper: ObjectMapper

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    try {
      filterChain.doFilter(request, response)

    } catch (allException: Exception) {
      response.writer.write(objectMapper.writeValueAsString(BasicResponse.It(SERVER_ERROR).setMessage(allException.message)))
    }
  }
}