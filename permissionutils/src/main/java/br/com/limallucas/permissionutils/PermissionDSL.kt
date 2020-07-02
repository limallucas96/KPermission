package br.com.limallucas.permissionutils

data class Permission(val requestCode: Int, val permissions: List<AppPermissions>)

data class AppPermissions(val name: String)

class AskBuilder {
    var requestCode: Int = 0
    private var permissionsName = mutableListOf<AppPermissions>()

    fun build() = Permission(requestCode, permissionsName)

    fun permissions(block: AppPermission.() -> Unit) {
        permissionsName.addAll(AppPermission().apply(block))
    }
}

class AppPermission : ArrayList<AppPermissions>() {
    fun permissionsType(block: PermissionBuilder.() -> Unit) {
        add(PermissionBuilder().apply(block).build())
    }
}

class PermissionBuilder {
    var type: String = ""
    fun build(): AppPermissions = AppPermissions(type)
}