package br.com.limallucas.permissionutils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class PermissionUtils(private val activity: Activity) {

    private lateinit var onResult: (result: PermissionResult) -> Unit

    private fun shouldCheckForPermissions() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    infix fun onAskResult(onResult: (result: PermissionResult) -> Unit) {
        this.onResult = onResult
    }

    fun ask(block: AppPermission.() -> Unit): PermissionUtils {
        val builder = AppPermission().apply(block)

        if (!shouldCheckForPermissions()) {
            onResult.invoke(PermissionResult.GRANTED)
        } else {
            val permissions = builder.map { it.name }.toTypedArray()
            ActivityCompat.requestPermissions(activity, permissions, 999)
        }
        return this
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
            onResult.invoke(PermissionResult.GRANTED)
        } else {
            permissions.filterIndexed { index, _ -> grantResults[index] != PackageManager.PERMISSION_GRANTED }
                .let { deniedPermissions ->
                    deniedPermissions.find { activity.shouldShowRequestPermissionRationale(it) }
                        ?.let {
                            onResult.invoke(PermissionResult.DENIED)
                        } ?: run {
                        onResult.invoke(PermissionResult.NEVER_ASK_AGAIN)
                    }
                }
        }
    }
}