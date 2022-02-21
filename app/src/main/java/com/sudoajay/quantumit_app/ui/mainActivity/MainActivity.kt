package com.sudoajay.quantumit_app.ui.mainActivity

import android.os.Build
import android.os.Bundle
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.sudoajay.quantumit_app.R
import com.sudoajay.quantumit_app.databinding.ActivityMainBinding
import com.sudoajay.quantumit_app.ui.BaseActivity

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainActivity : BaseActivity() {
    var TAG = "MainActivityTAG"

    private lateinit var binding: ActivityMainBinding

    private var isDarkTheme: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isDarkTheme =  isSystemDefaultOn(resources)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    true
            }

        }


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


    }



}