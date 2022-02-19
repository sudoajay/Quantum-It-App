package com.sudoajay.firebase_chat.helper

import android.content.Context
import android.widget.Toast

object Toaster {
    fun showToast(context: Context ,  message:String){
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

}