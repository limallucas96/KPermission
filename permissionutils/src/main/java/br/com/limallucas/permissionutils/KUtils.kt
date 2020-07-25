package br.com.limallucas.permissionutils

import android.Manifest
import android.os.Build

object KUtils {

    fun isGreaterThanM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun isGreaterThanMAndLessThanQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

    fun isGreaterThanQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

     fun filterPermissionsBySDK(permissions: Array<out String>): Array<out String> {
        if (isGreaterThanMAndLessThanQ()) {
            return permissions.filterNot { it == Manifest.permission.ACCESS_BACKGROUND_LOCATION }.toTypedArray()
        }
        return permissions
    }

    fun removeBackgroundPermission(permissions: Array<out String>): Array<out String> {
        return permissions.filterNot { it == Manifest.permission.ACCESS_BACKGROUND_LOCATION }.toTypedArray()
    }

}