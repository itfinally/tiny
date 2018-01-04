#### 前言
这是一个基于 spring-security 编写的权限控制模块,
为了实现比较基础的权限控制, 查了比较多的资料并写出这一个模块.

整个模块以 jwt 做为基础, 依赖 [jjwt](https://github.com/jwtk/jjwt) 项目生成 token,
并且重写了一部分 security 的组件, 被重写的组件如下:
 * service.AccessForbiddenHandler                   | 校验失败或权限不足拒绝访问时的处理器
 * web.component.JwtAuthenticationProcessingFilter  | 登陆验证
 * web.component.JwtAuthorizationFilter             | 访问身份验证
 * service.UserDetailService                        | 用户信息加载
 * service.PermissionValidService                   | 方法级别的访问权限验证
 * web.component.JwtAuthenticationToken             | 用户身份载体
 
在 SecurityConfigure 中, 可以见到类的顶部有一个 `@Order( SecurityProperties.ACCESS_OVERRIDE_ORDER )` 注解.
这是为了将来整合 Spring-Security-OAuth2 时, 降低权限验证拦截器的优先级, 优先给 OAuth2 进行调用方验证.

在默认情况下, 关闭了基于 http/表单 的登陆, 取消 csrf 校验, session 存留以及拒绝匿名访问, 并且开启了方法级别的权限校验. 

本来打算设计成连带 url 都是可配置式的, 但是考虑到这种 资源-权限 的变更情况比较罕见, 
而且设计出来还要考虑 url 的请求方法, 所以采用直接在方法上 @PreAuthorize 注解写 spEL 表达式的方式进行校验, 
对应的校验器是 `top.itfinally.security.service.PermissionValidService`.

#### 模块说明
内置的端点如下:
 * /verifies/login                  | 登陆
 * /authorization
    * /add_permission               | 增加权限
    * /add_role                     | 增加角色
    * /grant_role_to/{authorityId}  | 修改 {authorityId} 用户的角色
    * /grant_permission_to/{roleId} | 修改 {roleId} 角色的权限
    * /get_roles                    | 获取当前账户的所有角色
    * /get_permissions              | 获取当前账户的所有权限
 * /admin
    * /initialization               | 初始化系统数据
    * /create                       | 创建管理员账户
    * /lock                         | 禁用管理员账户
    
#### 端点说明

##### /verifies/login
```
GET /verifies/login
    headers:
        Authorization: Basic Base64( "username:password" )

    response:
        status:
        message:
        result: token
```
Base64( "account:password" ) 就是说将 account 和 password 用 ":" 拼接在一起然后用 base64 编码.
其中 "Basic" 前缀与登陆信息( 就是 base64 编码完的那一戳 )之间有且仅有一个空格.

同时, header 的名称固定叫 Authorization, 这是写死在代码里面的, 少个子都不行.

这个接口有个问题, 就是恶意用户可以通过频繁的请求进行缓存击穿直接拖垮登录服务, 当 Basic 前缀正确, 
且 base64 编码前的格式是 xxx:yyy 时便会通过前置的校验直接落到 UserDetailService 上.

好伐, 并不是不想解决这个问题, 主要是根本想不到有什么比较万全的策略, 解决方法很多, 
但都是有场景的, 干脆留给开发者好了. ( 突然感觉自己的做法好像 struts2 那种无良商家, 逃 = =||

开发者应当继承 `top.itfinally.security.service.UserDetailService` 并实现 `loadUserByAccount`,
在此方法内做对应的防御, 在 UserDetailService 内有一个名为 Default 的实现但没有做对应的防御.

还有就是, 因为暂时还没重写 successHandler/failedHandler( 流程还没想好 ), 所以没办法提供对应后处理流程.

##### /authorization/add_permission
```
POST /authorization/add_permission
    headers:
        Authorization: Bearer token
        
    body:
        name: ${通过该字段判断权限, 忽略大小写, 不能重复定义}
        description: ${这是个给人看的字段, 不能重复定义}
```
跟上面一样, Bearer 与 token 之间有且仅有一个空格.
不过这里有个注意的地方, <strong>所有在 /authorization 下的端点都只能被 ADMIN 角色访问.</strong>

##### /authorization/add_role
```
POST /authorization/add_role
    headers:
        Authorization: Bearer token
    
    body:
        name: ${通过该字段判断角色, 忽略大小写, 不能重复定义}
        description: ${这是个给人看的字段, 不能重复定义}
```
有问题为什么不先问问`/authorization/add_permission`呢?

##### /authorization/grant_role_to/{authorityId}
```
POST /authorization/grant_role_to/{authorityId}
	headers:
		Authorization: Bearer token
		Content-Type: application/json
		
	params:
		authorityId: ${瞅啥瞅, 这里传个用户的 authorityId}
		
	body:
	    roleIds
```
这里注意 body, 内容与前两个接口不同, 因为是用了 `@RequestBody` 声明 `roleIds` 参数.
代表整个 body 被这个参数独占, 传值时只需要用 json 把对应的数组集合转换然后传入即可, 比如 
`javascript: JSON.stringify( array )` 或者 `java: json.writeAsString( new ArrayList() )` 这样.

当然别忘了在 headers 上添加 `Content-Type: application/json`, 否则会收到 415 状态.

这个接口的使用方法是这样, 如果要在用户上添加某种角色, 在 `roleIds` 内加入即可. 
删除同理, 在 `roleIds`  内 remove 掉即可, 同时如果有些角色是之前就存在, 但是不想修改( 删除 ),
也要加入到 `roleIds` 内. 该接口会把用户当前所拥有的权限全数取出然后与接收到的集合做一次集合运算,
计算出需要 新增/删除 的角色, 简而言之:

* 以前有什么角色, 而你又不想删掉的, 请放入 `roleIds`
* 现在没有的角色, 而你又想赋予的, 请放入 `roleIds`
* 以前有什么角色, 而你想删掉的, 请不要放入 `roleIds`

##### /authorization/grant_permission_to/{roleId}
```
POST /grant_permission_to/{roleId}
    headers:
        Authorization: Bearer token
        Content-Type: application/json
        
    params:
        roleId: ${瞅啥瞅, 这里传个角色的 id}
        
    body:
        permissionIds ( tip: 这是个数组 )
```
有问题为什么不先问问`/authorization/grant_role_to/{authorityId}`呢?

##### /authorization/get_roles
```
GET /authorization/get_roles
    headers:
        Authorization: Bearer token
    
    response:
        status:
        message:
        result: [role1, role2, ...]
```

##### /authorization/get_permissions
```
GET /authorization/get_permissions
    headers:
        Authorization: Bearer token
            
    response:
        status:
        message:
        result: [permission1, permission2, ...]
```

##### /admin/initialization
```
GET /admin/initialization
```
直接调用就是了. 在默认情况下下, /admin 内所有端点都只能在本地( 也就是 127.0.0.1 )访问.
这也是考虑到不要给什么阿猫阿狗随便就能远程调用到而设置的最简单的防御.
假如再加一个账户身份验证的东西只会陷入"先有鸡还是先有蛋"的怪圈.

实际上这里的处理比较奇怪, 为了绕开 spring-security 禁用匿名的限制, 直接在 filter 上调用 service, 
详细的源码请查阅`top.itfinally.security.web.component.AdminManagerFilter`

##### /admin/create
```
GET /admin/create
```
有问题为什么不先问问`/admin/initialization`呢?

##### /admin/lock
```
GET /admin/lock
```
这个接口是用于禁用管理员账户的, 以防万一. 
至于为什么没有对应的 unlock 接口, 这不是也以防万一嘛, 要重新启用管理员账户, 自己去数据库
把账户的 status 设为 1 即可重新启用.
    
#### 使用说明

1. 使用 resource 目录下的 security.sql 创建表
2. 通过 curl 调用 get /admin/initialization 创建初始数据
3. 再一次用 curl 调用 get /admin/create 创建超级管理员账户, 默认账户密码均为 admin

最后, 记得修改管理员账户的密码.


#### 开发说明

这个当然不能直接用啦, 毕竟业务不一样, 先说说怎么覆盖默认实现.

提个醒, <strong>所有覆盖的类, 除了加上诸如 @Service, @Component 之类的注解, 必须再加上 @Primary 注解来告诉 
Spring 在多实现的情况下优先使用你的实现, 否则 Spring 容器会直接报错.
</strong>

理论上, 所有的组件都可以通过上述方法进行重写覆盖, 不过建议还是别那么调皮.
下面列出所有可覆盖的组件.

##### AccessForbiddenHandler 异常及拒绝访问的返回值处理
 这个类实现了 AuthenticationEntryPoint, AccessDeniedHandler 两个接口, 顾名思义, 
 当请求进入到身份验证流程( AuthenticationManager 阶段 )或者权限验证流程( PermissionEvaluator 阶段 )时,
 将捕捉 AuthenticationException, AccessDeniedException 两个异常进行统一返回.
 
 因为在 JwtAuthenticationProcessingFilter 内, 把这个处理器也加了进去, 
 所以如果登陆失败时, 也会转到这个处理器统一返回.
 
 说白了, 就是为了统一返回实体而实现的, 当然你也可以重写这个实现, 返回你自己的响应实体.
 你也可以定制你的实体, 通过继承 `top.itfinally.core.vo.BaseResponseVoBean` 添加你需要的字段.
 
 记得实体是分为:
 * 基类( BaseVoBean )
 * 单对象( SingleResponseVoBean )
 * 列表对象( CollectionResponseVoBean )
 * 字典对象( MapResponseVoBean )
 
 当然了, 已经存在的字段还是不能改的, 除非前端不打算统一规范或者
 是 JavaScript 这种弱类型语言.
 
 ( 其实像我这种早就不要脸的人觉得, 如此完美的实体定义, 这TM还要改?  )
 
##### UserDetailCachingService 用户信息缓存

这个类建议墙裂重写, 默认实现是使用 guava 的 CacheBuilder 创建的缓存集合. 默认超时时间为 30 天.
而且默认的缓存失效处理是直接返回 null, 不建议在生产时使用默认实现.

不设置缓存失效处理是因为访问验证和用户登录是分离的, 这个组件属于访问验证的一部分.

这里的缓存不需要计较失效时间对缓存的影响, 因为拿不到身份的话直接就当 401 状态处理, 
用户必须重新登录才能登陆, 中间的时间不存在雪崩问题.( app 自动登陆另当别论, 这是个注意点 )

嗯, 说得好像有多少流量一样, OA 组件还计较辣么多, mdzz.  = =||
 
##### UserDetailService 用户信息获取

这个东西是重点, 先说说当初设计时是怎么想的.

spring security 给出的必须的字段很明确, 就是一个用户的用户名密码及其账户状态, 通过六个必要的字段来构成用户信息.
那么在实现的时候, 如果要做到既实现 spring security 要求的信息, 又能便于重写用户信息, 
那只能把用户的基础信息拆开, 分成 UserDetail/UserAuthority 两个实体, UserAuthority 作为用户信息的附加描述和用户角色的关联.

所以整套工具的权限角色, 仅仅与 UserAuthority 进行绑定, 
完全不需要知道 UserDetail 是什么东西从哪里来, 甚至不需要知道 UserDetail 对应的 dao 层 api, 
在 UserAuthority 与 UserDetail 之间, 有的联系仅仅是 UserDetail 之间的 一个 authorityId.
(这个 authorityId 仅仅是存储在 UserDetail 伤的一个普通的字符串类型的字段, 数据库及其dao层上绝不可设计成任何形式的外键)

因此整个登录流程如下:
```
        user request  ->      ...... ( 略过 security 内部流程 ) 
                                |
                          JwtAuthenticationProcessingFilter
                                |
                              ...... ( 略过 security 内部流程 )
                                |
                          UserDetailService  <-- proxy and return --> UserDetailService implement
                                |
                              ...... ( 略过 security 内部流程 )
                                |
        user response <-  UserDetailCachingService & JwtTokenService
```

其中 UserDetailService implement 这一步就是需要继承 `top.itfinally.security.service.UserDetailService` 并实现的类,
啰啰嗦嗦说那么多就是为了这个, 当然本身也有一个默认实现, 叫做 `UserDetailService.Default`, 
如果觉得这个就OK的话, 可以直接使用, 对应的数据表是 `security_default_user`.

如果是自己重新实现( 多数情况下都是 ) UserDetailService, 意味着对应的实体也要重新实现,
对应的用户实体是 `top.itfinally.security.repository.po.AbstractUserDetailsEntity`, 而且在设计用户注册时必须注入 `top.itfinally.security.service.AuthorizationService` 
并且传入注册用户的 id( 注意不是 authorityId ) 调用 register 方法, 否则拦截器永远拒绝该用户的访问.

注册的流程大概如下:
```
UserDetailEntity user = new YourUserDetailEntity();
yourUserDao.save( user );
authorizationService.register( user.getId() );
```

#### 最后
其实最好还是对 spring-security 有个感性的认知, 列一下当初查阅过比较有用的资料:

* [JSON Web Token (JWT) in Spring Security -  a real-world example](https://www.linkedin.com/pulse/json-web-token-jwt-spring-security-real-world-example-boris-trivic)
* [REST Security with JWT using Java and Spring Security](https://www.toptal.com/java/rest-security-with-jwt-spring-security-and-java)
* [重拾后端之Spring Boot（四）：使用JWT和Spring Security保护REST API](http://www.jianshu.com/p/6307c89fe3fa)
* [集成JWT到Spring Boot项目](http://www.saily.top/2016/12/08/spring-boot-jwt/)

无论有多少资料, 其实最好也是最快的认知方式, 就是通过 debug + 各种博客 摸清整个框架的流程, 
然后再尝试跟着源码一块块去边抄边写, 慢慢理解作者的意图, 即使最后失败( 像这种工业级框架一个人写必然是会失败的 ),
也能收获不少.