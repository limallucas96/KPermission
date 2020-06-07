package br.com.limallucas.librarytest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.PermissionListener
import br.com.limallucas.permissionutils.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PermissionListener {

    private lateinit var permissionsUtils: PermissionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragment(MainFragment.getInstance())

        permissionsUtils = PermissionUtils.permissionBuilder {
            activity = this@MainActivity
            listener = this@MainActivity
        }

        location.setOnClickListener {
            permissionsUtils.ask(
                PermissionType.LOCATION_TYPE.code,
                PermissionType.LOCATION_TYPE.permissions
            )
        }

        camera.setOnClickListener {
            permissionsUtils.ask(
                PermissionType.CAMERA_TYPE.code,
                PermissionType.CAMERA_TYPE.permissions
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsUtils.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PermissionType.fromInt(requestCode)?.let { type ->
            permissionsUtils.ask(type.code, type.permissions)
        }
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

    private fun addFragment(fragment: Fragment) {
        if (!isFinishing) {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.frame_layout, fragment)
                commitAllowingStateLoss()
            }
        }
    }
}

