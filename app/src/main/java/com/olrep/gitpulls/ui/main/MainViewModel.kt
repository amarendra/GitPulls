package com.olrep.gitpulls.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olrep.gitpulls.api.GithubApi
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

    fun getPulls(author: String, perPage: Int, page: Int) {
        Log.d(TAG, "getPulls called")
        progress.value = Pair(first = true, second = false)

        GithubRepository.getClosedPulls(author, perPage, page, object : OnResponse<Items> {
            override fun onSuccess(result: Items?) {
                Log.d(TAG, "onSuccess: $result")
                var success = true

                if (result != null && result.items.isNotEmpty()) {
                    Log.d(TAG, "Result is available, setting data")

                    if (author != username.value) {
                        Log.d(TAG, "Setting new username. old user name is ${username.value} ")
                        username.value = author
                    }

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

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private const val TAG = Utils.TAG + "MVM"
    }
}