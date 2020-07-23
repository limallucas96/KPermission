package br.com.limallucas.librarytest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.PermissionResult
import br.com.limallucas.permissionutils.KPermission
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var kPermission: KPermission

    companion object {
        fun getInstance() : MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kPermission =  KPermission(this)

        audio.setOnClickListener {
            kPermission.ask {
                permission { name = android.Manifest.permission.RECORD_AUDIO }
                permission { name = android.Manifest.permission.WRITE_EXTERNAL_STORAGE }
            } onResult { result ->
                when (result) {
                    PermissionResult.GRANTED -> {}
                    PermissionResult.DENIED -> {}
                    PermissionResult.NEVER_ASK_AGAIN -> {}
                }
                Toast.makeText(requireContext(), "Audio: $result", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        kPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

