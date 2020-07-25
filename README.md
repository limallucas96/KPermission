

## KPermission
**An android library for handling permissions written in Kotlin.**

## Usage

Here's a minimum example, in which you register a `MainActivity` or a `Fragment` that whishes to access user's location. 

### 0. Prepare AndroidManifest

Add the following lines to `AndroidManifest.xml`:
 
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.example.snazzyapp">

<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />  
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />  
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
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

You may request your permission with `ask` and get it's result with `onResult`.

`Ask` is a block of your application permissions. Add the permissions you want to ask inside it.

`onResult` is a block that will return a state for your ask. 
The state might be: 

 - **GRANTED_EVER** -> User accepted all permissions to run all the time. 
 - **GRANTED_IN_APP** -> User accepted all permissions but in foreground. 
 - **DENIED** -> User denied one or more permissions.
 - **NEVER_ASK_AGAIN** -> User marked one or more permissions as never ask again.

```kotlin
location.setOnClickListener {
    kPermission.ask {
        permission { name = Manifest.permission.ACCESS_FINE_LOCATION }
        permission { name = Manifest.permission.ACCESS_COARSE_LOCATION }
        permission { name = Manifest.permission.ACCESS_BACKGROUND_LOCATION }
    } onResult { result ->
        when (result) {
           	 PermissionResult.GRANTED_EVER -> {} //User accepted all permissions
           	 PermissionResult.GRANTED_IN_APP -> {} //User accepted all permissions but in foreground
          	 PermissionResult.DENIED -> {} //Denied
           	 PermissionResult.NEVER_ASK_AGAIN -> {} //Never ask again
        }
        Toast.makeText(this, "Location: $result", Toast.LENGTH_SHORT).show()
    }
}
```

**NOTE**

 1. If your application targets SDK 29 or higher , and your permission list DO contain any background permission, then be sure to listen for `GRANTED_EVER` and `GRANTED_IN_APP` results, since the user might not grant you background access.
 
 
 2. If your application targets SDK 29 or higher , and your permission list do NOT contain any background permission, then the result should automatically be `GRANTED_EVER` 


 3. If your application targets SDK 28 or less, then `GRANTED_EVER` result should be enough.


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

        location.setOnClickListener {
            kPermission.ask {
                permission { name = Manifest.permission.ACCESS_FINE_LOCATION }
                permission { name = Manifest.permission.ACCESS_COARSE_LOCATION }
                permission { name = Manifest.permission.ACCESS_BACKGROUND_LOCATION }
            } onResult { result ->
                when (result) {
           		PermissionResult.GRANTED_EVER -> {} //User accepted all permissions
           		PermissionResult.GRANTED_IN_APP -> {} //User accepted all permissions but in foreground
           		PermissionResult.DENIED -> {} //Denied
           		PermissionResult.NEVER_ASK_AGAIN -> {} //Never ask again
                }
                Toast.makeText(this, "Location: $result", Toast.LENGTH_SHORT).show()
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
