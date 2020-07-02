package br.com.limallucas.librarytest

import android.Manifest
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
            permissionsUtils.ask {
                requestCode = 123
                permissions {
                    permissionsType { type = Manifest.permission.WRITE_EXTERNAL_STORAGE }
                    permissionsType { type = Manifest.permission.RECORD_AUDIO }
                    permissionsType { type = Manifest.permission.CAMERA }
                }
            } //add infix fun like onResult {status, requestCode -> }
        }

        camera.setOnClickListener {
//            permissionsUtils.ask(
//                PermissionType.CAMERA_TYPE.code,
//                PermissionType.CAMERA_TYPE.permissions
//            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PermissionType.fromInt(requestCode)?.let { type ->
//            permissionsUtils.ask(type.code, type.permissions)
        }
    }

    override fun onPermissionGranted(requestCode: Int) {
        //Treats granted permission based on request code
        val result = "onPermissionGranted ${PermissionType.fromInt(requestCode)?.name}"
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionDenied(requestCode: Int) {
        //Treats denied permission based on request code
        val result = "onPermissionDenied ${PermissionType.fromInt(requestCode)?.name}"
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }

    override fun onNeverAskAgain(requestCode: Int) {
        //Treats onNeverAskAgain. Gives which permission was selected to never be asked again and request code.
        val result = " onNeverAskAgain:  ${PermissionType.fromInt(requestCode)?.name}"
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

