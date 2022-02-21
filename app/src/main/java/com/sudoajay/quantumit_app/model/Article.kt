package com.sudoajay.quantumit_app.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable


data class Article (
    @SerializedName("source")var source: Source? = null,
    @SerializedName("author")var author: String? = null,
    @SerializedName("title")var title: String? = null,
    @SerializedName("description")var description: String? = null,
    @SerializedName("url")var url: String? = null,
    @SerializedName("urlToImage")var urlToImage: String? = null,
    @SerializedName("publishedAt") var publishedAt: String? = null
): Serializable