package com.mbabo.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mbabo.android.R;
import com.mbabo.android.global.Apis;
import com.mbabo.android.global.Functions;

public class EventActivity extends Activity implements OnClickListener {
	WebView webview;
	String UserEmail;
	WebSettings websetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.event);
		initView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void initView() {
		webview = (WebView) findViewById(R.id.webView2);
		UserEmail = Functions.UserEmailFetcher
				.getEmail(getApplicationContext());
		webview.setWebViewClient(new webViewClient());
		websetting = webview.getSettings();
		websetting.setJavaScriptEnabled(true);
		websetting.setJavaScriptCanOpenWindowsAutomatically(true);
		websetting.setDomStorageEnabled(true);
		websetting.setAppCacheEnabled(true);
		websetting.setPluginState(PluginState.ON);
		Functions.checkNetwork(getApplicationContext());
		UserEmail = Functions.UserEmailFetcher
				.getEmail(getApplicationContext());
		webview.setWebViewClient(new webViewClient());
		webview.loadUrl(Apis.URLEventMobile);
	}

	public class webViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('body')[0].innerHTML);");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Intent intent = new Intent(EventActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

}
