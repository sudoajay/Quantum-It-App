package com.sudoajay.quantumit_app.ui.news

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.sudoajay.firebase_chat.helper.Toaster
import com.sudoajay.quantumit_app.R
import com.sudoajay.quantumit_app.data.repository.NewsPagingAdapter
import com.sudoajay.quantumit_app.databinding.ActivityNewsApiBinding
import com.sudoajay.quantumit_app.ui.BaseActivity
import com.sudoajay.quantumit_app.ui.mainActivity.MainActivity
import com.sudoajay.quantumit_app.ui.sendFeedback.SendFeedback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class News : BaseActivity() {
    val viewModel: NewsViewModel by viewModels()
    lateinit var binding: ActivityNewsApiBinding
    private var isDarkTheme: Boolean = false
    private lateinit var newsPagingAdapter: NewsPagingAdapter
    private lateinit var settingBottomSheet: SettingBottomSheet
    private var doubleBackToExitPressedOnce = false
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkTheme = isSystemDefaultOn(resources)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    true
            }

        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_api)
        binding.viewmodel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
    }

    override fun onResume() {
        super.onResume()
        mAuth = FirebaseAuth.getInstance()
        settingBottomSheet = SettingBottomSheet(viewModel)
        googleSetUp()
        setReference()
    }

    private fun googleSetUp(){
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setReference() {

        //      Setup Swipe Refresh
        binding.swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(applicationContext, R.color.swipeBgColor)
        )

        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }

        //         Setup BottomAppBar Navigation Setup
        binding.bottomAppBar.navigationIcon?.mutate()?.let {
            it.setTint(
                ContextCompat.getColor(applicationContext, R.color.colorAccent)
            )
            binding.bottomAppBar.navigationIcon = it
        }
        setSupportActionBar(binding.bottomAppBar)


        setRecyclerView()

    }

    private fun setRecyclerView() {
        newsPagingAdapter = NewsPagingAdapter()

        binding.recyclerView.apply {
            this.layoutManager = LinearLayoutManager(applicationContext)
            this.adapter = newsPagingAdapter
        }


        viewModel.dataType.observeForever {
            refreshData()
        }

    }

    private fun callAPI() {
        lifecycleScope.launch {
            viewModel.callEverythingAPI()
                .collectLatest { pagingData ->
                    viewModel.hideProgress.postValue(true)
                    Log.e("NewsTAG", "asdasdasd ${pagingData}")
                    newsPagingAdapter.submitData(pagingData = pagingData)
                }
        }
    }


    private fun refreshData() {
        showProgressAndHideRefresh()
        CoroutineScope(Dispatchers.IO).launch {
            callAPI()
            delay(2000)
            viewModel.hideProgress.postValue(true)
        }
    }

    private fun showProgressAndHideRefresh() {
        if (binding.swipeRefresh.isRefreshing)
            binding.swipeRefresh.isRefreshing = false
        viewModel.hideProgress.value = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_toolbar_menu, menu)
        val actionSearch = menu.findItem(R.id.search_optionMenu)
        manageSearch(actionSearch)

        return super.onCreateOptionsMenu(menu)
    }

    private fun manageSearch(searchItem: MenuItem) {
        val searchView =
            searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        manageFabOnSearchItemStatus(searchItem)
        manageInputTextInSearchView(searchView)
    }

    private fun manageFabOnSearchItemStatus(searchItem: MenuItem) {
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                binding.deleteFloatingActionButton.hide()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                binding.deleteFloatingActionButton.show()
                return true
            }
        })
    }

    private fun manageInputTextInSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String): Boolean {
                val query: String = newText.lowercase(Locale.ROOT).trim { it <= ' ' }
                viewModel.searchValue = query
                refreshData()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {


                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> showNavigationDrawer()
            R.id.logOut_optionMenu -> logOut()
            R.id.sendFeedBack_optionMenu -> sendFeedBack()
            R.id.refresh_optionMenu -> refreshData()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun showNavigationDrawer() {
    }


    fun openSetting() {
        settingBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            settingBottomSheet.tag
        )

    }

    private fun logOut() {
        signOut()
        finish()
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun sendFeedBack() {
        startActivity(
            Intent(
                applicationContext,
                SendFeedback::class.java
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        signOut()
    }

    private fun signOut(){
        mAuth.signOut()
        googleSignInClient.signOut()
    }

    override fun onBackPressed() {
        onBack()
    }

    private fun onBack() {
        if (doubleBackToExitPressedOnce) {
            closeApp()
            return
        }
        doubleBackToExitPressedOnce = true
        Toaster.showToast(applicationContext, getString(R.string.click_back_text))
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            doubleBackToExitPressedOnce = false
        }
    }

    private fun closeApp() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(homeIntent)
    }
}