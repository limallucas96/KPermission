package br.com.limallucas.permissionutils

interface PermissionListener {
    fun onPermissionGranted(requestCode: Int)
    fun onPermissionDenied(requestCode: Int)
    fun onNeverAskAgain(requestCode: Int)
}

enum class PermissionResult {
    GRANTED, DENIED, NEVER_ASK_AGAIN
}