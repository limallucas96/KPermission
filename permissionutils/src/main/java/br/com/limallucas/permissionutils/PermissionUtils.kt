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

    fun ask(block: AskBuilder.() -> Unit) {
        val builder = AskBuilder().apply(block).build()
        activity?.let {
            val permissions = builder.permissions.map { it.name }.toTypedArray()
            ActivityCompat.requestPermissions(it, permissions, builder.requestCode)
        }
    }

    fun ask(reqCode: Int, perms: Array<String>) {
        activity?.let {
            ActivityCompat.requestPermissions(it, perms, reqCode)
        }
    }

    @SuppressLint("NewApi")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        val didGrantAllPermissions = grantResults.map { it == PackageManager.PERMISSION_GRANTED }.find { !it } ?: true

        if (didGrantAllPermissions) {
            listener?.onPermissionGranted(requestCode)
        } else {
            permissions.filterIndexed { index, s ->  grantResults[index] != PackageManager.PERMISSION_GRANTED }.let { deniedPermissions ->
                deniedPermissions.find { activity?.shouldShowRequestPermissionRationale(it) == true }?.let {
                    listener?.onPermissionDenied(requestCode)
                } ?: run {
                    listener?.onNeverAskAgain(requestCode)
                }
            }
        }
    }
}