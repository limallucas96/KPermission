

## KPermission
**An android library for handling permissions written in Kotlin.**

## Usage

Here's a minimum example, in which you register a `MainActivity` or a `Fragment` that whishes to take a picture. 

### 0. Prepare AndroidManifest

Add the following lines to `AndroidManifest.xml`:
 
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.example.snazzyapp">

    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <!-- other permissions go here -->

    <application ...>
        ...
    </application>
</manifest>
```

## 1. Implementation

### 1.1 Init the KPermission library.

```kotlin
private lateinit var kPermission: KPermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kPermission = KPermission(this)
    }
```

### 1.2 Request permission.

You may request your permission and get it's result with two lamdas. `Ask` and `onResult`.

`Ask` is a block of your app permissions and right after you can call `onResult` which will return `GRANTED`, `DENIED`  or `NEVER_ASK_AGAIN`.


```kotlin
camera.setOnClickListener {
    kPermission.ask {
        permission { name = Manifest.permission.CAMERA }
        permission { name = Manifest.permission.WRITE_EXTERNAL_STORAGE }
    } onResult { result ->
        when (result) {
            PermissionResult.GRANTED -> {} //All permissions were granted
            PermissionResult.DENIED -> {} // At least one was denied
            PermissionResult.NEVER_ASK_AGAIN -> {} //At least one was marked as never ask again
        }
        Toast.makeText(this, "Camera: $result", Toast.LENGTH_SHORT).show()
    }
}
```

### 1.3 Let the library handle the permission result. 

```kotlin
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    kPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
}
```

## Full implementation

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var kPermission: KPermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kPermission = KPermission(this)

        camera.setOnClickListener {
            kPermission.ask {
                permission { name = Manifest.permission.CAMERA }
                permission { name = Manifest.permission.WRITE_EXTERNAL_STORAGE }
            } onResult { result ->
                when (result) {
                    PermissionResult.GRANTED -> {} //All permissions were granted
                    PermissionResult.DENIED -> {} // At least one was denied
                    PermissionResult.NEVER_ASK_AGAIN -> {} //At least one was marked as never ask again
                }
                Toast.makeText(this, "Camera: $result", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        kPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
  implementation 'com.github.limallucas96:KPermission:0.1.1'
}
```
