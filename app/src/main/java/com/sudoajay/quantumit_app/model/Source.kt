package com.sudoajay.quantumit_app.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable


data class Source(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("language") var language: String? = null,
    @SerializedName("country") var country: String? = null
):Serializable