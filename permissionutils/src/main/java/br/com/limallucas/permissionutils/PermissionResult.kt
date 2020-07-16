package br.com.limallucas.permissionutils

import android.app.Activity
import androidx.fragment.app.Fragment

enum class PermissionResult {
    GRANTED, DENIED, NEVER_ASK_AGAIN
}

enum class ViewType {
    ACTIVITY, FRAGMENT, INVALID;

    companion object {
        fun isTypeOf(activity: Activity?, fragment: Fragment?) : ViewType {
            var type = INVALID
            if (activity != null) {
                type = ACTIVITY
            } else if (fragment != null) {
                type = FRAGMENT
            }
            return type
        }
    }
}