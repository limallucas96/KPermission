package br.com.limallucas.permissionutils

interface PermissionListener {
    fun onPermissionGranted(requestCode: Int)
    fun onPermissionDenied(requestCode: Int)
}