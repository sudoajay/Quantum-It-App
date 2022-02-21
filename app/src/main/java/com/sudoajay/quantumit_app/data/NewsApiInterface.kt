package com.sudoajay.quantumit_app.data

import com.sudoajay.quantumit_app.model.response.ArticleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiInterface {


    @GET("/v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("q") search: String="tesla",
        @Query("page") page: Int,
        @Query("pageSize")pageSize:Int = NETWORK_PAGE_SIZE,
        @Query("apiKey") apiKey: String = APIKEY
    ): ArticleResponse

    @GET("/v2/everything")
    suspend fun getEverything(
        @Query("q") search: String="tesla",
        @Query("page") page: Int,
        @Query("pageSize")pageSize:Int = NETWORK_PAGE_SIZE,
        @Query("apiKey") apiKey: String = APIKEY
    ): ArticleResponse


    companion object {
        const val baseUrl = "https://newsapi.org"
        const val APIKEY = "0b9a97755108426c88982ce7d9428d40"  // 21e80363b3e74a85ba7819a9b7c9f8e1
        const val NETWORK_PAGE_SIZE = 10
        const val STARTING_PAGE = 1

    }


}