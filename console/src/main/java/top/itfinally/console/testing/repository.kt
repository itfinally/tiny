package top.itfinally.console.testing

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Base64Utils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import top.itfinally.console.repository.MenuItemRepository
import top.itfinally.console.repository.entity.MenuItemEntity
import top.itfinally.core.BasicRepository
import top.itfinally.security.component.BasicUserSecurityComponent
import top.itfinally.security.repository.RoleRepository
import top.itfinally.security.repository.UserRoleRepository
import top.itfinally.security.repository.UserSecurityRepository
import top.itfinally.security.repository.entity.AbstractUserDetail
import top.itfinally.security.repository.entity.UserRoleEntity
import top.itfinally.security.repository.entity.UserSecurityEntity
import java.nio.charset.Charset
import javax.persistence.NoResultException

@Repository
@Transactional
open class UserDetailRepository : BasicRepository<UserDetailEntity>() {

  @Autowired
  private
  lateinit var userSecurityRepository: UserSecurityRepository

  @Autowired
  lateinit var passwordEncoder: PasswordEncoder

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  open fun queryByAccountIs(account: String): UserDetailEntity? {
    val runtime = QueryRuntime()
    val table = runtime.table
    val builder = runtime.builder

    runtime.select(table).where(builder.equal(table.get<String>("username"), account))

    return try {
      entityManager.createQuery(runtime.build()).singleResult

    } catch (exp: NoResultException) {
      null
    }
  }

  override fun save(entity: UserDetailEntity): UserDetailEntity {
    val userSecurity = UserSecurityEntity()
    entity.userSecurityId = userSecurity.id
    entity.password = passwordEncoder.encode(entity.password)

    userSecurityRepository.save(userSecurity)
    return super.save(entity)
  }
}

@Primary
@Component
class UserSecurityComponent : BasicUserSecurityComponent<UserDetailEntity>() {

  @Autowired
  lateinit var userDetailRepository: UserDetailRepository

  override fun loadUserByAccount(username: String): AbstractUserDetail<UserDetailEntity>? {
    return userDetailRepository.queryByAccountIs(username)
  }
}

@Service
class UserService {

  @Autowired
  lateinit var userDetailRepository: UserDetailRepository

  fun register(entity: UserDetailEntity) {
    userDetailRepository.save(entity)
  }
}

@RestController
@RequestMapping("/testing")
class UserController {

  @Autowired
  lateinit var userService: UserService

  @PostMapping("/register")
  fun register(@RequestParam("basic") basic: String) {
    val entry = String(Base64Utils.decode(basic.toByteArray()), Charset.forName("utf-8")).split(":")
    val account = entry[0]
    val password = entry[1]
    userService.register(UserDetailEntity().setUsername(account).setPassword(password))
  }
}

@Component
@DependsOn("securityInitComponent")
open class AccountInitComponent : ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  lateinit var userService: UserService

  @Autowired
  lateinit var userDetailRepository: UserDetailRepository

  @Autowired
  lateinit var roleRepository: RoleRepository

  @Autowired
  lateinit var userSecurityRepository: UserSecurityRepository

  @Autowired
  lateinit var userRoleRepository: UserRoleRepository

  @Autowired
  lateinit var menuItemRepository: MenuItemRepository

  override fun onApplicationEvent(event: ContextRefreshedEvent) {
    if (userDetailRepository.queryByAccountIs("admin") != null) {
      return
    }

    val user = userDetailRepository.save(UserDetailEntity().setUsername("admin").setPassword("admin"))
    val userSecurity = userSecurityRepository.queryByIdIs(user.userSecurityId)
    val role = roleRepository.queryByNameIs("admin")

    userRoleRepository.save(UserRoleEntity().setRole(role).setUserSecurity(userSecurity))

    val root1  = MenuItemEntity().setRoot(true).setLeaf(false).setName("1")
    val node12 = MenuItemEntity().setRoot(false).setLeaf(false).setName("1-2")
    val node13 = MenuItemEntity().setRoot(false).setLeaf(false).setName("1-3")
    val node131 = MenuItemEntity().setRoot(false).setLeaf(false).setName("1-3-1")
    val node132 = MenuItemEntity().setRoot(false).setLeaf(false).setName("1-3-2")

    val root2 = MenuItemEntity().setRoot(true).setLeaf(false).setName("2")
    val node23 = MenuItemEntity().setRoot(false).setLeaf(false).setName("2-3")

    menuItemRepository.save(root1, root1.id)
    menuItemRepository.save(root2, root2.id)
    menuItemRepository.save(node12, root1.id)
    menuItemRepository.save(node13,  root1.id)
    menuItemRepository.save(node131, node13.id)
    menuItemRepository.save(node132, node13.id)
    menuItemRepository.save(node23, root2.id)
  }
}