package com.example.banturumah

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class MyWebViewClient : WebViewClient() {
    lateinit var progressBar: ProgressBar
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        progressBar.visibility = View.VISIBLE // Tampilkan ProgressBar saat halaman dimuat
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        progressBar.visibility = View.GONE // Sembunyikan ProgressBar saat halaman selesai dimuat
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        // Tampilkan ProgressBar saat pindah halaman
        progressBar.visibility = View.VISIBLE
        return super.shouldOverrideUrlLoading(view, request)
    }
}
