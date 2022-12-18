package com.umaia.movesense.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.*
import com.umaia.movesense.ApiViewModel
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.ScanActivity
import com.umaia.movesense.databinding.ActivityLoginBinding
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.repository.ApiRepository
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.ui.home.startNewActivity
import com.umaia.movesense.ui.home.visible
import com.umaia.movesense.util.ViewModelFactory

open class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: ApiViewModel
    private val remoteDataSource = RemoteDataSource()
    private lateinit var userPreferences: UserPreferences
    lateinit var gv: GlobalClass


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        gv = application as GlobalClass

        val factory =
            ViewModelFactory(
                ApiRepository(
                    remoteDataSource.buildApi(ServerApi::class.java),
                    userPreferences
                )
            )

        viewModel = ViewModelProvider(this, factory)[ApiViewModel::class.java]


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
                    viewModel.saveUserID(it.value.user.id)
                    gv.userID = it.value.user.id
                    gv.authToken = it.value.user.access_token
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