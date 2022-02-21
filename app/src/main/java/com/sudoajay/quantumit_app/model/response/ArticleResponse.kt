package com.sudoajay.quantumit_app.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sudoajay.quantumit_app.model.Article
import kotlinx.parcelize.Parcelize
import java.io.Serializable


data class ArticleResponse (
    @SerializedName("status") var status: String?,
    @SerializedName("totalResults") var totalResults: Int ,
    @SerializedName("articles") var articles: List<Article>
):Serializable