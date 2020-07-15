package br.com.limallucas.permissionutils

@DslMarker
annotation class PersonDsl

data class Permission(val permissions: List<AppPermissions>)

data class AppPermissions(val name: String)

@PersonDsl
class AskBuilder {
    private var permissionsName = mutableListOf<AppPermissions>()

    fun build() = Permission(permissionsName)

    fun permissions(block: AppPermission.() -> Unit) {
        permissionsName.addAll(AppPermission().apply(block))
    }
}

@PersonDsl
class AppPermission : ArrayList<AppPermissions>() {
    fun permissionsType(block: PermissionBuilder.() -> Unit) {
        add(PermissionBuilder().apply(block).build())
    }
}

@PersonDsl
class PermissionBuilder {
    var type: String = ""
    fun build(): AppPermissions = AppPermissions(type)
}