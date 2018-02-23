package com.mbabo.android;

import static com.mbabo.android.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.mbabo.android.CommonUtilities.EXTRA_MESSAGE;
import static com.mbabo.android.CommonUtilities.SENDER_ID;
import static com.mbabo.android.CommonUtilities.SERVER_URL;

import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.mbabo.android.R;
import com.mbabo.android.apis.UserApis;
import com.mbabo.android.global.Apis;
import com.mbabo.android.global.Constants;
import com.mbabo.android.global.Functions;
import com.mbabo.android.global.Variables;
import com.mbabo.android.util.PreferencesHelper;

public class SplashActivity extends Activity {
	int slashDelay = 1000;
	WebView webview;
	static String UserEmail;
	static String PhoneNumber = "";
	static String Packagename = "";
	public static String UID = "";
	WebSettings websetting;
	static String androidId;
	static String PhoneModel;
	static String AndroidVersion;
	public static final String PROPERTY_REG_ID = "registration_id";
	AsyncTask<Void, Void, Void> mRegisterTask;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;
	String regid;
	TextView mDisplay;

	private final int LOGIN_SUCCESS = 2;
	private final int LOGOUT_SUCCESS = 3;
	private String LOGIN = "http://mbabo.com/login/servLogin.php?";

	Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			// case LOGOUT_SUCCESS:
			// SettingActivity.btnlogin
			// .setText(getString(R.string.setting_login));
			// break;
			// case SettingActivity.LOGIN_SUCCESS:
			// SettingActivity.btnlogin.setText(getApplicationContext()
			// .getString(R.string.setting_logout));
			// break;
			// case SHOW_PROG_DIALOG:
			//
			// break;
			//
			// case HIDE_PROG_DIALOG:
			//
			// break;
			default:
				break;
			}

			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadLanguage();
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		setContentView(R.layout.splash);
		Packagename = getPackageName();
		initView();
//		ClearCookie();
		automatic();
		androidId = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ANDROID_ID);
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);	
		

	}
	public void ClearCookie() {
		// CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	public void initWebDebug() {
		Log.d("hcsong", "initWebDebug()");
		mDisplay = (TextView) findViewById(R.id.textView1);
		androidId = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ANDROID_ID);
		PhoneModel = Build.MODEL;
		// Android version
		AndroidVersion = Build.VERSION.RELEASE;
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, Constants.GCM_SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				// mDisplay.append(getString(R.string.already_registered) +
				// "\n");
				registerGCM(regId);
				registerServerGCM(regId);
			} else {
				registerGCM(regId);
				registerServerGCM(regId);
			}
		}

	}

	debugRegister mdebugRegister;

	private void registerGCM(String regId) {
		if (mdebugRegister == null
				|| mdebugRegister.getStatus() != AsyncTask.Status.RUNNING) {
			mdebugRegister = new debugRegister();
			mdebugRegister.execute(new String[] { regId });
		}
	}

	class debugRegister extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			ServerUtilitiesDebug.register(context, params[0], androidId,
					PhoneModel, AndroidVersion);
			/*
			 * UserApis.serverMem(UserEmail, PhoneNumber, params[0],
			 * Packagename);
			 */

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	ServerRegister serverRegister;

	private void registerServerGCM(String regId) {
		if (serverRegister == null
				|| serverRegister.getStatus() != AsyncTask.Status.RUNNING) {
			serverRegister = new ServerRegister();
			serverRegister.execute(new String[] { regId });
		}
	}

	class ServerRegister extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			ServerUtilitiesSerIntro.register(context, UID, UserEmail,
					PhoneNumber, params[0], androidId);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	public void initWebSerIntro() {
		Log.d("hcsong", "initWebSerIntro()");
		mDisplay = (TextView) findViewById(R.id.textView1);

		PhoneModel = Build.MODEL;
		// Android version
		AndroidVersion = Build.VERSION.RELEASE;
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, Constants.GCM_SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				// mDisplay.append(getString(R.string.already_registered) +
				// "\n");
				Log.d("hcsong", "isRegisteredOnServer()");

				registerServerGCM(regId);

			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilitiesSerIntro.register(
								context, UID, UserEmail, PhoneNumber, regId,
								androidId);
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.

						// TEST
						if (!registered) {
							GCMRegistrar.unregister(context);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}

	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	public void initView() {
		if (!Functions.checkNetwork(getApplicationContext()))
			finish();
		webview = (WebView) findViewById(R.id.webView2);
		PhoneNumber = getMyPhoneNumber();
		webview.setWebViewClient(new webViewClient());
		websetting = webview.getSettings();
		websetting.setJavaScriptEnabled(true);
		websetting.setJavaScriptCanOpenWindowsAutomatically(true);
		websetting.setDomStorageEnabled(true);
		websetting.setAppCacheEnabled(true);
		websetting.setPluginState(PluginState.ON);

		UserEmail = Functions.UserEmailFetcher
				.getEmail(getApplicationContext());
		webview.setWebViewClient(new webViewClient());

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			webview.addJavascriptInterface(new MyJavaScriptInterface(),
					"HTMLOUT");
		}

		webview.loadUrl(Apis.BABO_API_CHECK_USER);

	}

	class MyJavaScriptInterface {

		@JavascriptInterface
		public void processHTML(String strMem) {
			String msg = "";

			Log.v("QUANG LOG RESPONSE: ", strMem);

			try {
				JSONObject jsonMem = new JSONObject(strMem);
				msg = jsonMem.getString("msg");
			} catch (JSONException e) {
			}
			if (!msg.equals("fail") && !msg.equals("")) {
				UID = msg;
			}
			initWebDebug();
			finish();

		}
	}

	public String getMyPhoneNumber() {
		try {
			if (getSystemService(TELEPHONY_SERVICE) != null) {
				String mobile = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
						.getLine1Number();
				if (mobile.equals(""))
					mobile = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
							.getSimSerialNumber();
				return mobile;
			}
		} catch (Exception ex) {
		}
		return "";
	}

	public class webViewClient extends WebViewClient {

		@SuppressLint("NewApi")
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('body')[0].innerHTML);");
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				Log.d("hcsong", "< Kitkat");
				webview.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('body')[0].innerHTML);");
			} else {
				Log.d("hcsogn", "Kitkat >");

				webview.evaluateJavascript(
						"(function() { return HTMLOUT.processHTML(document.getElementsByTagName('body')[0].innerHTML); })();",
						new ValueCallback<String>() {
							@TargetApi(Build.VERSION_CODES.HONEYCOMB)
							@Override
							public void onReceiveValue(String s) {
								String msg = "";

								try {
									JSONObject jsonMem = new JSONObject(s);
									msg = jsonMem.getString("msg");
								} catch (JSONException e) {
								}
								if (!msg.equals("fail") && !msg.equals("")) {
									UID = msg;
								}
								initWebDebug();
								finish();
							}
						});
			}
			view.clearCache(true);
		}
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}

	@Override
	protected void onDestroy() {
		try {
			if (mRegisterTask != null)
				mRegisterTask.cancel(true);

			if (mHandleMessageReceiver != null)
				unregisterReceiver(mHandleMessageReceiver);
		} catch (Exception ex) {
			Log.e("TaiError", ex.toString());
		}
		super.onDestroy();
	}

	private void loadLanguage() {
		new PreferencesHelper(SplashActivity.this).getPreference();
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			mDisplay.append(newMessage + "\n");
		}
	};
	Dialog custom;
	EditText account;
	EditText password;

	public void automatic() {
		if (custom == null) {
			if (!MainActivity.Lgin) {
				custom = new Dialog(SplashActivity.this);
				custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
				custom.setContentView(R.layout.login1);
				account = (EditText) custom.findViewById(R.id.btnacc);
				password = (EditText) custom.findViewById(R.id.btpass);				
				custom.setTitle("Custom Dialog");
				custom.show();
				SharedPreferences pre = getSharedPreferences(
						Constants.PREFS_NAME, MODE_PRIVATE);
				boolean bchk = pre.getBoolean("checked", false);
				if (bchk) {
					String user = pre.getString("user", "");
					String pwd = pre.getString("pwd", "");
					account.setText(user);
					password.setText(pwd);
					loginAction();
					custom.dismiss();
				} else
					custom.dismiss();
			}
		}
	}

	private boolean checkBlankField(String username, String password) {
		if (username.equals("") || password.equals(""))
			return false;

		return true;
	}

	AsynLogin asynLogin;
	String acc = "";
	String pass = "";

	private void loginAction() {
		acc = account.getText().toString();
		pass = password.getText().toString();
		boolean isBlank = checkBlankField(acc, pass);

		if (!isBlank) {
			Functions.showToastMessage(getString(R.string.login_blank_field),
					SplashActivity.this);
		} else {
			if (asynLogin != null
					&& asynLogin.getStatus() != AsyncTask.Status.FINISHED)
				asynLogin.cancel(true);
			asynLogin = new AsynLogin();
			asynLogin.execute(new String[] { acc, pass });
		}
	}

	String result = "";
	boolean flag = true;

	class AsynLogin extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			UserApis ua = new UserApis();
			result = ua.login(acc, pass, Variables.gGCM_regId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// mHandler.sendEmptyMessage(HIDE_PROG_DIALOG);

			Log.d("hcsong", "login result:" + result.toString());

			if (!result.equals("fail")) {
				mHandler.sendEmptyMessage(LOGIN_SUCCESS);
				MainActivity.Lgin = true;
				String login = LOGIN + "id" + "=" + URLEncoder.encode(acc)
						+ "&" + "pass" + "=" + URLEncoder.encode(pass);

				Log.d("hcsong", login);

				sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION)
						.putExtra("login", login));

//				Functions.showToastMessage(getString(R.string.login_success),
//						SplashActivity.this);
			} else {

			}
		}
	}

}
