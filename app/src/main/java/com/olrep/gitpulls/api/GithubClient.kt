package com.olrep.gitpulls.api

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GithubClient {
    private var client: Retrofit

    init {
        client = initClient()
    }

    private fun initClient(): Retrofit {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.interceptors().add(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                var request: Request = chain.request()

                val url: HttpUrl = request.url.newBuilder()
                    .addQueryParameter("sort", "created")
                    .addQueryParameter("order", "asc")
                    .build()

                request = request.newBuilder()
                    .url(url)
                    .build()
                return chain.proceed(request)
            }

        })

        client = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        return client
    }

    fun getInstance(): Retrofit {
        return client
    }
}