package br.com.limallucas.permissionutils

import android.Manifest.permission.*

enum class PermissionStringType(value: String) {
    CAMERA_STRING("need camera"),
    WRITE_EXTERNAL_STORAGE_STRING("need write"),
    ACCESS_COARSE_LOCATION_STRING("need coarse location"),
    ACCESS_FINE_LOCATION_STRING("need fine location"),
    RECORD_AUDIO("record audio");

    companion object {
        fun toReadablePermission(permission: String) = values().firstOrNull { permission.equals(it.name, true) }
    }
}