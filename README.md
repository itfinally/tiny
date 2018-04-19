# Tiny - 轻量级后台管理框架

在日常的 Java web 开发中, 大多数工作都是重复劳动, 本想找有没有现成的, 然而大部分框架都是零几年那时候创建, 其中的技术提升不了工作效率, 还可能因为填坑而浪费大量时间, 因而编写出这个框架.

Java web 项目基本都要求有一个后台管理系统, 该框架目的在于解决权限管理及访问控制这一重用度极高的功能, 安全框架使用了 Spring-Security, 所以要求使用者至少了解 Spring-Security 的基础流程. 

如果未曾接触过 Spring-Security 也请先看看, 整体设计不会很难理解, 整个安全验证流程除去一堆复杂的继承以外, 实际上只是一条责任链, 只要其中一个环节校验失败都等同于拒绝访问.

另外该项目使用 Java8 与 Kotlin 混编, 并且是基于 Spring-boot 框架编写, 使用时可以与 Java 无缝对接, 但要求 Java 版本必须为 8, 因为该项目使用了 lambda 表达式, 暂时没有提供 Java 9 及其以上版本的 package-info 描述文件. 另外该项目所有接口均采用 Restful 风格.

整个项目的结构如下:

 * parent - 空项目, 描述 maven 依赖
 * core - 公共代码包
 * security - 权限模块
 * console - 后台管理模块

其中后台管理模块有一个配套的前端项目, 同样叫 Tiny, 采用 TypeScript 编写, 使用 Vue, IView 进行搭建, 文档另附.

<strong>在覆盖任何框架默认组件时, 必须使用 `@Primary` 注解标记, 即通知 Spring 碰到重复实现时优先使用该实现.</strong>

## 使用说明

由于无法定义用户信息, 因此该项目不属于开箱即用型, 需要开发者自行添加空缺的组件方可使用.

 1. 首先需要实现 `AbstractUserDetail`, 定义自己的用户实体.

 ```java
 public abstract class AbstractUserDetail<Entity extends BasicEntity<Entity>> extends BasicEntity<Entity> {
  public abstract String getPassword();

  // 该字段必须唯一, 因为登陆与访问验证时均使用 username 进行查询
  public abstract String getUsername();

  public abstract String getUserSecurityId();
}
 ```
 
 2. 其次需要实现 `BasicUserSecurityComponent` 抽象类, 其中的 `loadUserByAccount` 方法返回步骤一定义的 `AbstractUserDetail` 实体即可. 如果当前 username 没有对应的用户信息, 返回 null 即可.

 ```kotlin
 abstract class BasicUserSecurityComponent<Entity : AbstractUserDetail<Entity>> : UserDetailsService {
     abstract fun loadUserByAccount(username: String): AbstractUserDetail<Entity>?
}
 ```
 
 3. 然后需要编写一个类来继承 `SecurityConfigure` 来开启 Spring-Security

 ```kotlin
 @EnableWebSecurity
 @Order(SecurityProperties.BASIC_AUTH_ORDER)
 @EnableGlobalMethodSecurity(prePostEnabled = true)
 open class ConsoleSecurityConfigure : SecurityConfigure() {
 }
 ```
 
 如果需要添加自己的配置, 一定要在调用 `super.configure(http)` 之前设置
 ```kotlin
 override fun configure(http: HttpSecurity) {
     // you configuration
 
     super.configure(http)
 }
 ```

 4. (可选) 默认情况下是禁止跨域访问, 如果要跨域还需要继承 Spring 的 `WebMvcConfigurer` 并实现 `addCorsMappings` 方法, 并且该类需要配置为 `@Configuration`, 详情可参考 Spring 文档

 5. 最后, 因为尚未编写 starter, 因此需要在 `@SpringBootApplication` 的 scanBasePackages 中加入 "top.itfinally".

  ```
  @SpringBootApplication(scanBasePackages = {"top.itfinally"})
  ```

值得注意的是, 在用户信息中 UserSecurityId 是将用户与整个权限控制关联的字段, 在整个控制流程内真正流通的是 UserSecurity 对象, 相当于一个人的身份证. 因此在编写用户注册的流程时, 必须创建一个 UserSecurity 对象并且将其 Id 存储在用户信息内.

<strong>另外</strong>, 在为新用户加密密码时, 请使用 `PasswordEncoder` 对象加密, 该对象直接使用 `@Autowired` 注入即可使用, 当然你也可以使用其他 `PasswordEncoder` 的实现, 但一定要把该实现放入 Spring 内并标注 `@Primary`, 否则在校验时使用的实现与加密使用的实现不一致时将导致无法登陆.

 
## 接口说明

HTTP 协议规定空出一行即表示 Header 结束, 此处遵循此规定, Header 空行后跟随的是 Body.

此外, 所有接口都通过 `BasicResponse` 及其子类返回相应的数据.

### 基础接口

* 登录:
  
  ```
  POST /verifies/login HTTP/1.1
  Authorization: Basic "Base64('username:password')"
  
  verifyCode='your verify code'
  ```
  
  verifyCode: 验证码, 超出指定次数后需要该参数, 否则属于可选参数
  
* 登出:
  
  ```
  GET /verifies/logout HTTP/1.1
  Authorization: Bearer 'your token'
  ```
  
* 验证码获取:

  ```
  GET /get_valid_image/{account}/{random} HTTP/1.1
  ```
  
  account: 用户名
  random: 随机数, 目的是为了防止浏览器缓存

### 权限模块接口

* 添加权限:

 ```
 POST /permission/add_permission HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='permission name'&description='permission description'
 ```
 
 name: 权限名
 description: 权限描述, 可选
 
* 删除权限:

 ```
 DELETE /permission/add_permission/{permissionId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 permissionId: 权限 Id
 
### 角色模块接口
 
* 新增角色:

 ```
 POST /role/add_role HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='role name'&description='role description ( option )'
 ```
 
 name: 角色名
 description: 角色描述, 可选
 
* 删除角色:
 
 ```
 POST /role/remove_role/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 roleId: 角色 Id
 
* 对指定角色添加权限:
 
 ```
 POST /role/add_permissions_to_role/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 roleId: 角色Id
 request body: 由权限 Id 组成的数组
 
* 删除指定角色的权限:
 
 ```
 DELETE /role/remove_permissions_from_role/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 roleId: 角色Id
 request body: 由权限 Id 组成的数组
 
* 查询指定角色的权限:

 ```
 GET /role/query_permissions_by_role_id_is/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 roleId: 角色Id
 
* 查询自己的权限:

 ```
 GET /role/query_own_permissions HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
### 部门模块接口

* 新增部门:

 ```
 POST /department/add_department HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='department name'&description='department description'
 ```
 
 name: 部门名称
 description: 部门名称
 
* 删除部门:

 ```
 POST /department/remove_department/{departmentId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 departmentId: 部门 Id
 
* 对指定部门增加权限:

 ```
 POST /department/add_roles_to_department/{departmentId} HTTP/1.1
 Authorization: Bearer 'your token'
 
 'request body'
 ```
 
 departmentId: 部门 Id
 request body: 由角色 Id 组成的数组
 
 

## 项目描述

由于源码大部分是使用 kotlin 编写, 因此关键字与 Java 不尽相同, 但只要有基本的编程意识和经验, 阅读本章节都不会有太多问题.

默认情况下, Kotlin 在 IDEA 内反编译也不会显示源码实现, 因此源码需要自行去 Github 上下载本项目进行查看.

项目最初是使用 Mybatis, 但考虑到大部分项目初期都是为了快速开发, 因此摒弃 Mybatis 并采用 Hibernate5. 在新版的 Hibernate 中, EntityManager 对象相当于过去版本的 Session 且线程安全, 同时可以精确控制 SQL 的生成, API 也更为精简和语义化.

另外本项目所有删除操作均为逻辑删除, 同时提供物理删除, 但不推荐使用.
为了避免时间出现问题, 实体在存储及运行时的时间单位均以毫秒为单位, 即存储长整型, 不推荐存储 Date 对象.

### core 模块

主要是公共代码, 比如:
 
 * BasicEntity - 数据库基础实体
 * BasicQuerySituation - 查询条件
 * BasicResponse - 响应实体
 * BasicVoBean - 响应数据基础实体
 * EntityStatus - 实体状态枚举
 * QueryStatus - 查询状态枚举
 * BasicRuntime - 简化 entityManager 查询的一个辅助类, 实现类是 BasicRepository 的一个内部类, 仅限 BasicRepository 及其子类使用
 * BasicRepository 基础 crud 抽象类

在创建自己的数据访问类时, 如果需要默认提供的能力, 可以继承 `BasicRepository` 类, 但相应地, 一个 Repository 只能对应一个实体, 并且该实体必须继承自 `BasicEntity` 类, 另外对应的 vo 实体也需要继承 `BasicVoBean` 类.

在返回 json 数据时, 响应的实体必须是 `BasicResponse`, 

### security 模块

#### 登陆

Spring-Security 的权限验证除开方法级别的校验流程外, 对 web 的校验实际上是通过责任链来完成, 并且最重要的一个组件便是 `AbstractAuthenticationProcessingFilter` 的子类, 默认实现是 `UsernamePasswordAuthenticationFilter`, 即普通的用户名-密码登陆. 该框架使用 jwt 作为通讯 token, 因此重新实现了该组件并覆盖默认实现, 即 `JwtLoginProcessingFilter`.

在该组件中比较重要的是这一小段流程:

```kotlin
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

// etc...
```

这段流程要求登陆时"用户名和密码"按"username:password"格式拼接成字符串, 并且以 Base64 编码该字符串, 然后通过 HTTP Header 发送, 即 `Authorization: Basic Base64('username:password')`. 其次, 如果登陆超出指定次数便会要求附带验证码, 即 `verifyCode: 'your verify code'`. 注意, 该验证码是通过 POST body 发送.

在通过上述组件后, token 会发送到 `BasicUserSecurityComponent` 的实现类中查询对应的用户, 该类需要开发者自行继承并实现用户查询. 返回用户信息后, 默认流程会把用户对应的所有角色以及所有所属部门的角色查询出来作为该用户最终拥有的角色.

最终如果登陆成功, 服务器响应如下:

```
{
	"code": 200,
	"message": "请求成功",
	"result": "your token"
}
```

对于超出指定登录次数而没有登陆成功的请求, 则要求输入验证码, 接口 `/get_valid_image/{account}/{random}` 会返回一个验证码图案的流数据, 直接用于 `<img/>` 标签即可.

其中 `AbstractValidationImageComponent` 存在一个默认实现 `DefaultValidationImageComponent`, 该实现给出一个普通的 kaptcha 图形配置, 默认的最大重试登陆次数为 3 次, 登陆大于等于 3 次则要求输入验证码.

你可以通过实现 `AbstractValidationImageComponent` 或者继承`DefaultValidationImageComponent` 然后覆盖 `getMaxTryTime` 方法来修改最大重试登陆次数.

注: kaptcha 是谷歌的一个图形验证码生成器

#### 访问验证

通过登陆后返回的 token, 用户可以访问任意其拥有角色所允许的接口, 这里涉及到两个组件.

首先是 AbstractJwtTokenComponent, 登陆后该组件会创建 token, 这里推荐重写该组件并给出更安全的 `java.security.Key` 实例, 比如用不对称加密的密匙. 默认实现使用的 Key 仅仅是使用一个普通字符串生成的 Key 实例. 详细配置可以查看 [jjwt](https://github.com/jwtk/jjwt).

其次是 AbstractUserDetailCachingComponent, 该组件在登陆成功后会缓存 token, 默认使用的缓存是 Guava 的 LoadingCache, 默认配置为写后( 即登陆后 ) 30 天失效, 最大可存储 20480 个 token.

登陆时给出的 token 必须以 Bearer 字符串开头, 并且与 token 只隔一个空格, 即: `Authorization: Bearer token`

#### 方法级别的访问验证

该框架的权限检查最终是通过 Spring-Security 提供的 `@PreAuthorize` 配合 SpEL 来提供检查, 其中真正的权限检查发生在 `PermissionValidationComponent`, 该组件会根据当前的用户拥有的角色的权限进行检查.

`hasPermission` 方法一共有两个签名, 该组件只开放这个签名的方法进行验证:
`hasPermission(targetDomainObject: Any, permission: Any)`

其中 `permission` 参数是用来定义访问当前方法需要什么权限, `targetDomainObject` 可以设定为 null, 在权限检查中不会用到该参数.

即:
```java
@PreAuthrize( null, "permission_name" )
public doSomething() {
 // code...
}
```

另外一个方法有三个参数, 但不清楚是为什么场景提供, 因此在该组件中永远返回一个 false.

最后总结一下可以重写的组件:

| 					组件名称 			|     描述                  |
| 					:-----:			|    :-----:                |
| AbstractJwtTokenComponent	| 创建 Token 的组件, 可选          |
| AbstractAccessForbiddenHandler | 未登陆或权限不足被拒绝访问时被调用的组件, 用于返回错误信息, 可选 |
| AbstractUserDetailCachingComponent | 登陆成功后缓存 Token 的组件, 用于访问前身份检查, 可选 |
| AbstractValidationImageComponent | 创建图形验证码的组件, 可选 |
| BasicUserSecurityComponent | 查询用户信息的组件, 必须 |
| PasswordEncoder	 | Spring-Security 密码加密组件, 可选 |


### console 模块

该模块主要为前端项目提供接口, 同时整合 security 模块, 提供菜单及其权限控制逻辑.


#### 菜单设计

菜单设计采用主副表形式设计, 主表记录菜单信息, 副表记录菜单节点关系.

| v1_menu_item |     v1_menu_relation  |
|  :---:   |     :---:   |
|  name   |   child_id   |
|  path   |   parent_id |
| is_root |  gap     |
| is_leaf |

其中 v1_menu_relation 表通过数据冗余的方式存储菜单关系, 也就是闭包表.

-> 下列是列名
parent - child - gap

-> 插入 menu1 根节点
menu1 -  menu1 - 0

-> 在 menu1 下插入一个子节点 menu2
menu1 - menu2 - 1
menu2 - menu2 - 0

-> 在 menu3 下插入一个子节点 menu3
menu1 - menu3 - 2
menu2 - menu3 - 1
menu3 - menu3 - 0

如上述所示, 每加入一个节点, 都需要与所有父节点形成新的记录( 包括自身, 也就是说自己是自己的子节点同时也是自己的父节点 )

因此在查询的时候无论是正向/反向,或指定某一代节点, 只需要执行一句简单查询即可:

```sql
select * from v1_menu_relation where parent_id = ? and gap = ?
```

另外在创建菜单树时, 采用的策略是广度优先遍历, 各子节点通过在父节点的子节点集合内找到自身, 利用指针的特性间接对父节点进行更新, 用最少的代码创建菜单树, 具体的代码可以查阅 `menu.kt` 文件.

#### 权限管理

整个权限控制设计规则如下:

 1. 每个角色都有一个 priority( 优先级 ), priority 的值越小, 优先级越高. 其中 0 优先级为最高级, 只能赋予名为 ADMIN 的角色
 2. 给部门赋予角色时只能赋予 priority 的值大于但不等于自身所有角色中 priority 值最小的角色
 3. 给其他角色赋权时必须拥有 "grant" 权限, 同时只能给出由自身所有角色所拥有的权限

创建菜单树时, 也会检查当前用户是否拥有可以使用当前菜单节点的角色, 所以相应地, 也会有针对菜单的授权, 菜单权限是通过角色来赋予的. 

一般地, 如果用户可以访问某个节点, 那必然能访问该节点的父节点, 如此反复直至根节点. 因此在赋权时, 必须在当前节点及其所有父节点建立与指定角色的关联. 



