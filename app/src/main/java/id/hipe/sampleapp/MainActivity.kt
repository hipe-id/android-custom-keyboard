package id.hipe.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.hipe.customkeyboard.R
import id.hipe.sampleapp.ui.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.main_content, MainFragment())
    }
}
