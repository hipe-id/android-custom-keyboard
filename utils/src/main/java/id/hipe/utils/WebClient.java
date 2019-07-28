package id.hipe.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.RequiresApi;

/**
 * Created by dika on 06/03/18.
 */

public class WebClient extends WebViewClient {
    private Callback callback;

    public WebClient(Callback callback) {
        this.callback = callback;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
            view.getContext().startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (callback != null)
            callback.onError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (callback != null)
            callback.onLoadStart(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (callback != null)
            callback.onLoadFinished(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (callback != null)
            callback.onError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
    }

    public interface Callback {
        void onLoadStart(WebView view, String url, Bitmap favicon);

        void onLoadFinished(WebView view, String url);

        void onError(WebView view, int errorCode, String description, String failingUrl);
    }
}
