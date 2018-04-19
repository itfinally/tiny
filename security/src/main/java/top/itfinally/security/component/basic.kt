package top.itfinally.security.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.code.kaptcha.Producer
import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.eventbus.Subscribe
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import top.itfinally.core.web.ResponseStatus.FORBIDDEN
import top.itfinally.core.web.ResponseStatus.UNAUTHORIZED
import top.itfinally.core.web.SingleResponse
import top.itfinally.security.AccountChangeEvent
import top.itfinally.security.AccountResetEvent
import top.itfinally.security.repository.entity.UserSecurityEntity
import java.awt.image.BufferedImage
import java.lang.Runtime.getRuntime
import java.security.Key
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.ParametersAreNonnullByDefault
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.DatatypeConverter

class JwtAuthenticationToken(private val token: String,
                             private val userSecurityDelegateEntity: UserSecurityEntity.UserSecurityDelegateEntity<*>)

  : AbstractAuthenticationToken(userSecurityDelegateEntity.getAuthorities()) {

  init {
    super.setAuthenticated(true)
  }

  override fun getCredentials(): Any {
    return token
  }

  override fun getPrincipal(): Any {
    return userSecurityDelegateEntity
  }

  // Just copy from 'UsernamePasswordAuthenticationToken'
  override fun setAuthenticated(isAuthenticated: Boolean) {
    if (isAuthenticated) {
      throw IllegalArgumentException(
          "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead")
    }

    super.setAuthenticated(false)
  }
}

interface AccessForbiddenCallback {
  fun handle(request: HttpServletRequest, response: HttpServletResponse, authException: RuntimeException)
}

abstract class AbstractAccessForbiddenHandler : AuthenticationEntryPoint, AccessDeniedHandler {

  @Autowired
  private lateinit var jsonMapper: ObjectMapper

  @Autowired(required = false)
  private lateinit var accessForbiddenCallback: AccessForbiddenCallback

  // called when user authentication fails
  protected abstract fun authorityException(authException: AuthenticationException): Any

  // called when user illegal access resource
  protected abstract fun accessDeniedException(accessDeniedException: AccessDeniedException): Any

  override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
    if (::accessForbiddenCallback.isLateinit) {
      accessForbiddenCallback.handle(request, response, authException)
    }

    handler(response, authorityException(authException))
  }

  override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
    if (::accessForbiddenCallback.isLateinit) {
      accessForbiddenCallback.handle(request, response, accessDeniedException)
    }

    handler(response, accessDeniedException(accessDeniedException))
  }

  private fun handler(response: HttpServletResponse, result: Any) {
    response.contentType = "application/json;charset=UTF-8"
    response.writer.write(jsonMapper.writeValueAsString(result))
  }
}

@Component
class DefaultAccessForbiddenHandler : AbstractAccessForbiddenHandler() {
  override fun authorityException(authException: AuthenticationException): Any {
    return SingleResponse<Any>(UNAUTHORIZED).setMessage(authException.message ?: "")
  }

  override fun accessDeniedException(accessDeniedException: AccessDeniedException): Any {
    return SingleResponse<Any>(FORBIDDEN).setMessage(accessDeniedException.message ?: "")
  }
}

abstract class AbstractJwtTokenComponent {
  protected abstract fun getAlgorithm(): SignatureAlgorithm

  protected abstract fun encodeKey(): Key

  protected abstract fun decodeKey(): Key

  fun create(account: String?): String {
    if (null == account) {
      throw UsernameNotFoundException("Account is empty, cannot generate token.")
    }

    val params = HashMap<String, Any>()
    params["account"] = account

    return Jwts.builder()
        .addClaims(params)
        .signWith(getAlgorithm(), encodeKey())
        .compact()
  }

  fun queryByTokenIs(token: String): String? {
    return try {
      val body = Jwts.parser()
          .setSigningKey(decodeKey())
          .parseClaimsJws(token)
          .body

      body["account"] as String

    } catch (ignored: RuntimeException) {
      null
    }
  }
}

@Component
class DefaultJwtTokenService : AbstractJwtTokenComponent() {
  private val randomSecret = "Must to override this secret."
  private val keySpec: SecretKeySpec by lazy { SecretKeySpec(DatatypeConverter.parseBase64Binary(randomSecret), getAlgorithm().jcaName) }

  override fun getAlgorithm(): SignatureAlgorithm {
    return SignatureAlgorithm.HS256
  }

  override fun encodeKey(): Key {
    return keySpec
  }

  override fun decodeKey(): Key {
    return keySpec
  }
}

abstract class AbstractUserDetailCachingComponent {
  private val accountChangingMarker: LoadingCache<String, Boolean> = CacheBuilder.newBuilder()
      .concurrencyLevel(getRuntime().availableProcessors())
      .expireAfterWrite(15, TimeUnit.MINUTES)
      .initialCapacity(16)
      .maximumSize(20480)
      .build(object : CacheLoader<String, Boolean>() {
        override fun load(roleId: String?): Boolean {
          return false
        }
      })

  abstract fun queryByAccountIs(account: String): UserSecurityEntity.UserSecurityDelegateEntity<*>?

  abstract fun caching(account: String, user: UserSecurityEntity.UserSecurityDelegateEntity<*>)

  abstract fun remove(account: String)

  fun isChange(account: String): Boolean {
    return try {
      val isChange = accountChangingMarker.get(account)
      accountChangingMarker.invalidate(account)

      isChange

    } catch (exp: ExecutionException) {
      false
    }
  }

  @Subscribe
  fun setAccountChange(event: AccountChangeEvent) {
    accountChangingMarker.put(event.account, true)
  }

  @Subscribe
  fun resetAccountChange(event: AccountResetEvent) {
    accountChangingMarker.invalidate(event.account)
  }
}

@Component
class DefaultUserDetailCachingService : AbstractUserDetailCachingComponent() {
  private val userTokenCache = CacheBuilder.newBuilder()
      .concurrencyLevel(getRuntime().availableProcessors())
      .expireAfterWrite(30, TimeUnit.DAYS)
      .initialCapacity(64)
      .maximumSize(20480)
      .build(object : CacheLoader<String, UserSecurityEntity.UserSecurityDelegateEntity<*>>() {
        override fun load(roleId: String?): UserSecurityEntity.UserSecurityDelegateEntity<*> {
          // No cache, just redirect to login
          throw AccountExpiredException("Require re-login.")
        }
      })

  override fun queryByAccountIs(account: String): UserSecurityEntity.UserSecurityDelegateEntity<*>? {
    return try {
      userTokenCache[account]

    } catch (exp: Exception) {
      null
    }
  }

  override fun caching(account: String, user: UserSecurityEntity.UserSecurityDelegateEntity<*>) {
    userTokenCache.put(account, user)
  }

  override fun remove(account: String) {
    userTokenCache.invalidate(account)
  }
}

abstract class AbstractValidationImageComponent {
  private val producer: Producer by lazy {
    val kaptcha = DefaultKaptcha()
    kaptcha.config = getConfigure()

    kaptcha
  }

  private val validationCodeCache = CacheBuilder.newBuilder()
      .expireAfterAccess(0, TimeUnit.SECONDS)
      .expireAfterWrite(15, TimeUnit.MINUTES)
      .build<String, String>()

  private val loginCounter = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.DAYS)
      .build(object : CacheLoader<String, AtomicInteger>() {
        @ParametersAreNonnullByDefault
        override fun load(key: String): AtomicInteger {
          return AtomicInteger(0)
        }
      })

  protected abstract fun getConfigure(): Config

  fun createImage(account: String): BufferedImage {
    val code = producer.createText()
    validationCodeCache.put(account, code)

    return producer.createImage(code)
  }

  fun countingFor(account: String) {
    try {
      loginCounter.get(account).incrementAndGet()

    } catch (ignored: ExecutionException) {
    }
  }

  fun isRequireValidation(account: String): Boolean {
    return try {
      loginCounter.get(account).get() >= getMaxTryTime()

    } catch (exp: ExecutionException) {
      false
    }
  }

  fun validation(account: String, code: String): Boolean {
    if (validationCodeCache.asMap().containsKey(account)) {
      return try {
        val isExist = code.equals(validationCodeCache.get(account) { "" }, true)

        validationCodeCache.invalidate(account)
        loginCounter.invalidate(account)

        isExist

      } catch (exp: ExecutionException) {
        false
      }
    }

    return true
  }

  fun clear(account: String) {
    loginCounter.invalidate(account)
  }

  open protected fun getMaxTryTime(): Int {
    return 3
  }
}

@Component
class DefaultValidationImageComponent : AbstractValidationImageComponent() {
  override fun getConfigure(): Config {
    val properties = Properties()
    properties.setProperty("kaptcha.border", "no")
    properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise")
    properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")

    properties.setProperty("kaptcha.image.width", "130")
    properties.setProperty("kaptcha.image.height", "45")
    properties.setProperty("kaptcha.textproducer.font.size", "32")

//        properties.setProperty( "kaptcha.noise.color", "black" );
    properties.setProperty("kaptcha.textproducer.font.color", "244,86,55")

    properties.setProperty("kaptcha.textproducer.char.length", "4")
    properties.setProperty("kaptcha.textproducer.font.names", "Monaco,Consolas,微软雅黑")

    val producer = DefaultKaptcha()
    producer.config = Config(properties)

    return Config(properties)
  }
}