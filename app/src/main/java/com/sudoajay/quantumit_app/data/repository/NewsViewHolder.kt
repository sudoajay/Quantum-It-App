package com.sudoajay.quantumit_app.data.repository


import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.quantumit_app.databinding.HolderArticleBinding
import com.sudoajay.quantumit_app.model.Article


class NewsViewHolder(
    private val binding: HolderArticleBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(article: Article) {
        binding.article = article
        Log.e("NewsTAG","article - ${article.title}")
    }


}