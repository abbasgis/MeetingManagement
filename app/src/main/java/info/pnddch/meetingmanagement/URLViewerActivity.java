package info.pnddch.meetingmanagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.ProgressDialog;

public class URLViewerActivity extends AppCompatActivity {

    ProgressDialog progressBar;
    WebView wv;

    private class MyBrowser extends WebViewClient {
        private MyBrowser() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            if (URLViewerActivity.this.progressBar.isShowing()) {
                URLViewerActivity.this.progressBar.dismiss();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_urlviewer);
        this.progressBar = ProgressDialog.show(this, "Loading. . ", "Please Wait. . ");
        this.wv = (WebView) findViewById(R.id.webView);
        this.wv.setPadding(0, 0, 0, 0);
//        this.wv.getSettings().setLoadWithOverviewMode(true);
//        this.wv.getSettings().setUseWideViewPort(true);
        this.wv.getSettings().setSupportZoom(true);
        this.wv.getSettings().setBuiltInZoomControls(true);
        this.wv.setWebViewClient(new MyBrowser());
        this.wv.getSettings().setLoadsImagesAutomatically(true);
        this.wv.getSettings().setJavaScriptEnabled(true);
        this.wv.getSettings().setDomStorageEnabled(true);
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("url");
            this.wv.loadUrl(url);
        }

    }

}