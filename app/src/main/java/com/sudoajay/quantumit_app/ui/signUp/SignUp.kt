package com.sudoajay.quantumit_app.ui.signUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sudoajay.firebase_chat.helper.Toaster
import com.sudoajay.quantumit_app.R
import com.sudoajay.quantumit_app.databinding.FragmentSignupBinding
import com.sudoajay.quantumit_app.model.User
import com.sudoajay.quantumit_app.ui.BaseActivity

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUp : Fragment() {

    private var isDarkTheme: Boolean = false
    lateinit var binding: FragmentSignupBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var TAG = "SignUpTAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myDrawerView = layoutInflater.inflate(R.layout.fragment_signup, null)
        binding = FragmentSignupBinding.inflate(layoutInflater, myDrawerView as ViewGroup, false)
        binding.fragment = this
        binding.lifecycleOwner = this

        reference()

        return binding.root

    }


    private fun reference() {
        isDarkTheme = BaseActivity.isSystemDefaultOn(resources)
        binding.headingImageView.setImageResource(if (isDarkTheme) R.drawable.signup_night else R.drawable.signup)
        mAuth = FirebaseAuth.getInstance()

        binding.firstNameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                binding.firstNameTextInputLayout.isErrorEnabled = false
        }
        binding.lastNameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                binding.lastNameTextInputLayout.isErrorEnabled = false
        }

        binding.emailOrPhoneTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                binding.emailOrPhoneTextInputLayout.isErrorEnabled = false
        }
        binding.passwordTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                binding.passwordTextInputLayout.isErrorEnabled = false
        }

    }

    fun clickSignUpButton() {
        if (!isStillError()) {
            val fullName = "${binding.firstNameTextInputLayoutEditText.text} ${binding.lastNameTextInputLayoutEditText.text}"
            val emailOrPhone = binding.emailOrPhoneTextInputLayoutEditText.text.toString().replace("\\s".toRegex(),"")
            val pass = binding.passwordTextInputLayoutEditText.text.toString().replace("\\s".toRegex(),"")

            signUp(fullName,emailOrPhone, pass)
        }
    }

    private fun isStillError(): Boolean {
        var value = ""
        var isEmpty = true
        when {
            binding.firstNameTextInputLayoutEditText.text.isNullOrBlank() -> {
                value = getString(R.string.somethingEmpty_text)
                binding.firstNameTextInputLayout.error = value
            }
            binding.lastNameTextInputLayoutEditText.text.isNullOrBlank() -> {
                value = getString(R.string.somethingEmpty_text)
                binding.lastNameTextInputLayout.error = value
            }
            binding.emailOrPhoneTextInputLayoutEditText.text.isNullOrBlank() -> {
                value = getString(R.string.emailOrPhoneEmpty_text)
                binding.emailOrPhoneTextInputLayout.error = value
            }
            binding.passwordTextInputLayoutEditText.text.isNullOrBlank() -> {
                value = getString(R.string.passwordEmpty_text)
                binding.passwordTextInputLayout.error = value
            }
            else -> isEmpty = false
        }
        if (value.isNotBlank())
            throwToaster(value)
        return isEmpty
    }

    private fun throwToaster(value: String?) {
        Toaster.showToast(requireContext(), value?:"")
    }


    private fun signUp(fullName:String , emailOrPhone: String, pass: String) {

        mAuth.createUserWithEmailAndPassword(emailOrPhone, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.i(TAG, "createUserWithEmail:success")
                val user = mAuth.currentUser
                addUserToDataBase(fullName,emailOrPhone, user?.uid!!)
//                Navigation.findNavController(binding.root).navigate(R.id.action_nav_signup_to_friendsActivity)
            } else {
                // If sign in fails, display a message to the user.
                Log.e(TAG, "createUserWithEmail:failure ${it.exception}")
                val split =it.exception.toString().split(": ")
                throwToaster(split[1])
            }
        }
    }

    private fun addUserToDataBase(fullName:String ,emailOrPhone: String, uid:String){
        val database = Firebase.database
        databaseReference = database.reference
        databaseReference.child("user").child(uid).setValue(User(fullName,emailOrPhone,uid))
    }
}
