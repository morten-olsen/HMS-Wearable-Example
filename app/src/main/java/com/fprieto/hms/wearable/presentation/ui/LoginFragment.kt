package com.fprieto.hms.wearable.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fprieto.hms.wearable.R
import com.fprieto.hms.wearable.databinding.FragmentLoginBinding
import com.fprieto.hms.wearable.presentation.vm.LoginResult
import com.fprieto.hms.wearable.presentation.vm.LoginViewModel
import com.fprieto.hms.wearable.presentation.vm.observeEvent
import javax.inject.Inject

class LoginFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoginButton()
        observeViewModel()
    }

    private fun setupLoginButton() {
        binding.buttonLogin.setOnClickListener {
            val serverUrl = binding.editTextServerUrl.text.toString().trim()
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString() // No trim for password

            if (serverUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(serverUrl, username, password)
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observeEvent(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Loading -> {
                    binding.progressBarLogin.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }
                is LoginResult.Success -> {
                    binding.progressBarLogin.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Login Successful: ${result.loginResponse.user.username}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_libraryListFragment)
                }
                is LoginResult.Error -> {
                    binding.progressBarLogin.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Login Failed: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
