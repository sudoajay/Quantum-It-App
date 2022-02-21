package com.sudoajay.quantumit_app.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sudoajay.firebase_chat.helper.Toaster
import com.sudoajay.quantumit_app.R
import com.sudoajay.quantumit_app.data.NewsApiInterface
import com.sudoajay.quantumit_app.data.NewsApiInterface.Companion.STARTING_PAGE
import com.sudoajay.quantumit_app.model.Article
import retrofit2.HttpException
import java.io.IOException

class ArticlePagingSourceNetwork(
    private val newsApiInterface: NewsApiInterface,
    private val context: Context,
    private val searchValue: String,
    private val dataType: Int
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        //for first case it will be null, then we can pass some default value, in our case it's 1
        Log.e("NewsTAG", "asdasdasd coming")

        val page = params.key ?: STARTING_PAGE
        return try {

            val response = if (dataType == 1) newsApiInterface.getEverything(
                search = searchValue,
                page = page
            ) else newsApiInterface.getTopHeadlines(search = searchValue, page = page)
            val article = response.articles
            Log.e("NewsTAG", "article -- ${article}")

            LoadResult.Page(
                data = article,
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if ((page * 10) == response.totalResults) null else page + 1
            )

        } catch (exception: IOException) {
            Toaster.showToast(context, "Exception  -----  ${exception.message}")
            Log.e("NewsTAG", "Exception  - ${exception.message}   ----  ${exception.message}")

            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.e("NewsTAG", "Exception  - ${exception.message()}   ----  ${exception.message}")
            Toaster.showToast(context, "Exception  - ${exception.message()}   ----  ${exception.message}")

            return LoadResult.Error(exception)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
