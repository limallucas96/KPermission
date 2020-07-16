package br.com.limallucas.permissionutils

@DslMarker
annotation class PersonDsl

data class AppPermissions(val name: String)

@PersonDsl
class AppPermission : ArrayList<AppPermissions>() {
    fun permission(block: PermissionBuilder.() -> Unit) {
        add(PermissionBuilder().apply(block).build())
    }
}

@PersonDsl
class PermissionBuilder {
    var name: String = ""
    fun build(): AppPermissions = AppPermissions(name)
}