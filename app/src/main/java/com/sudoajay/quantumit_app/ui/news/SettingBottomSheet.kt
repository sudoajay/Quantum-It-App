package com.sudoajay.quantumit_app.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.quantumit_app.R
import com.sudoajay.quantumit_app.databinding.LayoutSettingBottomSheetBinding


class SettingBottomSheet(private val viewModel: NewsViewModel) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val myDrawerView = layoutInflater.inflate(R.layout.layout_setting_bottom_sheet, null)
        val binding = LayoutSettingBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.viewModel = viewModel
        binding.bottomSheet = this
        binding.lifecycleOwner = this

        return binding.root
    }


    fun setValue(dataType: Int) {
        viewModel.dataType.value = dataType
        dismiss()
    }
}