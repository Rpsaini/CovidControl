package apps.envision.musicvibes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class ChatBot extends AppCompatActivity {

    private ProgressBar viewprogressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        getSupportActionBar().hide();
        viewprogressbar = findViewById(R.id.viewprogressbar);



        WebView mywebview = (WebView) findViewById(R.id.webview);
//        WebSettings webSettings = mywebview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//
//        if (Build.VERSION.SDK_INT >= 19) {
//            mywebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        }
//        else {
//            mywebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
//
//        mywebview.loadUrl("https://links.collect.chat/5e9d6e241214466c8162611d");

//        mywebview.setWebViewClient(new WebViewController());



        WebSettings settings = mywebview.getSettings();
        mywebview.loadUrl("https://links.collect.chat/5e9d6e241214466c8162611d");
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        mywebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mywebview.setScrollbarFadingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mywebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mywebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewprogressbar.setVisibility(View.GONE);
            }
        },7000);



    }

    public class WebViewController extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
          //  viewprogressbar.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //viewprogressbar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            System.out.println("webview error===="+error);
        }
    }

}