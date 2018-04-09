package top.itfinally.security.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.EventBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import top.itfinally.core.web.ResponseStatus.SUCCESS
import top.itfinally.core.web.ResponseStatus.UNAUTHORIZED
import top.itfinally.core.web.SingleResponse
import top.itfinally.security.AccountResetEvent
import top.itfinally.security.repository.entity.AbstractUserDetail
import top.itfinally.security.repository.entity.UserSecurityEntity
import java.nio.charset.Charset
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtLoginProcessingFilter : AbstractAuthenticationProcessingFilter("/verifies/login") {

  @Autowired
  @Qualifier("securityEventBus")
  private
  lateinit var eventBus: EventBus

  @Autowired
  private
  lateinit var jsonMapper: ObjectMapper

  @Autowired
  private
  lateinit var jwtTokenComponent: AbstractJwtTokenComponent

  @Autowired
  private
  lateinit var accessForbiddenHandler: AbstractAccessForbiddenHandler

  @Autowired
  private
  lateinit var validationImageComponent: AbstractValidationImageComponent

  @Autowired
  private
  lateinit var userDetailCachingComponent: AbstractUserDetailCachingComponent

  @Autowired
  override fun setAuthenticationManager(authenticationManager: AuthenticationManager) {
    super.setAuthenticationManager(authenticationManager)
  }

  override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
    var token = request.getHeader("Authorization")
    val validCode = request.getParameter("verifyCode")

    if (StringUtils.isEmpty(token) || !token.startsWith("Basic ")) {
      throw BadCredentialsException("Missing token in request headers.")
    }

    token = String(Base64Utils.decode(token.substring(6).toByteArray()), Charset.forName("UTF-8")).trim()

    if (!token.contains(":") || !token.matches("\\w+:\\w+".toRegex())) {
      throw BadCredentialsException("Invalid basic authentication token.")
    }

    val entry = token.split(":").toTypedArray()
    if (validationImageComponent.isRequireValidation(entry[0]) && !validationImageComponent.validation(entry[0], validCode)) {
      throw BadCredentialsException("Wrong verification code.")
    }

    val authToken = UsernamePasswordAuthenticationToken(entry[0], entry[1])

    try {
      return authenticationManager.authenticate(authToken)

    } catch (exp: AuthenticationException) {
      validationImageComponent.countingFor(entry[0])
      throw exp
    }
  }

  override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse,
                                        chain: FilterChain?, authResult: Authentication) {

    val userSecurityDelegateEntity = authResult.principal as UserSecurityEntity.UserSecurityDelegateEntity<*>
    val token = jwtTokenComponent.create(userSecurityDelegateEntity.getUsername())

    validationImageComponent.clear(userSecurityDelegateEntity.getUsername())
    userDetailCachingComponent.caching(userSecurityDelegateEntity.getUsername(), userSecurityDelegateEntity)

    // active 'userDetailCachingComponent' to reset user status
    eventBus.post(AccountResetEvent(userSecurityDelegateEntity.getUsername()))

    response.contentType = "application/json;charset=UTF-8"
    response.writer.write(jsonMapper.writeValueAsString(SingleResponse<String>(SUCCESS).setResult(token)))
  }

  override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException) {
    accessForbiddenHandler.commence(request, response, failed)
  }
}

@Component
class JwtAuthorizationFilter : OncePerRequestFilter() {
  private val authenticationDetailsSource = WebAuthenticationDetailsSource()

  @Autowired
  private
  lateinit var jsonMapper: ObjectMapper

  @Autowired
  private
  lateinit var jwtTokenComponent: AbstractJwtTokenComponent

  @Autowired
  private
  lateinit var userDetailCachingComponent: AbstractUserDetailCachingComponent

  @Autowired(required = false)
  private
  var userSecurityComponent: BasicUserSecurityComponent<*>? = null

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    if (null == userSecurityComponent) {
      throw UnsupportedOperationException("Please implement interface 'BasicUserSecurityComponent' before use it.")
    }

    var token = request.getHeader("Authorization")

    if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
      filterChain.doFilter(request, response)
      return
    }

    token = token.substring(6).trim()
    val account = jwtTokenComponent.queryByTokenIs(token)

    if (null == account || account.isBlank()) {
      filterChain.doFilter(request, response)
      return
    }

    var userSecurityDelegateEntity = userDetailCachingComponent.queryByAccountIs(account)

    if (null == userSecurityDelegateEntity) {
      response.writer.write(jsonMapper.writeValueAsString(SingleResponse<Any>(UNAUTHORIZED).setMessage("Token expired.")))
      return
    }

    // refresh account when someone change it
    if (userDetailCachingComponent.isChange(userSecurityDelegateEntity.getUsername())) {
      val userName = (userSecurityDelegateEntity.getUser() as AbstractUserDetail).getUsername()
      userSecurityDelegateEntity = userSecurityComponent!!.loadUserByUsername(userName) as UserSecurityEntity.UserSecurityDelegateEntity<*>

      userDetailCachingComponent.caching(account, userSecurityDelegateEntity)
    }

    val authResult = JwtAuthenticationToken(token, userSecurityDelegateEntity)

    authResult.details = authenticationDetailsSource.buildDetails(request)

    SecurityContextHolder.getContext().authentication = authResult

    filterChain.doFilter(request, response)
  }
}

@Component
class JwtLogoutHandler : LogoutHandler, LogoutSuccessHandler {

  @Autowired
  private
  lateinit var jsonMapper: ObjectMapper

  @Autowired
  private
  lateinit var userDetailCachingComponent: AbstractUserDetailCachingComponent

  override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
    authentication.isAuthenticated = false

    val securityDelegateEntity = authentication.principal
        as? UserSecurityEntity.UserSecurityDelegateEntity<*> ?: return

    userDetailCachingComponent.remove(securityDelegateEntity.getUsername())
  }

  override fun onLogoutSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
    response.contentType = "application/json;charset=UTF-8"
    response.writer.write(jsonMapper.writeValueAsString(SingleResponse<Any>(SUCCESS).setMessage("Logout successful.")))
  }
}