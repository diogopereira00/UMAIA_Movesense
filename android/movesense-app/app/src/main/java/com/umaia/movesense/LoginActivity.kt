package com.umaia.movesense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.umaia.movesense.databinding.ActivityLoginBinding
import com.umaia.movesense.databinding.ActivityScanBinding
import com.umaia.movesense.network.AuthApi
import com.umaia.movesense.network.RemoteDataSource
import com.umaia.movesense.network.Resource
import com.umaia.movesense.repository.AuthRepository
import com.umaia.movesense.repository.BaseRepository
import com.umaia.movesense.util.ViewModelFactory

open class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var  viewModel : AuthViewModel
    private val remoteDataSource = RemoteDataSource()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(AuthRepository(remoteDataSource.buildApi(AuthApi::class.java)))
        viewModel = ViewModelProvider(this,factory)[AuthViewModel::class.java]

        viewModel.loginResponse.observe(this,Observer{
            when(it){
                is Resource.Success -> {
                    Toast.makeText(this, it.toString(),Toast.LENGTH_LONG).show()
                }
                is Resource.Failure -> {
                    Toast.makeText(this,"Login Failure",Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.loginButton.setOnClickListener{
            //@todo: validations
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            viewModel.login(username,password)
        }
    }

}