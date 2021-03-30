package com.olrep.gitpulls.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    const val TAG = "GitPulls_"
    const val ITEMS_PER_PAGE = 15   // just a globally configurable value

    const val KEY_PR_URL = "pr_url"
    const val KEY_PR_TITLE = "pr_title"

    fun shouldLoadMore(localSize: Int, remoteSize: Int): Boolean {
        return localSize < remoteSize
    }

    fun getTimeWLabel(time: String, isCreated: Boolean): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd MMM yy   hh:mm a", Locale.ENGLISH)
        val date: Date? = inputFormat.parse(time)

        val formattedDate: String

        if (date != null) {
            formattedDate = outputFormat.format(date)
        } else {
            formattedDate = time
        }

        return (if(isCreated) "Created " else "Closed ") + formattedDate
    }
}