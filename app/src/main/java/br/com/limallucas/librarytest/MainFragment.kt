package br.com.limallucas.librarytest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.limallucas.permissionutils.PermissionUtils
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

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

        permissionsUtils = PermissionUtils(this.requireActivity())

        audio.setOnClickListener {
//            permissionsUtils.ask(PermissionType.AUDIO_TYPE.code, PermissionType.AUDIO_TYPE.permissions)
        }
    }
}

