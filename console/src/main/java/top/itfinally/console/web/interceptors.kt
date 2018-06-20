package top.itfinally.console.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import top.itfinally.console.repository.AccessLogRepository
import top.itfinally.console.repository.entity.AccessLogEntity
import top.itfinally.core.web.BasicResponse
import top.itfinally.core.web.ResponseStatus.SERVER_ERROR
import top.itfinally.security.component.AccessForbiddenCallback
import top.itfinally.security.repository.entity.UserSecurityEntity
import java.lang.Exception
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// 用于存储当前线程承载的请求
// 主要解决在多个拦截器中通过判断 HttpServletRequest 对象在 ThreadLocal 的存活
// 来避免多次记录请求信息的状况, 跨类的共享变量设计尽量少用, 并且作用域越小越好
//
// 这里用 ThreadLocal 其实有问题, 但只会在 mvc 使用异步回调的方式处理请求时才会凸显
// 异步 + 线程池可能会出现 A 线程负责某个拦截器, 然后被中断, 最后 B 线程继续处理
// 即两个线程处理同一个请求
private val isAlreadyRecordThisError = ThreadLocal<Boolean>()

@Component
class HoldingErrorInterceptor : OncePerRequestFilter() {
  @Autowired
  private lateinit var objectMapper: ObjectMapper

  @Autowired
  private lateinit var accessLoggerInterceptor: AccessLoggerInterceptor

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    try {
      filterChain.doFilter(request, response)

    } catch (allException: Exception) {

      // 主要是为了记录在 spring-security 的拦截链内触发的异常, 因为 AccessLoggerInterceptor 组件只能记录
      // 经过自身的请求, 但是在成功登陆或校验身份前是不会经过 AccessLoggerInterceptor, 因此需要在此处再做一次拦截
      // 记录经过 AccessLoggerInterceptor 前就触发的异常, 并且要注意不能重复记录异常
      if (null == isAlreadyRecordThisError.get() || !isAlreadyRecordThisError.get()) {
        accessLoggerInterceptor.buildLog(request, true, allException)
      }

      isAlreadyRecordThisError.remove()

      response.writer.write(objectMapper.writeValueAsString(BasicResponse.It(SERVER_ERROR).setMessage(allException.message)))
    }
  }
}

@Component
class AccessLoggerInterceptor : OncePerRequestFilter(), AccessForbiddenCallback, ApplicationListener<ContextClosedEvent> {
  // (Hibernate batch)[http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html]
  // JDBC 批处理的推荐大小为 10-50
  private val bufferSize = 32

  private val sentinelThread = Executors.newScheduledThreadPool(1)
  private val loggerContainer = LinkedBlockingQueue<AccessLogEntity>(512)
  private val lock = ReentrantLock()

  // 解决抛出 spring-security 异常时, 被专用的异常处理组件拦截导致本组件认为请求成功
  private val isAlreadyRecordInSecurity = ThreadLocal<Boolean>()

  private val task = Task()
  private val currentScheduledTask = AtomicReference<Future<*>>(task.start())

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
            }

          } finally {
            lock.unlock()
          }
        }
      }

      currentScheduledTask.compareAndSet(currentScheduledTask.get(), start())
    }

    fun start(): ScheduledFuture<*> {
      return sentinelThread.schedule(this, 5, TimeUnit.MINUTES)
    }
  }

  internal fun buildLog(request: HttpServletRequest, isException: Boolean, exp: Exception?) {
    val userSecurity = SecurityContextHolder.getContext()
        ?.authentication?.principal as? UserSecurityEntity.UserSecurityDelegateEntity<*>

    loggerContainer.offer(AccessLogEntity()
        .setException(isException)
        .setSourceIp(request.remoteAddr)
        .setRequestMethod(request.method)
        .setRequestPath(request.requestURI)
        .setUsername(if (null == userSecurity) "Anonymous" else userSecurity.getUsername())
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

            val scheduled = currentScheduledTask.get()
            currentScheduledTask.compareAndSet(scheduled, task.start())
            scheduled.cancel(false)
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

  override fun onApplicationEvent(event: ContextClosedEvent) {
    sentinelThread.shutdownNow()
    accessLogRepository.saveAll(loggerContainer.toList())
  }

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    try {
      filterChain.doFilter(request, response)

      if (null == isAlreadyRecordInSecurity.get() || !isAlreadyRecordInSecurity.get()) {
        buildLog(request, false, null)
      }

      isAlreadyRecordInSecurity.remove()

    } catch (allException: Exception) {
      buildLog(request, true, allException)

      // 上层拦截时不会再重复记录该请求
      isAlreadyRecordThisError.set(true)

      // 继续往上抛出异常
      throw allException
    }
  }

  // 出现由于权限问题拒绝访问而导致异常时, 该方法的调用会先于 doFilterInternal 的 isAlreadyRecordInSecurity.get
  override fun handle(request: HttpServletRequest, response: HttpServletResponse, authException: RuntimeException) {
    buildLog(request, true, authException)
    isAlreadyRecordInSecurity.set(true)
  }
}