package top.itfinally.security

data class RolePermissionRefreshEvent(val roleId: String)

data class AccountChangeEvent(val account: String)

data class AccountResetEvent(val account: String)