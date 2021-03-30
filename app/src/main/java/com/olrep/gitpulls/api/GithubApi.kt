package com.olrep.gitpulls.api

import com.olrep.gitpulls.model.Items
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/issues")
    fun getClosedPulls(
        @Query("q", encoded = true) searchQuery: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<Items>
}