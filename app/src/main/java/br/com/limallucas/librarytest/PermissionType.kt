package br.com.limallucas.librarytest

import android.Manifest.permission.*

enum class PermissionType(val code: Int, val permissions: Array<String>) {
    CAMERA_TYPE(123, arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO)),
    LOCATION_TYPE(456, arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)),
    AUDIO_TYPE(789, arrayOf(RECORD_AUDIO));

    companion object {
        fun fromInt(code: Int) = values().firstOrNull { it.code == code }
    }
}