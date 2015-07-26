package com.hackthon.amberalertcn.client;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
/**
 * ��¼�ٶ��˺ų�ʼ��Channel��Activity
 */
public class LoginActivity extends Activity {

    /** log tag. */
    private static final String TAG = LoginActivity.class.getSimpleName();

    private WebView mWebView;

    /** redirect uri ֵΪ"oob" */
    private static final String REDIRECT = "oob";
    private final String API_KEY = "TfBK6mlV3UybD2CgirQVd8O2";
    static final String DEV_CENTER = "https://openapi.baidu.com/";

    public static final String ACCESS_TOKEN = "AccessToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView = new WebView(LoginActivity.this);

        setContentView(mWebView);

        initWebView(mWebView);

        getAccessToken();
    }

    /**
     * ��ȡ access token��url
     */
    private void getAccessToken() {
        String url = DEV_CENTER + "oauth/2.0/authorize?"
                + "response_type=token" + "&client_id="
                + API_KEY
                + "&redirect_uri=" + REDIRECT + "&display=mobile";

        Log.d(TAG, "GetAccessTokenUrl: " + url);

        mWebView.loadUrl(url);
    }

    /**
     * ����Webview��WebviewClient
     *
     * @param webview
     *            webview
     */
    private void initWebView(WebView webview) {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view,SslErrorHandler handler, SslError error) {
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (url.startsWith(REDIRECT) || url.contains("login_success")) {

                    // change # -> ?
                    int fragmentIndex = url.indexOf("#");
                    url = "http://localhost/?"
                            + url.substring(fragmentIndex + 1);

                    // ��URL�л��Access token
                    String accessToken = Uri.parse(url).getQueryParameter(
                            "access_token");
                    Log.d(TAG, ">>> Get Original AccessToken: \r\n"
                            + accessToken);

                    Toast.makeText(LoginActivity.this,
                            "get access token success", Toast.LENGTH_SHORT)
                            .show();

                    // ?��¼
                    /*
                    Intent intent = new Intent(Utils.ACTION_LOGIN);
                    intent.setClass(LoginActivity.this, PushDemoActivity.class);
                    intent.putExtra(Utils.EXTRA_ACCESS_TOKEN, accessToken);
                    startActivity(intent);*/

                    Intent result = new Intent();
                    result.putExtra(ACCESS_TOKEN, accessToken);
                    setResult(Activity.RESULT_OK, result);

                    finish();
                }
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (goBack()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean goBack() {
        WebView webView = mWebView;
        if (webView != null && webView.canGoBack()) {
            webView.goBack();

            return true;
        }

        return false;
    }
}
