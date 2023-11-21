package com.example.banturumah

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var filePath: ValueCallback<Array<Uri>>? = null

    companion object {
        private const val FILE_UPLOAD_REQUEST_CODE = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.WV)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://nauvan.my.id")

        websettingsview()

        swipeRefreshLayout.setOnRefreshListener {
            webView.reload()
            Handler().postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 2000)
        }

        uploadchoosefiles()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun websettingsview() {
        val websettings = webView.settings

        websettings.javaScriptEnabled = true
        websettings.domStorageEnabled = true
        websettings.databaseEnabled = true
        websettings.allowFileAccess = true
    }

    fun uploadchoosefiles() {
        val getFile = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                filePath?.onReceiveValue(null)
            } else if (result.resultCode == Activity.RESULT_OK && filePath != null) {
                val data = result.data
                if (data != null) {
                    val selectedFileUri = data.data
                    if (selectedFileUri != null) {
                        val selectedFileArray = arrayOf(selectedFileUri)
                        filePath?.onReceiveValue(selectedFileArray)
                    } else {
                        filePath?.onReceiveValue(null)
                    }
                } else {
                    filePath?.onReceiveValue(null)
                }
                filePath = null
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                filePath = filePathCallback
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                getFile.launch(intent)
                return true
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
