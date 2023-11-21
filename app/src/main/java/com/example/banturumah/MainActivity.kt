package com.example.banturumah

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    lateinit var webView: WebView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var filePath: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.WV)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                if (url != null) {
                    if (url.startsWith("whatsapp://send/")) {
                        // Handle WhatsApp URL
                        try {
                            val parts = url.split("/send/?phone=")
                            val phoneNumber = parts[1].substringBefore("&text=")
                            val message = Uri.decode(parts[1].substringAfter("&text="))

                            val whatsappIntent = Intent(Intent.ACTION_VIEW)
                            whatsappIntent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$message")
                            startActivity(whatsappIntent)

                            return true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (url.startsWith("intent://")) {
                        // Handle the "intent://" URL scheme here
                        try {
                            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            startActivity(intent)
                            return true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (url.startsWith("fb://")) {
                        // Handle the "fb://" URL scheme here
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                            return true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

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
