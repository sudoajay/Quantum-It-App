package com.sudoajay.quantumit_app.data

import com.sudoajay.quantumit_app.data.NewsApiInterface.Companion.baseUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NewsInterfaceBuilderGson {
    companion object {
        var picsumApiInterface: NewsApiInterface? = null
        private var okHttpClient: OkHttpClient? = null


        fun getApiInterface(): NewsApiInterface? {
            if (picsumApiInterface == null) {
                //For printing API url and body in logcat
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

                okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .callTimeout(50, TimeUnit.SECONDS)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient!!)
                    .build()
                picsumApiInterface = retrofit.create(NewsApiInterface::class.java)
            }

            return picsumApiInterface
        }

    }

}