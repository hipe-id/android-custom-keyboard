package id.hipe.sampleapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import id.hipe.customkeyboard.R

class MainActivity : AppCompatActivity() {

    private lateinit var tvKeyboard: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvKeyboard = findViewById(R.id.tvKeyboard)

        tvKeyboard.text = "Hipe keyboard currently disabled"

        startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 99)

        if (isInputEnabled()) {
            (getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
        } else {
            Toast.makeText(this@MainActivity, "Please enable keyboard first", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 99) {
            tvKeyboard.text = "Hipe keyboard now enabled"
        }
    }

    private fun isInputEnabled(): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val mInputMethodProperties = imm.enabledInputMethodList

        val N = mInputMethodProperties.size
        var isInputEnabled = false

        for (i in 0 until N) {

            val imi = mInputMethodProperties[i]
            Log.d("INPUT ID", imi.id.toString())
            if (imi.id.contains(packageName ?: "")) {
                isInputEnabled = true
            }
        }

        return isInputEnabled
    }

}
