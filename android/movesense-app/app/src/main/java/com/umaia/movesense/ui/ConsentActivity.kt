package com.umaia.movesense.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.MainViewModel
import com.umaia.movesense.R
import com.umaia.movesense.ScanActivity
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.databinding.ActivityConsentBinding
import com.umaia.movesense.ui.home.startNewActivity

class ConsentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsentBinding
    private lateinit var userPreferences: UserPreferences
    lateinit var gv: GlobalClass
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gv = application as GlobalClass
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        userPreferences = UserPreferences(this)

        binding.textviewConsentExplanation.setText(R.string.consent_explanation)

        binding.consentButton.setOnClickListener {

            if (!binding.scroll.canScrollVertically(1)) {
                viewModel.setConsentStatus(true)
                gv.consent = true
                this@ConsentActivity.startNewActivity(ScanActivity::class.java)
            } else {
                // User has not scrolled to the end of the text, show an error message
                Toast.makeText(
                    this,
                    "Por favor, le tudo antes de continuar",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}