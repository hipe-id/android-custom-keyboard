package id.hipe.sampleapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment

class MainFragment : Fragment() {

    // TODO: Implement by lazy/DI ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Init or setup view, viewmodel related, etc anything to setup here


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Init or setup view, viewmodel related, etc anything to setup here
        startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 99)

        if (isInputEnabled()) {
            (activity?.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
        } else {
            Toast.makeText(context, "Please enable keyboard first", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isInputEnabled(): Boolean {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val mInputMethodProperties = imm.enabledInputMethodList

        val N = mInputMethodProperties.size
        var isInputEnabled = false

        for (i in 0 until N) {

            val imi = mInputMethodProperties[i]
            Log.d("INPUT ID", imi.id.toString())
            if (imi.id.contains(activity?.packageName ?: "")) {
                isInputEnabled = true
            }
        }

        return isInputEnabled
    }
}