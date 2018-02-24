package top.itfinally.admin.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.itfinally.core.vo.BaseResponseVoBean;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.itfinally.core.enumerate.ResponseStatusEnum.SERVER_ERROR;

@Component
public class SystemErrorInterceptor extends OncePerRequestFilter {
  private Logger logger = LoggerFactory.getLogger( getClass() );
  private ObjectMapper jsonMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws IOException {
    try {
      filterChain.doFilter( request, response );

    } catch ( Exception allException ) {
      logger.error( allException.getMessage(), allException.getCause() );
      response.getWriter().write( jsonMapper.writeValueAsString( new BaseResponseVoBean<>( SERVER_ERROR ) ) );
    }
  }
}
