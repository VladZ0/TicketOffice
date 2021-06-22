package com.vlad.ticketoffice.MainActivity.ui.LoginFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {
    private var authMode = 1
    private lateinit var mBinding: FragmentLoginBinding
    private lateinit var mViewModel: LoginFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        mBinding = FragmentLoginBinding.bind(root)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel = ViewModelProvider(this, LoginFragmentVMFactory(requireActivity().application))
            .get(LoginFragmentViewModel::class.java)

        mBinding.tvLoginSignUp.setOnClickListener{
            toggleLoginMode()
        }

        mBinding.loginSignUpBtn.setOnClickListener{
            if(authMode == 0) {
                if(mBinding.etName.text.trim().toString()
                        .matches("([а-яА-Яa-zA-Z]*) ([а-яА-Яa-zA-Z]*)".toRegex())) {
                    mViewModel.signUp(
                        mBinding.etName.text.toString().trim(),
                        mBinding.etEmailAddress.text.toString().trim(),
                        mBinding.etPassword.text.toString().trim()
                    )
                }
                else{
                    Toast.makeText(requireContext(), "Введіть ім'я та прізвище!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            else {
                mViewModel.signIn(mBinding.etEmailAddress.text.toString().trim(),
                    mBinding.etPassword.text.toString().trim())
            }
        }

    }

    private fun toggleLoginMode(){
        if(authMode == 0){
            mBinding.tvLoginSignUp.setText(R.string.toggling_btn_login)
            mBinding.loginSignUpBtn.setText(R.string.login)
            mBinding.etName.setText("")
            mBinding.etName.visibility = View.GONE
            authMode = 1
        }
        else{
            mBinding.tvLoginSignUp.setText(R.string.toggling_btn_signUp)
            mBinding.loginSignUpBtn.setText(R.string.sign_up)
            mBinding.etName.visibility = View.VISIBLE
            authMode = 0
        }
    }
}