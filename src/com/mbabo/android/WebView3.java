package com.mbabo.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager.OnCancelListener;
import android.app.SearchManager.OnDismissListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mbabo.android.MainActivity._GetVersionInDialog;
import com.mbabo.android.global.Apis;
import com.mbabo.android.global.Functions;
import com.mbabo.android.global.Variables;
import com.mbabo.android.util.PreferencesHelper;

public class WebView3 extends Activity {

	private Button btnBack;
	WebView webViewPay;
	TextView btnDong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview3);
		btnDong = (TextView) findViewById(R.id.btnClose);
		webViewPay = (WebView) findViewById(R.id.webViewPAY);
		btnBack = (Button) findViewById(R.id.btn_back);
		Intent intent = getIntent();
		String GET_URL = intent.getStringExtra("ORDER");
		Log.e("Popup start with link", "" + GET_URL);
		String GET_NAME = intent.getStringExtra("SEND_NAME");

		btnBack.setText("" + GET_NAME);
		btnDong.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//
		webViewPay.setWebChromeClient(new webViewChromeClient());
		//
		webViewPay.getSettings().setJavaScriptEnabled(true);
		webViewPay.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webViewPay.getSettings().setDomStorageEnabled(true);
		webViewPay.getSettings().setAppCacheEnabled(true);
		webViewPay.getSettings().setPluginState(PluginState.ON);
		webViewPay.getSettings().setRenderPriority(RenderPriority.HIGH);
		webViewPay.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webViewPay.setWebViewClient(new webViewClient());
		webViewPay.loadUrl(GET_URL);
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
						WebView3.this);
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

			new AlertDialog.Builder(WebView3.this)
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

			mcustomdialog = new Dialog(WebView3.this);
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

	public class webViewClient extends WebViewClient {

		@SuppressWarnings("deprecation")
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);

			if (url.startsWith("http://www.mbabo.com/product/product_detail.zoomImage.php?")) {
				view.stopLoading();
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(i);
			}

			if (url.equals("http://www.facebook.com/babo7979")
					|| url.startsWith("http://www.facebook.com/babo7979")) {
				view.stopLoading();
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://www.facebook.com/babo7979"));
				startActivity(i);
			}
			if (url.equals("https://www.pinterest.com/babosarang/")
					|| url.startsWith("https://www.pinterest.com/babosarang/")) {
				view.stopLoading();
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://www.pinterest.com/babosarang/"));
				startActivity(i);
			}
			if (url.equals("https://instagram.com/babosarang79/")
					|| url.startsWith("https://instagram.com/babosarang79/")) {
				view.stopLoading();
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://instagram.com/babosarang79/"));
				startActivity(i);
			}
			if (url.equals("https://story.kakao.com/ch/babolover")
					|| url.startsWith("https://story.kakao.com/ch/babolover")) {
				view.stopLoading();
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://story.kakao.com/ch/babolover"));
				startActivity(i);
			}
			if (url.equals("https://twitter.com/babosarang79")
					|| url.startsWith("https://twitter.com/babosarang79")) {
				view.stopLoading();
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://twitter.com/babosarang79"));
				startActivity(i);
			}

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			ShowCustomDialog();
			if (url.contains("mbabo://window('CART','http://www.mbabo.com/cart/')")
					|| url.contains("mbabo://window('ORDER','http://www.mbabo.com/cart/order_form.v1.php')")) {
				HideCustomDiaglog();
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) {
			Log.d("get URL", "" + url);

			if ((url.startsWith("http://") || url.startsWith("https://"))
					&& url.endsWith(".apk")) {
				downloadFile(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
			/*
			 * else if (url.contains("mbabo.com")) { URLDefault = url;
			 * loadDefault(); return true; }
			 */
			else if ((url.startsWith("http://") || url.startsWith("https://"))
					&& (url.contains("market.android.com") || url
							.contains("m.ahnlab.com/kr/site/download"))) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(intent);
					return true;
				} catch (ActivityNotFoundException e) {
					return false;
				}
			} else if (url.startsWith("http://") || url.startsWith("https://")) {
				view.loadUrl(url);
				return true;
			} else if (url != null
					&& (url.contains("vguard")
							|| url.contains("droidxantivirus")
							|| url.contains("smhyundaiansimclick://")
							|| url.contains("smshinhanansimclick://")
							|| url.contains("smshinhancardusim://")
							|| url.contains("smartwall://")
							|| url.contains("appfree://")
							|| url.contains("v3mobile")
							|| url.endsWith(".apk")
							|| url.contains("market://")
							|| url.contains("ansimclick")
							|| url.contains("market://details?id=com.shcard.smartpay") || url
								.contains("shinhan-sr-ansimclick://"))) {
				return callApp(url);
			} else if (url.startsWith("smartxpay-transfer://")) {
				boolean isatallFlag = isPackageInstalled(
						getApplicationContext(), "kr.co.uplus.ecredit");
				if (isatallFlag) {
					boolean override = false;
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							getPackageName());

					try {
						startActivity(intent);
						override = true;
					} catch (ActivityNotFoundException ex) {
					}
					return override;
				} else {
					showAlert("확인버튼을 누르시면 구글플레이로 이동합니다.", "확인",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Intent.ACTION_VIEW,
											Uri.parse(("market://details?id=kr.co.uplus.ecredit")));
									intent.addCategory(Intent.CATEGORY_BROWSABLE);
									intent.putExtra(
											Browser.EXTRA_APPLICATION_ID,
											getPackageName());
									startActivity(intent);
									overridePendingTransition(0, 0);
								}
							}, "취소", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					return true;
				}
			} else if (url.startsWith("ispmobile://")) {
				boolean isatallFlag = isPackageInstalled(
						getApplicationContext(), "kvp.jjy.MispAndroid320");
				if (isatallFlag) {
					boolean override = false;
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							getPackageName());

					try {
						startActivity(intent);
						override = true;
					} catch (ActivityNotFoundException ex) {
					}
					return override;
				} else {
					showAlert("확인버튼을 누르시면 구글플레이로 이동합니다.", "확인",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									view.loadUrl("http://mobile.vpay.co.kr/jsp/MISP/andown.jsp");
								}
							}, "취소", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					return true;
				}
			} else if (url.startsWith("paypin://")) {
				boolean isatallFlag = isPackageInstalled(
						getApplicationContext(), "com.skp.android.paypin");
				if (isatallFlag) {
					boolean override = false;
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							getPackageName());

					try {
						startActivity(intent);
						override = true;
					} catch (ActivityNotFoundException ex) {
					}
					return override;
				} else {
					Intent intent = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse(("market://details?id=com.skp.android.paypin&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5za3AuYW5kcm9pZC5wYXlwaW4iXQ..")));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							getPackageName());
					startActivity(intent);
					overridePendingTransition(0, 0);
					return true;
				}
			} else if (url.startsWith("lguthepay://")) {
				boolean isatallFlag = isPackageInstalled(
						getApplicationContext(), "com.lguplus.paynow");
				if (isatallFlag) {
					boolean override = false;
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							getPackageName());

					try {
						startActivity(intent);
						override = true;
					} catch (ActivityNotFoundException ex) {
					}
					return override;
				} else {
					Intent intent = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse(("market://details?id=com.lguplus.paynow")));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							getPackageName());
					startActivity(intent);
					overridePendingTransition(0, 0);
					return true;
				}
			} else {
				return callApp(url);
			}

		}

		//
		public boolean callApp(String url) {
			Log.d("hcsong", "callApp():" + url);

			Intent intent = null;
			try {
				intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
				Log.e("intent getScheme     +++===>", intent.getScheme());
				Log.e("intent getDataString +++===>", intent.getDataString());
			} catch (URISyntaxException ex) {
				Log.e("Browser", "Bad URI " + url + ":" + ex.getMessage());
				return false;
			}
			try {
				boolean retval = true;
				//
				if (url.startsWith("intent")) { //
					//
					if (getPackageManager().resolveActivity(intent, 0) == null) {
						String packagename = intent.getPackage();
						if (packagename != null) {
							Uri uri = Uri.parse("market://search?q=pname:"
									+ packagename);
							intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
							retval = true;
						}
					} else {
						intent.addCategory(Intent.CATEGORY_BROWSABLE);
						intent.setComponent(null);
						try {
							if (startActivityIfNeeded(intent, -1)) {
								retval = true;
							}
						} catch (ActivityNotFoundException ex) {
							retval = false;
						}
					}
				} else { //
					Uri uri = Uri.parse(url);
					intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
					retval = true;
				}
				return retval;
			} catch (ActivityNotFoundException e) {
				Log.e("error ===>", e.getMessage());
				e.printStackTrace();
				return false;
			}
		}

		// DownloadFileTask?
		private void downloadFile(String mUrl) {
			new DownloadFileTask().execute(mUrl);
		}

		// AsyncTask<Params,Progress,Result>
		private class DownloadFileTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... urls) {
				URL myFileUrl = null;
				try {
					myFileUrl = new URL(urls[0]);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				try {
					HttpURLConnection conn = (HttpURLConnection) myFileUrl
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					//
					// sdcard?uses-permission??
					// android.permission.WRITE_EXTERNAL_STORAGE?
					String mPath = "sdcard/v3mobile.apk";
					FileOutputStream fos;
					File f = new File(mPath);
					if (f.createNewFile()) {
						fos = new FileOutputStream(mPath);
						int read;
						while ((read = is.read()) != -1) {
							fos.write(read);
						}
						fos.close();
					}

					return "v3mobile.apk";
				} catch (IOException e) {
					e.printStackTrace();
					return "";
				}
			}

			@Override
			protected void onPostExecute(String filename) {
				if (!"".equals(filename)) {
					Toast.makeText(getApplicationContext(),
							"download complete", Toast.LENGTH_SHORT).show();

					File apkFile = new File(
							Environment.getExternalStorageDirectory() + "/"
									+ filename);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(apkFile),
							"application/vnd.android.package-archive");
					startActivity(intent);
				}
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			HideCustomDiaglog();
			if (!failingUrl.contains("facebook")) {
				// Toast.makeText(MainActivity.this,
				// "Description: " + description + "URL:" + failingUrl,
				// Toast.LENGTH_LONG).show();
			}
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// Log.e("get link", "" + view.getUrl());
			HideCustomDiaglog();
			Log.d("hcsong", "end");
		}

	}

	public static boolean isPackageInstalled(Context ctx, String pkgName) {
		try {
			ctx.getPackageManager().getPackageInfo(pkgName,
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void showAlert(String message, String positiveButton,
			DialogInterface.OnClickListener positiveListener,
			String negativeButton,
			DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(message);
		alert.setPositiveButton(positiveButton, positiveListener);
		alert.setNegativeButton(negativeButton, negativeListener);
		alert.show();
	}
}
