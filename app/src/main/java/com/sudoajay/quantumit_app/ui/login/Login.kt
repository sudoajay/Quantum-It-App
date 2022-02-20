package com.sudoajay.quantumit_app.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sudoajay.firebase_chat.helper.Toaster
import com.sudoajay.quantumit_app.R
import com.sudoajay.quantumit_app.databinding.FragmentLoginBinding
import com.sudoajay.quantumit_app.ui.BaseActivity
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog


class Login : Fragment() {

    private var isDarkTheme: Boolean = false
    private lateinit var binding: FragmentLoginBinding
    private var TAG = "LoginTAG"
    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var callbackManager: CallbackManager
//    private lateinit var buttonFacebookLogin: LoginButton

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
        googleSetUp()

        return binding.root

    }
    private fun googleSetUp(){
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun facebookSetUp(){
        // Initialize Facebook Login button
//        callbackManager = CallbackManager.Factory.create()
//
//        buttonFacebookLogin.setReadPermissions("email", "public_profile")
//        buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(loginResult: LoginResult) {
//                Log.d(TAG, "facebook:onSuccess:$loginResult")
//                handleFacebookAccessToken(loginResult.accessToken)
//            }
//
//            override fun onCancel() {
//                Log.d(TAG, "facebook:onCancel")
//            }
//
//            override fun onError(error: FacebookException) {
//                Log.d(TAG, "facebook:onError", error)
//            }
//        })

    }

    private fun reference() {
        mAuth = FirebaseAuth.getInstance()

        isDarkTheme = BaseActivity.isSystemDefaultOn(resources)
        binding.headingImageView.setImageResource(if (isDarkTheme) R.drawable.login_night else R.drawable.login)

        binding.emailTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                binding.emailTextInputLayout.isErrorEnabled = false
        }
        binding.passwordTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                binding.passwordTextInputLayout.isErrorEnabled = false
        }

    }

    fun openSendFeedback() {
        Navigation.findNavController(binding.root).navigate(R.id.action_nav_login_to_sendFeedback)

    }

    fun openSignUp() {
        Navigation.findNavController(binding.root).navigate(R.id.action_open_signup)

    }

    fun clickLoginButton() {
        if (!isStillError()) {
            val emailOrPhone = binding.emailTextInputLayoutEditText.text.toString()
                .replace("\\s".toRegex(), "")
            val pass =
                binding.passwordTextInputLayoutEditText.text.toString().replace("\\s".toRegex(), "")
            login(emailOrPhone, pass)
        }
    }

    private fun isStillError(): Boolean {
        var value = ""
        var isEmpty = true
        when {
            binding.emailTextInputLayoutEditText.text.isNullOrBlank() -> {
                value = getString(R.string.emailOrPhoneEmpty_text)
                binding.emailTextInputLayout.error = value
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
        Toaster.showToast(requireContext(), value ?: "")
    }

    private fun login(email: String, pass: String) {

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.i(TAG, "createUserWithEmail:success")
                if (isVerifiedEmail()) {

                }

            } else {
                // If sign in fails, display a message to the user.
                Log.e(TAG, "createUserWithEmail:failure ${it.exception}")
                val split = it.exception.toString().split(": ")
                throwToaster(split[1])
            }
        }
    }

    private fun isVerifiedEmail(): Boolean {
        val firebaseUser = mAuth.currentUser
        if (firebaseUser != null) {
            if (!firebaseUser.isEmailVerified) {
                sendEmailVerification()
                return false
            }
        }
        return true
    }

    private fun sendEmailVerification() {
        val firebaseUser = mAuth.currentUser
        firebaseUser?.sendEmailVerification()?.addOnSuccessListener {
            emailSent()
        }?.addOnFailureListener { exception ->
            Toaster.showToast(requireContext(), "$exception error")
        }
    }


    private fun emailSent() {

        val mBottomSheetDialog = BottomSheetMaterialDialog.Builder(requireActivity())
            .setAnimation(R.raw.email)
            .setTitle(getString(R.string.verification_send_text))
            .setMessage(getString(R.string.please_verify_email_text))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok_text),
                R.drawable.ic_done
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                findNavController().popBackStack(R.id.nav_login, false)
            }
            .setNegativeButton(
                getString(R.string.resend),
                R.drawable.ic_round_redo
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()

            }
            .build()

        val params = mBottomSheetDialog.animationView.layoutParams as RelativeLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)

        mBottomSheetDialog.show()
    }

    fun googleLogin() {
        // Configure Google Sign In
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toaster.showToast(requireContext(),"successfuly login")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toaster.showToast(requireContext(),"except -  ${task.exception} ")
                }
            }
    }


    fun facebookLoginIn(){

    }



    companion object {
        const val RC_SIGN_IN = 9001
    }


}