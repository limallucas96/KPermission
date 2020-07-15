package br.com.limallucas.librarytest

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var permissionsUtils: PermissionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragment(MainFragment.getInstance())

        permissionsUtils = PermissionUtils(this)

        location.setOnClickListener {
            permissionsUtils.ask {
                permissions {
                    permissionsType { type = Manifest.permission.ACCESS_FINE_LOCATION }
                    permissionsType { type = Manifest.permission.ACCESS_COARSE_LOCATION }
                }
            } onAskResult { result ->
                //result will return a value of GRANTED, DENIED or NEVER_ASK_AGAIN.
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

    private fun addFragment(fragment: Fragment) {
        if (!isFinishing) {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.frame_layout, fragment)
                commitAllowingStateLoss()
            }
        }
    }
}

