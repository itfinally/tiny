package top.itfinally.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BasicConfigure {

  @Bean
  open fun getJsonMapper(): ObjectMapper {
    return ObjectMapper()
  }
}