package br.com.limallucas.permissionutils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
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

    private fun shouldCheckForPermissions() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    infix fun onAskResult(onResult: (result: PermissionResult) -> Unit) {
        this.onResult = onResult
    }

    fun ask(block: AppPermission.() -> Unit): PermissionUtils {
        val permissions = AppPermission().apply(block).map { it.name }.toTypedArray()

        if (permissions.isEmpty()) {
            return this
        }

//        if (!shouldCheckForPermissions()) {
//            onResult?.invoke(PermissionResult.GRANTED)
//        } else {
            activity?.let {
                ActivityCompat.requestPermissions(it, permissions, 999)
            }
            fragment?.let {
                it.requestPermissions(permissions, 999)
            }
//        }
        return this
    }

    @SuppressLint("NewApi")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (grantResults.isEmpty()) {
            return
        }

        val didGrantAllPermissions = grantResults.map { it == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true

        if (didGrantAllPermissions) {
            onResult?.invoke(PermissionResult.GRANTED)
        } else {
            permissions.filterIndexed { index, _ -> grantResults[index] != PackageManager.PERMISSION_GRANTED }.let { deniedPermissions ->
                activity?.let { act ->
                    deniedPermissions.find { act.shouldShowRequestPermissionRationale(it) }?.let {
                        onResult?.invoke(PermissionResult.DENIED)
                    } ?: run {
                        onResult?.invoke(PermissionResult.NEVER_ASK_AGAIN)
                    }
                }
                fragment?.let { fgt ->
                    deniedPermissions.find { fgt.shouldShowRequestPermissionRationale(it) }?.let {
                        onResult?.invoke(PermissionResult.DENIED)
                    } ?: run {
                        onResult?.invoke(PermissionResult.NEVER_ASK_AGAIN)
                    }
                }
            }
        }
    }
}