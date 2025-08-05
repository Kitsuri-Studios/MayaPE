package io.kitsuri.mayape.ui.components.landing

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AuthenticationWebView(url: String, code: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, pageUrl: String?) {
                        super.onPageFinished(view, pageUrl)
                        view?.evaluateJavascript(
                            """
                            (function() {
                                setTimeout(function() {
                                    var inputs = document.querySelectorAll('input[name="otc"], input[type="text"], input[placeholder*="code"], input[id*="code"]');
                                    for (var i = 0; i < inputs.length; i++) {
                                        var input = inputs[i];
                                        if (input && input.offsetParent !== null) {
                                            input.value = '$code';
                                            input.focus();
                                            var event = new Event('input', { bubbles: true });
                                            input.dispatchEvent(event);
                                            var changeEvent = new Event('change', { bubbles: true });
                                            input.dispatchEvent(changeEvent);
                                            break;
                                        }
                                    }
                                }, 1000);
                            })();
                            """.trimIndent()
                        ) {}
                    }
                }
                loadUrl(url)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
    )
}
