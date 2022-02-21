package com.sudoajay.quantumit_app.data.repository

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sudoajay.quantumit_app.databinding.HolderArticleBinding
import com.sudoajay.quantumit_app.model.Article
import javax.inject.Inject


class NewsPagingAdapter @Inject constructor(
) : PagingDataAdapter<Article, NewsViewHolder>(Person_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NewsViewHolder(
            HolderArticleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        Log.e("NewsTAG","Holder ---- position - ${position}")
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val Person_COMPARATOR = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.source!!.id == newItem.source!!.id

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem
        }
    }


}