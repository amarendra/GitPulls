package com.olrep.gitpulls.model


data class Items(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<Item>
)

data class Item(
    val url: String,    // issue url
    val repository_url: String,
    val html_url: String,   // PR url
    val number: Int,        // pr or issue number
    val title: String,
    val user: User,
    val state: String,
    val created_at: String,
    val closed_at: String
)

data class User(
    val login: String,
    val avatar_url: String,
    val html_url: String    // user profile page url
)