package br.com.limallucas.permissionutils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.KUtils.isGreaterThanM
import br.com.limallucas.permissionutils.KUtils.isGreaterThanMAndLessThanQ
import br.com.limallucas.permissionutils.KUtils.isGreaterThanQ

class KPermission() {

    constructor(activity: Activity) : this() {
        this.activity = activity
    }

    constructor(fragment: Fragment) : this() {
        this.fragment = fragment
    }

    private var onResult: ((result: PermissionResult) -> Unit)? = null
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var permissions = mutableListOf<String>()

    private fun invokeBySDK() {
        val contains = permissions.any { it == Manifest.permission.ACCESS_BACKGROUND_LOCATION }
        if (contains) {
            invoke(PermissionResult.GRANTED_ALL_THE_TIME)
        } else {
            invoke(PermissionResult.GRANTED)
        }
    }

    private fun filterPermissionsBySDK() {
        if (isGreaterThanMAndLessThanQ()) {
            permissions = permissions.filterNot { it == Manifest.permission.ACCESS_BACKGROUND_LOCATION }.toMutableList()

        }
    }

    infix fun onResult(onResult: (result: PermissionResult) -> Unit) {
        this.onResult = onResult

        if (permissions.isEmpty()) {
            this.onResult = null
            return
        }

        filterPermissionsBySDK()

        if (isGreaterThanM()) {
            if (checkSelfPermission(permissions.toTypedArray())) {
                invokeBySDK()
            } else {
                activity?.let {
                    ActivityCompat.requestPermissions(it, permissions.toTypedArray(), 999)
                }
                fragment?.let {
                    it.requestPermissions(permissions.toTypedArray(), 999)
                }
            }
        } else {
            invoke(PermissionResult.GRANTED)
        }
    }

    private fun checkSelfPermission(permissions: Array<out String>): Boolean {
        var isAllGranted = false
        (activity?.applicationContext ?: fragment?.context)?.let { context ->
            isAllGranted = permissions.map { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true
        }
        return isAllGranted
    }

    private fun invoke(result: PermissionResult) {
        onResult?.invoke(result)
        onResult = null
    }

    fun ask(block: AppPermission.() -> Unit): KPermission {
        permissions = AppPermission().apply(block).map { it.name }.toMutableList()
        return this
    }

    @SuppressLint("NewApi")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (grantResults.isEmpty() || permissions.isEmpty() || (grantResults.size != permissions.size)) {
            onResult = null
            return
        }

        val didGrantAllPermissions = grantResults.map { it == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true

        if (isGreaterThanQ()) {
            if (didGrantAllPermissions) {
                invokeBySDK()
            } else {
                mutableListOf<Pair<String, Int>>().apply {
                    permissions.forEachIndexed { index, permission ->
                        this.add(Pair(permission, grantResults[index]))
                    }
                }.filterNot { it.first == Manifest.permission.ACCESS_BACKGROUND_LOCATION }.map { it.first }.let { deniedPermissions ->
                    shouldShowRequestPermissionRationale(deniedPermissions)
                }
            }
        } else {
            if (didGrantAllPermissions) {
                invoke(PermissionResult.GRANTED)
            } else {
                permissions.filterIndexed { index, _ -> grantResults[index] != PackageManager.PERMISSION_GRANTED }.let { deniedPermissions ->
                    shouldShowRequestPermissionRationale(deniedPermissions)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun shouldShowRequestPermissionRationale(deniedPermissions: List<String>) {
        activity?.let { act ->
            if (checkSelfPermission(deniedPermissions.toTypedArray())) {
                invoke(PermissionResult.GRANTED)
            } else {
                deniedPermissions.find { act.shouldShowRequestPermissionRationale(it) }?.let {
                    invoke(PermissionResult.DENIED)
                } ?: run {
                    invoke(PermissionResult.NEVER_ASK_AGAIN)
                }
            }
        }
        fragment?.let { fgt ->
            deniedPermissions.find { fgt.shouldShowRequestPermissionRationale(it) }?.let {
                invoke(PermissionResult.DENIED)
            } ?: run {
                invoke(PermissionResult.NEVER_ASK_AGAIN)
            }
        }
    }
}