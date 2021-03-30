package com.olrep.gitpulls.datasource

import android.util.Log
import com.olrep.gitpulls.api.GithubApi
import com.olrep.gitpulls.api.GithubClient
import com.olrep.gitpulls.callback.OnResponse
import com.olrep.gitpulls.model.Items
import com.olrep.gitpulls.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GithubRepository {
    private var api: GithubApi = GithubClient().getInstance().create(GithubApi::class.java)
    private const val TAG = Utils.TAG + "GR"

    fun getClosedPulls(user: String, page: Int, callback: OnResponse<Items>) {
        val searchQuery = "is:pr+user:$user+state:closed"

        val apiCall: Call<Items> = api.getClosedPulls(searchQuery, page, Utils.ITEMS_PER_PAGE)
        apiCall.enqueue(object : Callback<Items> {
            override fun onResponse(call: Call<Items>, response: Response<Items>) {
                Log.d(TAG, "onResponse: $response")
                callback.onSuccess(response.body())
            }

            override fun onFailure(call: Call<Items>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
                callback.onError(t)
            }
        })
    }

}