package com.umaia.movesense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.databinding.ActivityMainBinding
import com.umaia.movesense.databinding.ActivitySplashScreenBinding
import com.umaia.movesense.ui.auth.LoginActivity
import com.umaia.movesense.ui.home.startNewActivity
import com.umaia.movesense.ui.home.startNewActivityFromSplash


private lateinit var binding: ActivitySplashScreenBinding
private lateinit var userPreferences: UserPreferences
lateinit var gv: GlobalClass

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gv = application as GlobalClass
        supportActionBar?.hide()

        userPreferences = UserPreferences(this)

        userPreferences.token.asLiveData().observe(this, Observer {
            val activity =
                if (it == null)
                    startNewActivityFromSplash(LoginActivity::class.java)
                else if (!gv.connected)
                    startNewActivityFromSplash(ScanActivity::class.java)
                else
                    startNewActivityFromSplash(MainActivity::class.java)
            Toast.makeText(this, it ?: "Empty", Toast.LENGTH_SHORT).show()
        })
    }
}