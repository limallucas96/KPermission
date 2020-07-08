package br.com.limallucas.permissionutils

import android.annotation.SuppressLint
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

    private fun shouldCheckForPermissions() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun ask(block: AskBuilder.() -> Unit) {
        val builder = AskBuilder().apply(block).build()

        if (!shouldCheckForPermissions()) {
            listener?.onPermissionGranted(builder.requestCode)
        } else {
            activity?.let {
                val permissions = builder.permissions.map { it.name }.toTypedArray()
                ActivityCompat.requestPermissions(it, permissions, builder.requestCode)
            }
        }
    }

    fun ask(requestCode: Int, perms: Array<String>) {
        if (!shouldCheckForPermissions()) {
            listener?.onPermissionGranted(requestCode)
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(it, perms, requestCode)
            }
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
            permissions.filterIndexed { index, s -> grantResults[index] != PackageManager.PERMISSION_GRANTED }
                .let { deniedPermissions ->
                    deniedPermissions.find { activity?.shouldShowRequestPermissionRationale(it) == true }
                        ?.let {
                            listener?.onPermissionDenied(requestCode)
                        } ?: run {
                            listener?.onNeverAskAgain(requestCode)
                    }
                }
        }
    }
}