package top.itfinally.security.web

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import top.itfinally.security.component.UserSecurityHolder
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class UserSecurityInjectInterceptor : OncePerRequestFilter() {
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    try {
      // 本类仅作为 UserSecurityHolder 的辅助类
      UserSecurityHolder.initContext()
      filterChain.doFilter(request, response)

    } finally {
      UserSecurityHolder.removeContext()
    }
  }
}