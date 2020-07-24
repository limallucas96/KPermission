package br.com.limallucas.librarytest

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.KPermission
import br.com.limallucas.permissionutils.PermissionResult
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var kPermission: KPermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragment(MainFragment.getInstance())
        kPermission = KPermission(this)

        location.setOnClickListener {
            kPermission.ask {
                permission { name = Manifest.permission.ACCESS_FINE_LOCATION }
                permission { name = Manifest.permission.ACCESS_COARSE_LOCATION }
                permission { name = Manifest.permission.ACCESS_BACKGROUND_LOCATION }
            } onResult { result ->
                when (result) {
                    PermissionResult.GRANTED -> { }
                    PermissionResult.DENIED -> { }
                    PermissionResult.NEVER_ASK_AGAIN -> { }
                    PermissionResult.GRANTED_ALL_THE_TIME -> { }
                }
                Toast.makeText(this, "Location: $result", Toast.LENGTH_SHORT).show()
            }
        }

        camera.setOnClickListener {
            kPermission.ask {
                permission { name = Manifest.permission.CAMERA }
                permission { name = Manifest.permission.WRITE_EXTERNAL_STORAGE }

            } onResult { result ->
                when (result) {
                    PermissionResult.GRANTED -> {
                    }
                    PermissionResult.DENIED -> {
                    }
                    PermissionResult.NEVER_ASK_AGAIN -> {
                    }
                }
                Toast.makeText(this, "Camera: $result", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        kPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun addFragment(fragment: Fragment) {
        if (!isFinishing) {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.frame_layout, fragment)
                commitAllowingStateLoss()
            }
        }
    }
}










