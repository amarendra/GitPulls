package com.olrep.gitpulls.ui.web

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.olrep.gitpulls.R
import com.olrep.gitpulls.utils.Utils

class WebActivity : AppCompatActivity() {
    private val webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_activity)

        val url: String? = intent.getStringExtra(Utils.KEY_PR_URL)
        val title: String? = intent.getStringExtra(Utils.KEY_PR_TITLE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        val webView = findViewById<WebView>(R.id.wv)
        webView.webViewClient = WebViewClient()

        url?.let { webView.loadUrl(it) }
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
    }

    override fun onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}