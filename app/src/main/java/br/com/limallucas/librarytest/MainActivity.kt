package br.com.limallucas.librarytest

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.PermissionListener
import br.com.limallucas.permissionutils.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PermissionListener {

    companion object {
        const val LOCATION_RESULT_CODE = 123
        const val CAMERA_RESULT_CODE = 456
    }

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
                requestCode = LOCATION_RESULT_CODE
                permissions {
                    permissionsType { type = Manifest.permission.ACCESS_FINE_LOCATION }
                    permissionsType { type = Manifest.permission.ACCESS_COARSE_LOCATION }
//                    permissionsType { type = Manifest.permission.ACCESS_BACKGROUND_LOCATION } TODO("Check about ACCESS_BACKGROUND_LOCATION")
                }
            } onAskResult { result ->
                Toast.makeText(this, "$result", Toast.LENGTH_SHORT).show()
            }
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

    override fun onPermissionGranted(requestCode: Int) {
        //You may treat many result codes with `when` or `if/else conditional statements
        Toast.makeText(this, "onPermissionGranted: $requestCode", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionDenied(requestCode: Int) {
        //You may treat many result codes with `when` or `if/else conditional statements
        Toast.makeText(this, "onPermissionDenied: $requestCode", Toast.LENGTH_SHORT).show()
    }

    override fun onNeverAskAgain(requestCode: Int) {
        //You may treat many result codes with `when` or `if/else conditional statements
        Toast.makeText(this, "onNeverAskAgain: $requestCode", Toast.LENGTH_SHORT).show()
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

