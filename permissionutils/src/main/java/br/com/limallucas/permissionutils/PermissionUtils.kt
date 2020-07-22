package br.com.limallucas.permissionutils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionUtils() {

    constructor(activity: Activity) : this() {
        this.activity = activity
    }

    constructor(fragment: Fragment) : this() {
        this.fragment = fragment
    }

    private var onResult: ((result: PermissionResult) -> Unit)? = null
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var permissions = listOf<String>()

    private fun greaterThanMarshmallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    infix fun onAskResult(onResult: (result: PermissionResult) -> Unit) {
        this.onResult = onResult

        if (permissions.isEmpty()) {
            this.onResult = null
            return
        }

        if (greaterThanMarshmallow()) {
            if (checkSelfPermission(permissions.toTypedArray())) {
                invoke(PermissionResult.GRANTED)
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

    fun ask(block: AppPermission.() -> Unit): PermissionUtils {
        permissions = AppPermission().apply(block).map { it.name }
        return this
    }

    @SuppressLint("NewApi")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (grantResults.isEmpty()) {
            return
        }

        val didGrantAllPermissions = grantResults.map { it == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true

        if (didGrantAllPermissions) {
            invoke(PermissionResult.GRANTED)
        } else {
            permissions.filterIndexed { index, _ -> grantResults[index] != PackageManager.PERMISSION_GRANTED }.let { deniedPermissions ->
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
        onResult = null
    }
}