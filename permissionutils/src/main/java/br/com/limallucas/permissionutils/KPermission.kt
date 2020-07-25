package br.com.limallucas.permissionutils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.KUtils.filterPermissionsBySDK
import br.com.limallucas.permissionutils.KUtils.isGreaterThanM
import br.com.limallucas.permissionutils.KUtils.isGreaterThanMAndLessThanQ
import br.com.limallucas.permissionutils.KUtils.isGreaterThanQ
import br.com.limallucas.permissionutils.KUtils.removeBackgroundPermission

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
        if (isGreaterThanMAndLessThanQ()) {
            invoke(PermissionResult.GRANTED)
        } else if (isGreaterThanQ()) {
            invoke(PermissionResult.GRANTED_ALL_THE_TIME)
        }
    }

    private fun invoke(result: PermissionResult) {
        onResult?.invoke(result)
        onResult = null
    }

    private fun checkSelfPermission(permissions: Array<out String>): Boolean {
        var isAllGranted = false
        (activity?.applicationContext ?: fragment?.context)?.let { context ->
            isAllGranted = permissions.map { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true
        }
        return isAllGranted
    }

    fun ask(block: AppPermission.() -> Unit): KPermission {
        permissions = AppPermission().apply(block).map { it.name }.toMutableList()
        return this
    }

    infix fun onResult(onResult: (result: PermissionResult) -> Unit) {
        this.onResult = onResult

        if (permissions.isEmpty()) {
            this.onResult = null
            return
        }

        if (!isGreaterThanM()) {
            invoke(PermissionResult.GRANTED)
            return
        }

        permissions = filterPermissionsBySDK(permissions.toTypedArray()).toMutableList()

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
    }

    @SuppressLint("NewApi")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var askedPermissions = permissions

        if (grantResults.isEmpty() || askedPermissions.isEmpty() || (grantResults.size != askedPermissions.size)) {
            onResult = null
            return
        }

        val didGrantAllPermissions = grantResults.map { it == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true

        if (didGrantAllPermissions) {
            invokeBySDK()
        } else {
            if (isGreaterThanQ()) {
                askedPermissions = removeBackgroundPermission(permissions)
                if (checkSelfPermission(askedPermissions)) {
                    invoke(PermissionResult.GRANTED)
                } else {
                    shouldShowRequestPermissionRationale(askedPermissions.toList())
                }
            } else if (isGreaterThanMAndLessThanQ()) {
                permissions.filterIndexed { index, _ -> grantResults[index] != PackageManager.PERMISSION_GRANTED }.let { deniedPermissions ->
                    shouldShowRequestPermissionRationale(deniedPermissions)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun shouldShowRequestPermissionRationale(deniedPermissions: List<String>) {
        activity?.let { act ->
            deniedPermissions.find { act.shouldShowRequestPermissionRationale(it) }?.let {
                invoke(PermissionResult.DENIED)
            } ?: run {
                invoke(PermissionResult.NEVER_ASK_AGAIN)
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