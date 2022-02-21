package com.sudoajay.quantumit_app.ui.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sudoajay.quantumit_app.data.NewsApiInterface.Companion.NETWORK_PAGE_SIZE
import com.sudoajay.quantumit_app.data.NewsInterfaceBuilderGson
import com.sudoajay.quantumit_app.data.repository.ArticlePagingSourceNetwork
import com.sudoajay.quantumit_app.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val _application = application

    var searchValue: String = "tesla"
    var dataType: MutableLiveData<Int> = MutableLiveData()

    var hideProgress: MutableLiveData<Boolean> = MutableLiveData()

    init {
        loadHideProgress()
        dataType.value = 1

    }

    private fun loadHideProgress() {
        hideProgress.value = false
    }

    fun callEverythingAPI(): Flow<PagingData<Article>> {

        val apiInterface =
            NewsInterfaceBuilderGson.getApiInterface()

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                ArticlePagingSourceNetwork(
                    apiInterface!!,
                    _application,
                    searchValue,
                    dataType.value ?: 1
                )
            }
        ).flow.cachedIn(viewModelScope)
    }
}