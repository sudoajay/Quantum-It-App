package com.sudoajay.quantumit_app.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.sudoajay.firebase_chat.helper.Toaster
import com.sudoajay.quantumit_app.R
import com.sudoajay.quantumit_app.databinding.FragmentLoginBinding
import com.sudoajay.quantumit_app.ui.BaseActivity


class Login : Fragment() {

    private var isDarkTheme: Boolean = false
    private lateinit var binding: FragmentLoginBinding
    private var TAG = "LoginTAG"

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myDrawerView = layoutInflater.inflate(R.layout.fragment_login, null)
        binding = FragmentLoginBinding.inflate(layoutInflater, myDrawerView as ViewGroup, false)
        binding.fragment = this
        binding.lifecycleOwner = this

        reference()

        return binding.root

    }

    private fun reference() {
        mAuth = FirebaseAuth.getInstance()

        isDarkTheme = BaseActivity.isSystemDefaultOn(resources)
        binding.headingImageView.setImageResource(if(isDarkTheme) R.drawable.login_night else R.drawable.login)

        binding.emailOrPhoneTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank() )
                binding.emailOrPhoneTextInputLayout.isErrorEnabled = false
        }
        binding.passwordTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank() )
                binding.passwordTextInputLayout.isErrorEnabled = false
        }

    }

    fun openSendFeedback(){
        Navigation.findNavController(binding.root).navigate(R.id.action_nav_login_to_sendFeedback)

    }
    fun openSignUp(){
        Navigation.findNavController(binding.root).navigate(R.id.action_open_signup)

    }

    fun clickLoginButton(){
        if(!isStillError()) {
            val emailOrPhone = binding.emailOrPhoneTextInputLayoutEditText.text.toString().replace("\\s".toRegex(),"")
            val pass = binding.passwordTextInputLayoutEditText.text.toString().replace("\\s".toRegex(),"")
            login(emailOrPhone, pass)
        }
    }

    private fun isStillError():Boolean{
        var value = ""
        var isEmpty = true
        when {
            binding.emailOrPhoneTextInputLayoutEditText.text.isNullOrBlank() -> {
                value = getString(R.string.emailOrPhoneEmpty_text)
                binding.emailOrPhoneTextInputLayout.error = value
            }
            binding.passwordTextInputLayoutEditText.text.isNullOrBlank() -> {
                value = getString(R.string.passwordEmpty_text)
                binding.passwordTextInputLayout.error =value
            }
            else -> isEmpty = false
        }
        if (value.isNotBlank())
            throwToaster(value)
        return isEmpty
    }

    private fun throwToaster(value:String?){
        Toaster.showToast(requireContext(),value?:"")
    }

    private fun login(email:String , pass:String){

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.i(TAG, "createUserWithEmail:success")
//                Navigation.findNavController(binding.root).navigate(R.id.action_nav_login_to_friendsActivity)

            } else {
                // If sign in fails, display a message to the user.
                Log.e(TAG, "createUserWithEmail:failure ${it.exception}")
                val split =it.exception.toString().split(": ")
                throwToaster(split[1])
            }
        }
    }



}