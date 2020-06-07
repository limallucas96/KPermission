# PermissionUtils
Android library for handling permissions. Written in kotlin.
## Usage

Here's a minimum example, in which you register a `MainActivity` that requires `Manifest.permission.CAMERA`.

### 0. Prepare AndroidManifest

Add the following line to `AndroidManifest.xml`:
 
`<uses-permission android:name="android.permission.CAMERA" />`

### 1. Implementation

### 1.1 Make your class implements PermissionListener so you'll be aware when your permission has been granted or denied.

```kotlin
 override fun onPermissionGranted(requestCode: Int) {}
 override fun onPermissionDenied(requestCode: Int) {}
```

### 1.2 When initializing PermissionUtils, make sure to give an activity and listener context, otherwise it will throw NullPointerException

```kotlin
private lateinit var permissionsUtils: PermissionUtils

permissionsUtils = PermissionUtils.permissionBuilder {
   activity = this@MainActivity
   listener = this@MainActivity
}
```

### 1.3 Asking for permissions

Create an RequestCode and a String Array with the permissions you want to ask. 

The code will be returned by the interface methods, so you can deal with success or erros. 

```kotlin
camera.setOnClickListener {
  permissionsUtils.ask(
      PermissionType.CAMERA_TYPE.code,
      PermissionType.CAMERA_TYPE.permissions
 )
}
```

### 1.4 Let the library handle the permission result. 

```kotlin
override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray){
  super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  permissionsUtils.onRequestPermissionsResult(requestCode, grantResults)
}
```

## Full implementation

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

We advise you to organize your requests code and permissions inside an enum class. 


```kotlin
enum class PermissionType(val code: Int, val permissions: Array<String>) {
    CAMERA_TYPE(123, arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE));
    companion object {
        fun fromInt(code: Int) = values().firstOrNull { it.code == code }
    }
}
```


## Installation

Current version: 0.1.0

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency

```groovy
dependencies {
  implementation 'com.github.limallucas96:PermissionUtils:0.1.0'
}
```
