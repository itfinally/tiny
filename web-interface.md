# 接口说明

HTTP 协议规定空出一行即表示 Header 结束, 此处遵循此规定, Header 空行后跟随的是 Body.

此外, 所有接口都通过 `BasicResponse` 及其子类返回相应的数据.

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
  GET /get_valid_image/{account}/{random} HTTP/1.1
  ```
  
  account: 用户名
  random: 随机数, 目的是为了防止浏览器缓存

## 权限模块接口

* 添加权限:

 ```http
 POST /permission/add_permission HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='permission name'&description='permission description'
 ```
 
 name: 权限名
 description: 权限描述, 可选
 
* 删除权限:

 ```http
 DELETE /permission/add_permission/{permissionId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 permissionId: 权限 Id
 
## 角色模块接口
 
* 新增角色:

 ```http
 POST /role/add_role HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='role name'&description='role description ( option )'
 ```
 
 name: 角色名
 description: 角色描述, 可选
 
* 删除角色:
 
 ```http
 POST /role/remove_role/{roleId} HTTP/1.1
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
 
* 查询自己的权限:

 ```http
 GET /role/query_own_permissions HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
## 部门模块接口

* 新增部门:

 ```http
 POST /department/add_department HTTP/1.1
 Authorization: Bearer 'your token'
 
 name='department name'&description='department description'
 ```
 
 name: 部门名称
 description: 部门名称
 
* 删除部门:

 ```http
 POST /department/remove_department/{departmentId} HTTP/1.1
 Authorization: Bearer 'your token'
 ```
 
 departmentId: 部门 Id
 
* 对指定部门增加权限:

 ```http
 POST /department/add_roles_to_department/{departmentId} HTTP/1.1
 Authorization: Bearer 'your token'
 
 'request body'
 ```
 
 departmentId: 部门 Id
 request body: 由角色 Id 组成的数组
 

