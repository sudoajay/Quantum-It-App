package com.sudoajay.quantumit_app.util

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.sudoajay.quantumit_app.R
import java.util.*


@BindingAdapter("app:image_url")
fun loadImage(view: ImageView, url: String?) {
    try {
        Glide.with(view)
            .load(url)
            .error(R.drawable.news_api)
            .placeholder(R.drawable.news_api)
            .centerCrop()
            .into(view)
    } catch (e: Exception) {
        view.setImageResource(R.drawable.news_api)
    }
}


@SuppressLint("SetTextI18n")
@BindingAdapter("app:toLocalDate")
fun toLocalDate(textView: TextView, t1: String?) {
    if (t1 == null) {
        textView.visibility = View.GONE
    } else {
        textView.text = "Publish At\n"+toLocalDate(t1)
        textView.visibility = View.VISIBLE

    }
}
