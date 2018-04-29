# 接口说明

HTTP 协议规定空出一行即表示 Header 结束, 此处遵循此规定, Header 空行后跟随的是 Body.

此外, 所有接口都是 restful 风格, 并且响应时都通过 `BasicResponse` 及其子类返回相应的数据.

## 基础接口

* 登录:
  
  ```http
  POST /verifies/login HTTP/1.1
  Authorization: Basic "Base64('username:password')"
  
  verifyCode='your verify code'
  ```
  
  verifyCode: 验证码, 超出指定次数后需要该参数, 否则属于可选参数
  
* 登出:
  
  ```http
  GET /verifies/logout HTTP/1.1
  Authorization: Bearer 'your token'
  ```
  
* 验证码获取:

  ```http
  GET /get_valid_image/{account}/{now} HTTP/1.1
  ```
  
  account: 用户名
  
  now: 当前时间的时间戳, 目的是为了防止浏览器缓存

## 权限模块接口

* 添加权限:

 ```http
 POST /permission/add_permission HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='permission name'&description='permission description'&status='permission status'
 ```
 
 name: 权限名
 
 description: 权限描述, 可选
 
 status: 状态值, 可选
 
* 删除权限:

 ```http
 DELETE /permission/remove_permission/{permissionId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 permissionId: 权限 Id
 
* 查询个人权限:

 ```http
 GET /permission/query_own_permissions HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
* 根据多个条件查询权限元信息:

 ```http
 POST /permission/query_by_conditions_is HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由多个 key-value 组成的字典
 ```
 {
     id: 权限 id,
     status: 权限状态,
     createTimeStarted: 创建时间的起始时间,
     createTimeEnd: 创建时间的结束时间,
     updateTimeStarted: 更新时间的起始时间,
     updateTimeEnd: 更新时间的结束时间
 }
 ```
 
* 根据多个条件查询权限元信息的总数:

 ```http
 POST /permission/count_by_conditions_is HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由多个 key-value 组成的字典
 ```
 {
     id: 权限 id,
     status: 权限状态,
     createTimeStarted: 创建时间的起始时间,
     createTimeEnd: 创建时间的结束时间,
     updateTimeStarted: 更新时间的起始时间,
     updateTimeEnd: 更新时间的结束时间
 }
 ```
 
* 恢复指定的权限元信息:

 ```http
 POST /permission/recover_by_id_is/{permissionId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 permissionId: 权限 id
 
* 删除指定的所有权限元信息:

 ```http
 DELETE /permission/remove_all_by_id_in HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由权限 id 组成的数组
 
* 恢复指定的所有权限元信息:

 ```http
 POST /permission/recover_all_by_id_in HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由权限 id 组成的数组
 
* 更新权限元信息:

 ```http
 POST /permission/update HTTP/1.1
 Authorization: Bearer 'your token'
 
 permissionId='permission id'&status='permission status'&name='permission name'&description='permission description'
```
 
 permissionId: 权限 id
 
 status: 权限状态
 
 name: 权限名
 
 description: 权限描述
 
## 角色模块接口
 
* 新增角色:

 ```http
 POST /role/add_role HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='role name'&description='role description'&priority='role priority'&status='role status'
 ```
 
 name: 角色名
 
 description: 角色描述, 可选
 
 priority: 角色优先级, 值越小优先级越高
 
 status: 角色状态, 可选
 
* 删除角色:
 
 ```http
 DELETE /role/remove_role/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 roleId: 角色 Id
 
* 对指定角色添加权限:
 
 ```http
 POST /role/add_permissions_to_role/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 roleId: 角色Id
 
 request body: 由权限 Id 组成的数组
 
* 删除指定角色的权限:
 
 ```http
 DELETE /role/remove_permissions_from_role/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 roleId: 角色Id
 
 request body: 由权限 Id 组成的数组
 
* 查询指定角色的权限:

 ```http
 GET /role/query_permissions_by_role_id_is/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 roleId: 角色Id
 
* 查询可分配的角色:
 
 ```http
 GET /role/query_available_assign_roles HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
* 查询个人角色:
 
 ```http
 GET /role/query_own_roles HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
* 根据多个条件查询角色元信息:

 ```http
 POST /role/query_by_conditions_is HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由多个 key-value 组成的字典
 ```
 {
     id: 角色 id,
     status: 角色状态,
     createTimeStarted: 创建时间的起始时间,
     createTimeEnd: 创建时间的结束时间,
     updateTimeStarted: 更新时间的起始时间,
     updateTimeEnd: 更新时间的结束时间
 }
 ```
 
* 根据多个条件查询角色元信息的总数:

 ```http
 POST /role/count_by_conditions_is HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由多个 key-value 组成的字典
 ```
 {
     id: 角色 id,
     status: 角色状态,
     createTimeStarted: 创建时间的起始时间,
     createTimeEnd: 创建时间的结束时间,
     updateTimeStarted: 更新时间的起始时间,
     updateTimeEnd: 更新时间的结束时间
 }
 ```
 
* 恢复指定的角色元数据:

 ```http
 POST /role/recover_by_id_is/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 roleId: 角色 id
 
* 删除指定的所有角色元信息:

 ```http
 POST /role/remove_all_by_id_in HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由角色 id 组成的数组
 
* 恢复指定的所有角色元信息:

 ```http
 POST /role/recover_all_by_id_in HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由角色 id 组成的数组
 
* 更新指定的角色元数据:

 ```http
 POST /role/update HTTP/1.1
 Authorization: Bearer 'your token'
 
 roleId='role id'&status='role status'&name='role name'&description='role description'&priority='role priority'
 ```
 
name: 角色名
 description: 角色描述, 可选
 priority: 角色优先级, 值越小优先级越高
 status: 角色状态, 可选
 
## 部门模块接口

* 新增部门:

 ```http
 POST /department/add_department HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='department name'&description='department description'&status='department status'
 ```
 
 name: 部门名称
 
 description: 部门名称, 可选
 
 status: 部门状态, 可选
 
* 删除部门:

 ```http
 POST /department/remove_department/{departmentId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 departmentId: 部门 Id
 
* 对指定部门增加角色:

 ```http
 POST /department/add_roles_to_department/{departmentId} HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 departmentId: 部门 Id
 
 request body: 由角色 Id 组成的数组
 
* 删除指定部门的角色:

 ```http
 DELETE /department/remove_roles_from_department/{departmentId} HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 departmentId: 部门 Id
 request body: 由角色 Id 组成的数组
 
* 查询指定部门的角色:

 ```http
 GET /department/query_roles_by_department_id_is/{departmentId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 departmentId: 部门 Id
 
* 根据多个条件查询部门元信息:

 ```http
 POST /department/query_by_conditions_is HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由多个 key-value 组成的字典
 ```
 {
     id: 部门 id,
     status: 部门状态,
     createTimeStarted: 创建时间的起始时间,
     createTimeEnd: 创建时间的结束时间,
     updateTimeStarted: 更新时间的起始时间,
     updateTimeEnd: 更新时间的结束时间
 }
 ```
 
* 根据多个条件查询部门元信息的总数:

 ```http
 POST /department/count_by_conditions_is HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由多个 key-value 组成的字典
 ```
 {
     id: 部门 id,
     status: 部门状态,
     createTimeStarted: 创建时间的起始时间,
     createTimeEnd: 创建时间的结束时间,
     updateTimeStarted: 更新时间的起始时间,
     updateTimeEnd: 更新时间的结束时间
 }
 ```
 
* 恢复指定的部门元数据:

 ```http
 POST /department/recover_by_id_is/{roleId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 roleId: 部门 id
 
* 删除指定的所有部门元信息:

 ```http
 POST /department/remove_all_by_id_in HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由部门 id 组成的数组
 
* 恢复指定的所有部门元信息:

 ```http
 POST /department/recover_all_by_id_in HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由部门 id 组成的数组
 
* 更新指定的部门元数据:

 ```http
 POST /role/update HTTP/1.1
 Authorization: Bearer 'your token'
 
 departmentId='department id'&status='department status'&name='department name'&description='department description'
 ```
 
name: 部门名
 description: 部门描述, 可选
 status: 部门状态, 可选
 
## 菜单模块接口

* 获取菜单树:

 ```http
 GET /menu/get_menus HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
* 删除菜单项:

 ```http
 DELETE /menu/remove_menus HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由菜单 Id 组成的数组, 删除某个节点时, 如果有子节点则需要将所有子节点一并发送, 这里的子节点不限于直接子节点.
 
* 恢复菜单项:

 ```
 POST /menu/recover_menus HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 request body: 由菜单 Id 组成的数组, 恢复某个节点时, 如果有父节点则需要将所有父节点一并发送, 这里的父节点不限于直接父节点.
 
* 添加根节点菜单项:

 ```http
 POST /menu/add_root_menu HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='menu name'&path='menu path'&isLeaf='is leaf'
 ```
 
 name: 菜单名
 path: vue 前端路由路径, 菜单点击时会触发这里指定的路由
 isLeaf: 是否叶节点, true or false
 
* 添加非根节点的菜单项:

 ```http
 POST /menu/add_menu HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='menu name'&path='menu path'&parentId='parent id'&isLeaf='is leaf'
 ```
 
 name: 菜单名
 path: vue 前端路由路径, 菜单点击时会触发这里指定的路由
 parentId: 父菜单的 id
 isLeaf: 是否叶节点, true or false
 
* 给指定角色开放指定菜单:

 ```http
 POST /menu/add_roles_to_menu/{menuId} HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 menuId: 指定的菜单 id
 request body: 由角色 Id 组成的数组
 
* 禁止指定角色访问指定菜单

 ```http
 DELETE /menu/remove_roles_from_menu/{menuId} HTTP/1.1
 Authorization: Bearer 'your token'
 Content-Type: application/json
 
 'request body'
 ```
 
 menuId: 指定的菜单 id
 request body: 由角色 Id 组成的数组
 
* 更新菜单信息

  ```http
  POST /menu/update_menu HTTP/1.1
  Authorization: Bearer 'your token'
  
  menuId='menu id'&name='menu name'&path='menu path'
  ```
  
  menuId: 菜单 id
  name: 菜单名
  path: vue 前端路由路径, 菜单点击时会触发这里指定的路由

