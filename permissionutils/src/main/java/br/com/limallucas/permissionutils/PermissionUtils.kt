package br.com.limallucas.permissionutils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class PermissionUtils {

    var activity: Activity? = null
    var listener: PermissionListener? = null

    companion object {
        fun permissionBuilder(initializeAction: PermissionUtils.() -> Unit): PermissionUtils {
            return PermissionUtils().apply {
                initializeAction()
                activity ?: throw NullPointerException("activity must be set")
                listener ?: throw NullPointerException("listener must be set")
            }
        }
    }

    fun ask(reqCode: Int, perms: Array<String>) {
        activity?.let {
            ActivityCompat.requestPermissions(it, perms, reqCode)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        val result =
            grantResults.map { it == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true
        if (result) {
            listener?.onPermissionGranted(requestCode)
        } else {
            listener?.onPermissionDenied(requestCode)
        }
    }
}
