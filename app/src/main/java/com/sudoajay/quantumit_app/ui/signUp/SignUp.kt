package com.sudoajay.quantumit_app.ui.signUp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sudoajay.firebase_chat.helper.Toaster
import com.sudoajay.quantumit_app.R
import com.sudoajay.quantumit_app.databinding.FragmentSignupBinding
import com.sudoajay.quantumit_app.ui.BaseActivity
import com.sudoajay.quantumit_app.ui.login.Login
import com.sudoajay.quantumit_app.ui.social.FaceBookAuthActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog


@AndroidEntryPoint
class SignUp : Fragment() {

    private var isDarkTheme: Boolean = false
    lateinit var binding: FragmentSignupBinding
    private lateinit var mAuth: FirebaseAuth
    private var TAG = "SignUpTAG"
    private lateinit var googleSignInClient: GoogleSignInClient


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

        binding.emailTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                binding.emailTextInputLayout.isErrorEnabled = false
        }
        binding.passwordTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                binding.passwordTextInputLayout.isErrorEnabled = false
        }

    }

    fun clickSignUpButton() {
        if (!isStillError()) {
            val fullName =
                "${binding.firstNameTextInputLayoutEditText.text} ${binding.lastNameTextInputLayoutEditText.text}"
            val emailOrPhone = binding.emailTextInputLayoutEditText.text.toString()
                .replace("\\s".toRegex(), "")
            val pass =
                binding.passwordTextInputLayoutEditText.text.toString().replace("\\s".toRegex(), "")

            setupEmailVerification(emailOrPhone, pass)
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




    private fun setupEmailVerification(emailOrPhone: String, pass: String) {

        mAuth.createUserWithEmailAndPassword(emailOrPhone, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sendEmailVerification()
                } else {
                    // If sign in fails, display a message to the user.
                    val e = Log.e(TAG, "createUserWithEmail:failure ${it.exception}")
                    val split = it.exception.toString().split(": ")
                    throwToaster(split[1])
                }
            }

    }

    private fun sendEmailVerification() {
        val firebaseUser = mAuth.currentUser
        firebaseUser?.sendEmailVerification()?.addOnSuccessListener {
            emailSent()
        }?.addOnFailureListener { exception ->
            throwToaster( "$exception error")
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
        startActivityForResult(signInIntent, Login.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Login.RC_SIGN_IN) {
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
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_nav_signup_to_News)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                   throwToaster("except -  ${task.exception} ")
                }
            }
    }

    fun facebookLoginIn(){
        val intent = Intent(requireContext(), FaceBookAuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)

    }


}
