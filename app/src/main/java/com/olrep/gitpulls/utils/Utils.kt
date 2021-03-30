package com.olrep.gitpulls.utils

object Utils {
    const val TAG = "GitPulls_"
    const val ITEMS_PER_PAGE = 15   // just a globally configurable value

    fun shouldLoadMore(localSize: Int, remoteSize: Int): Boolean {
        return localSize < remoteSize
    }
}