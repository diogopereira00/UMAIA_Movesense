package com.umaia.movesense

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umaia.movesense.databinding.DialogLogoutBinding
import com.umaia.movesense.ui.auth.LoginActivity
import com.umaia.movesense.ui.home.startNewActivity

class DialogLogout(
    var authViewModel: ApiViewModel,
    var activity: Activity
) : DialogFragment() {
    private lateinit var binding: DialogLogoutBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogLogoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)

    }

    private fun setupClickListeners(view: View) {
        binding.terminarSessao.setOnClickListener {
            authViewModel.clearAuthToken()
            activity.startNewActivity(LoginActivity::class.java)
        }
        binding.cancelar.setOnClickListener { dismiss() }
    }
}