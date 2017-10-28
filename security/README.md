[TOC]

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
 
#### 模块说明
在默认情况下, 关闭了基于 http/表单 的登陆, 取消 csrf 校验, session 存留以及拒绝匿名访问. 
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
不过这里有个注意的地方, *所有在 /authorization 下的端点都只能被 ADMIN 角色访问.*

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
直接调用就是了, 在 /admin 下的端口都不需要登陆, 只要是在本地( 127.0.0.1 )调用即可.
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

在默认情况下下, /admin 内所有端点都只能在本地( 也就是 127.0.0.1 )访问.
这也是考虑到不要给什么阿猫阿狗随便就能远程调用到而设置的最简单的防御.
假如再加一个账户身份验证的东西只会陷入"先有鸡还是先有蛋"的怪圈.

1. 使用 resource 目录下的 security.sql 创建表
2. 通过 curl 调用 get /admin/initialization 创建初始数据
3. 再一次用 curl 调用 get /admin/create 创建超级管理员账户, 默认账户密码均为 admin

最后, 记得修改管理员账户的密码.


#### 开发说明

