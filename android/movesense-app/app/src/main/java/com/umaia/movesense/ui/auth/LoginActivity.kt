package com.umaia.movesense.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.*
import com.umaia.movesense.AuthViewModel
import com.umaia.movesense.ScanActivity
import com.umaia.movesense.databinding.ActivityLoginBinding
import com.umaia.movesense.data.network.AuthApi
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.repository.AuthRepository
import com.umaia.movesense.data.responses.User
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.ui.home.enable
import com.umaia.movesense.ui.home.startNewActivity
import com.umaia.movesense.ui.home.visible
import com.umaia.movesense.util.ViewModelFactory
import kotlinx.coroutines.launch

open class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private val remoteDataSource = RemoteDataSource()
    private lateinit var userPreferences: UserPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)

        val factory =
            ViewModelFactory(
                AuthRepository(
                    remoteDataSource.buildApi(AuthApi::class.java),
                    userPreferences
                )
            )

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        binding.progressBar.visible(false)

// TODO: corrigir este metodo de validação
        binding.editTextPassword.addTextChangedListener {
            val username = binding.editTextUsername.text.toString().trim()
        }

        viewModel.loginResponse.observe(this, Observer {
            binding.progressBar.visible(false)
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    viewModel.saveAuthToken(it.value.user.access_token)
                    this@LoginActivity.startNewActivity(ScanActivity::class.java)


                }
                is Resource.Failure -> {
                    Toast.makeText(this, "Login Failure", Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.loginButton.setOnClickListener {
            //@todo: validations
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            binding.progressBar.visible(true)
            viewModel.login(username, password)
        }
    }

}