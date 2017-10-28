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
 
 
#### 模块说明
在默认情况下, 关闭了基于 http/表单 的登陆, 取消 csrf 校验, session 存留以及拒绝匿名访问. 
内置的端点如下:
 * /verifies/login                  | 登陆
 * /authorization
    * /add_permission               | 增加权限
    * /add_role                     | 增加角色
    * /grant_role_to/{authorityId}  | 修改 {authorityId} 用户的角色
    * /grant_permission_to/{roleId} | 修改 {roleId} 角色的权限
    * /get_roles
    * /get_permissions
 * /admin
    * /initialization               | 初始化系统数据
    * /create                       | 创建管理员账户
    * /lock                         | 禁用管理员账户
    
#### 使用说明

1. 使用 resource 目录下的 security.sql 创建表
2. 在本地通过 curl 调用 get /admin/initialization 创建初始数据
3. 再一次在本地用 curl 调用 get /admin/create 创建超级管理员账户, 默认账户密码均为 admin

最后, 记得修改管理员账户的密码.


#### 开发说明

