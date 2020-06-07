# PermissionUtils
Android library for handling permissions

Written in kotlin

## Usage

Here's a minimum example, in which you register a `MainActivity` which requires `Manifest.permission.CAMERA`.

### 0. Prepare AndroidManifest

Add the following line to `AndroidManifest.xml`:
 
`<uses-permission android:name="android.permission.CAMERA" />`

### 1. Init the library in your view

Make your activity implements PermissionListener so you'll be aware when your permission has been denied or granted by:

```kotlin
 override fun onPermissionGranted(requestCode: Int) {}
 override fun onPermissionDenied(requestCode: Int) {}
```

```kotlin
class MainActivity : AppCompatActivity(), PermissionListener {

    private lateinit var permissionsUtils: PermissionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionsUtils = PermissionUtils.permissionBuilder {
            activity = this@MainActivity
            listener = this@MainActivity
        }

        camera.setOnClickListener {
            permissionsUtils.ask(
                PermissionType.CAMERA_TYPE.code,
                PermissionType.CAMERA_TYPE.permissions
            )
        }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsUtils.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onPermissionGranted(requestCode: Int) {
        //Treated granted permission based on request code
        val result = "onPermissionGranted ${PermissionType.fromInt(requestCode)?.name}"
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionDenied(requestCode: Int) {
        //Treated denied permission based on request code
        val result = "onPermissionDenied ${PermissionType.fromInt(requestCode)?.name}"
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }
}

```

### 2. We strongly recommend to create an enum class to organize your permissions. 

Make your activity implements PermissionListener so you'll be aware when your permission has been denied or granted by:

```kotlin
enum class PermissionType(val code: Int, val permissions: Array<String>) {
    CAMERA_TYPE(123, arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE));
    companion object {
        fun fromInt(code: Int) = values().firstOrNull { it.code == code }
    }
}
```
