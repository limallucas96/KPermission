package br.com.limallucas.permissionutils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
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

    @SuppressLint("NewApi")
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val didGrantAllPermissions =
            grantResults.map { it == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true

        if (didGrantAllPermissions) {
            listener?.onPermissionGranted(requestCode)
        } else {
            permissions.find { activity?.shouldShowRequestPermissionRationale(it) == true }?.let {
                listener?.onPermissionDenied(requestCode)
            } ?: run {
                listener?.onNeverAskAgain(requestCode)
            }
        }
    }
}