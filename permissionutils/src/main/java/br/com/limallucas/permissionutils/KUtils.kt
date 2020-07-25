package br.com.limallucas.permissionutils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import br.com.limallucas.permissionutils.KUtils.isLessThanQ

object KUtils {

    fun isLessThanQ() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

    fun isGreaterThanM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun isGreaterThanQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun MutableList<String>.filterPermissionsBySDK(): MutableList<String> {
    if (isLessThanQ()) {
        return this.filterNot { it == Manifest.permission.ACCESS_BACKGROUND_LOCATION }.toMutableList()
    }
    return this
}


fun Array<out String>.getDeniedPermissions(grantResults: IntArray): List<String> {
    return this.filterIndexed { index, _ -> grantResults[index] != PackageManager.PERMISSION_GRANTED }
}

fun Array<out String>.removeBackgroundPermission(): List<String> {
    return this.filterNot { it == Manifest.permission.ACCESS_BACKGROUND_LOCATION }
}