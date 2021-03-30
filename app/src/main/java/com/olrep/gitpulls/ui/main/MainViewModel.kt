package com.olrep.gitpulls.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olrep.gitpulls.callback.OnResponse
import com.olrep.gitpulls.datasource.GithubRepository
import com.olrep.gitpulls.model.Item
import com.olrep.gitpulls.model.Items
import com.olrep.gitpulls.utils.Utils
import kotlinx.coroutines.cancel

class MainViewModel : ViewModel() {
    val pulls = MutableLiveData<List<Item>>()
    val username = MutableLiveData<String>()
    val progress = MutableLiveData<Pair<Boolean, Boolean>>()    // This Pair depicts: <hide or show, error occurred>

    var currentPage = 0
    var totalCount = 0

    fun getPulls(user: String) {
        progress.value = Pair(first = true, second = false)

        val page = if (user != username.value) 1 else (currentPage + 1) // in case when a fresh user is being searched

        Log.d(TAG, "getPulls called - currentPage is $currentPage, totalCount is $totalCount, " +
                "called user is $user, saved username is ${username.value}, so page is $page")

        GithubRepository.getClosedPulls(user, page, object : OnResponse<Items> {
            override fun onSuccess(result: Items?) {
                Log.d(TAG, "onSuccess: $result")
                var success = true

                if (result != null && result.items.isNotEmpty()) {
                    Log.d(TAG, "Result is available, setting data")

                    if (user != username.value) {
                        Log.d(TAG, "Setting new username. old user name is ${username.value} ")
                        username.value = user   // this should trigger old value reset in the view
                        currentPage = 1
                    } else {
                        currentPage++
                    }

                    totalCount = result.total_count

                    success = false
                    pulls.value = result.items
                }

                progress.value = Pair(first = false, second = success)
            }

            override fun onError(error: Throwable) {
                Log.d(TAG, "onError: $error")
                progress.value = Pair(first = false, second = false)
            }
        })
    }

    // called when new user is being searched
    fun reset() {
        currentPage = 0
        totalCount = 0
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private const val TAG = Utils.TAG + "MVM"
    }
}