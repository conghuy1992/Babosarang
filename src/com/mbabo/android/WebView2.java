package com.mbabo.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.widget.Button;
import android.widget.TextView;

import com.mbabo.android.R;

public class WebView2 extends Activity {

	private Button btnBack;
	WebView webViewPay;
	TextView btnDong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview2);
		btnDong = (TextView) findViewById(R.id.btnClose);
		webViewPay = (WebView) findViewById(R.id.webViewPAY);
		btnBack = (Button) findViewById(R.id.btn_back);
		Intent intent = getIntent();
		String GET_URL = intent.getStringExtra("SHOPPING_CART");
		String GET_NAME = intent.getStringExtra("SEND_NAME");
		// Toast.makeText(getApplicationContext(), "" + GET_NAME + "-" +
		// GET_URL,
		// Toast.LENGTH_SHORT).show();
		// startWebView(GET_URL);
		btnBack.setText("" + GET_NAME);
		btnDong.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// webViewPay.getSettings().setJavaScriptEnabled(true);
		// webViewPay.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// webViewPay.getSettings().setDomStorageEnabled(true);
		// webViewPay.getSettings().setAppCacheEnabled(true);
		// webViewPay.getSettings().setPluginState(PluginState.ON);
		// webViewPay.getSettings().setRenderPriority(RenderPriority.HIGH);
		// webViewPay.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);	
		// webViewPay.setWebViewClient(new webViewClient());
		// webViewPay.loadUrl(GET_URL);
		
		webViewPay.setWebChromeClient(new webViewChromeClient());
		startWebView(GET_URL);
	}

	public class webViewClient extends WebViewClient {
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			ShowCustomDialog();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			HideCustomDiaglog();
			super.onPageFinished(view, url);
			// view.loadUrl("javascript:document.getElementsByClassName('container')[0].style.display=\"none\";");

		}
	}

	public class webViewChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			if (message.equals("로그인")) {
				MainActivity.Lgin = false;
				result.confirm();
			} else if (message.equals("로그아웃")) {
				MainActivity.Lgin = true;
				result.confirm();
			} else if (message.contains("백신이 정상 동작 하지 않았습니다.")) {
				result.confirm();
			} else if (!message.equals("null")) {
				result.confirm();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						WebView2.this);
				builder.setTitle(R.string.app_name);
				builder.setMessage(message);
				builder.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								result.confirm();
							}
						});// .setCancelable(false).create().show();
				AlertDialog dialog = builder.create();
				dialog.show();
				if (message.equals("undefined")) {
					// Log.e("tag", "show it");
					dialog.dismiss();
				}

			} else
				result.confirm();
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {

			new AlertDialog.Builder(WebView2.this)
					.setTitle(R.string.app_name)
					.setMessage(message)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.cancel();
								}
							}).create().show();
			return true;
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog,
				boolean isUserGesture, Message resultMsg) {
			return super.onCreateWindow(view, isDialog, isUserGesture,
					resultMsg);
		}

		@Override
		public void onCloseWindow(WebView window) {
			super.onCloseWindow(window);
		}
	}

	Dialog mcustomdialog;
	MyProgressBar pBar;

	public void ShowCustomDialog() {
		try {

			mcustomdialog = new Dialog(WebView2.this);
			mcustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mcustomdialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			mcustomdialog.setCanceledOnTouchOutside(true);
			mcustomdialog.setContentView(R.layout.dialog_circle);
			mcustomdialog.show();

		} catch (Exception ex) {
			Log.e("Show Dialog", ex.toString());
		}
	}

	public void HideCustomDiaglog() {
		try {
			mcustomdialog.dismiss();
			mcustomdialog = null;

		} catch (Exception ex) {
			Log.e("Hide dialog", ex.toString());
		}
	}

	private void startWebView(String url) {
		webViewPay.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				ShowCustomDialog();
			}
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			public void onLoadResource(WebView view, String url) {
			}
			public void onPageFinished(WebView view, String url) {
				HideCustomDiaglog();

			}
		});
		webViewPay.getSettings().setJavaScriptEnabled(true);
		webViewPay.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webViewPay.getSettings().setDomStorageEnabled(true);
		webViewPay.getSettings().setAppCacheEnabled(true);
		webViewPay.getSettings().setPluginState(PluginState.ON);
		webViewPay.getSettings().setRenderPriority(RenderPriority.HIGH);
		webViewPay.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webViewPay.loadUrl(url);
	}
}
