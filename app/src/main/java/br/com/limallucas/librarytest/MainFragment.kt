package br.com.limallucas.librarytest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.PermissionListener
import br.com.limallucas.permissionutils.PermissionUtils
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), PermissionListener {

    private lateinit var permissionsUtils: PermissionUtils

    companion object {
        fun getInstance() : MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionsUtils = PermissionUtils.permissionBuilder {
            activity = this@MainFragment.activity
            listener = this@MainFragment
        }

        audio.setOnClickListener {
            permissionsUtils.ask(PermissionType.AUDIO_TYPE.code, PermissionType.AUDIO_TYPE.permissions)
        }
    }

    override fun onPermissionGranted(requestCode: Int) {
        //Treated granted permission based on request code
        val result = "onPermissionGranted ${PermissionType.fromInt(requestCode)?.name}"
        Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionDenied(requestCode: Int) {
        //Treated denied permission based on request code
        val result = "onPermissionDenied ${PermissionType.fromInt(requestCode)?.name}"
        Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
    }

    override fun onNeverAskAgain(requestCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

