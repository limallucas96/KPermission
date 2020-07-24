package br.com.limallucas.permissionutils

import android.os.Build

object KUtils {

    fun isGreaterThanM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun isGreaterThanMAndLessThanQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

    fun isGreaterThanQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

}