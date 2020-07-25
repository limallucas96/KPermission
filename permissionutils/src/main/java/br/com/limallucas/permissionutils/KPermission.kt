package br.com.limallucas.permissionutils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.KUtils.isGreaterThanM
import br.com.limallucas.permissionutils.KUtils.isGreaterThanQ

@SuppressLint("NewApi")
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

    private fun invoke(result: PermissionResult) {
        onResult?.invoke(result)
        onResult = null
    }

    private fun checkSelfPermission(permissions: List<String>): Boolean {
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
            invoke(PermissionResult.GRANTED_EVER)
            return
        }

        permissions = permissions.filterPermissionsBySDK()

        if (checkSelfPermission(permissions)) {
            invoke(PermissionResult.GRANTED_EVER)
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(it, permissions.toTypedArray(), 999)
            }
            fragment?.let {
                it.requestPermissions(permissions.toTypedArray(), 999)
            }
        }
    }



    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (grantResults.isEmpty() || permissions.isEmpty() || (grantResults.size != permissions.size)) {
            onResult = null
            return
        }

        val didGrantAllPermissions = grantResults.map { it == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true

        if (didGrantAllPermissions) {
            invoke(PermissionResult.GRANTED_EVER)
        } else {
            if (isGreaterThanQ()) {
                val askedPermissions = permissions.removeBackgroundPermission()

                if (checkSelfPermission(askedPermissions)) {
                    invoke(PermissionResult.GRANTED_IN_APP)
                } else {
                    shouldShowRequestPermissionRationale(askedPermissions)
                }
            } else {
                shouldShowRequestPermissionRationale(permissions.getDeniedPermissions(grantResults))
            }
        }
    }

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