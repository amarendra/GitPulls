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
        progress.value = Pair(first = true, second = false) // show progress bar whenever an api call is made - this is the start to error flag will be false

        val page = if (user != username.value) 1 else (currentPage + 1) // in case when a fresh user is being searched

        Log.d(TAG, "getPulls called - currentPage is $currentPage, totalCount is $totalCount, " +
                "called user is $user, saved username is ${username.value}, so page is $page")

        GithubRepository.getClosedPulls(user, page, object : OnResponse<Items> {
            override fun onSuccess(result: Items?) {
                Log.d(TAG, "onSuccess: $result")
                var failure = true  // use this flag to show error if received data isn't proper

                // if there's no proper data or any incomplete result, treat it as error - otherwise there will be a laundry list of null checks
                if (result != null && result.items.isNotEmpty() && !result.incomplete_results) {
                    Log.d(TAG, "Result is available, setting data")

                    if (user != username.value) {
                        Log.d(TAG, "Setting new username. old user name is ${username.value} ")
                        username.value = user   // this should trigger old value reset in the view
                        currentPage = 1 // user name changed just now so it has to be the first page
                    } else {
                        currentPage++   // same user name, increment page
                    }

                    totalCount = result.total_count // update total count for a query in every call - query will not change unless username name changes

                    failure = false
                    pulls.value = result.items  // finally setting the newly received list of pull data received in this call
                }

                progress.value = Pair(first = false, second = failure)  // progress bar hidden and error flag based on value of success
            }

            override fun onError(error: Throwable) {
                Log.d(TAG, "onError: $error")
                progress.value = Pair(first = false, second = true) // hide progress and show error
            }
        })
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private const val TAG = Utils.TAG + "MVM"
    }
}