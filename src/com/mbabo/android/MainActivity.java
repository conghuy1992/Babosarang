package com.mbabo.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mbabo.android.apis.UserApis;
import com.mbabo.android.global.Apis;
import com.mbabo.android.global.Constants;
import com.mbabo.android.global.Functions;
import com.mbabo.android.global.Variables;
import com.mbabo.android.util.PreferencesHelper;
import com.nineoldandroids.animation.ObjectAnimator;

public class MainActivity extends Activity implements OnClickListener {
	WebSettings websetting;
	WebView webview;
	LinearLayout ln_bottom;
	ImageView under_footer_tv_home;
	ImageView under_footer_tv_back;
	ImageView under_footer_tv_forward;
	ImageView under_footer_tv_setting;
	ImageView under_footer_tv_top;
	String URLCurrent = "";
	String URLDefault = "";
	String URLpkgVGuard = "";
	String URLpkgMobileISP = "";
	String URLpkgHyundai = "";
	String URLpkgShinhan = "";
	EditText account, password;
	String acc = "";
	String pass = "";
	Button login;
	Button btnRegister;
	TextView tv_forgot;
	Button btnFindPass;
	Dialog custom, dialogLogin;
	Dialog dialogCon, DialogSetting;
	private String CSCENTER = "http://www.mbabo.com/cscenter/index.php";
	private String URLPrevious = "http://mbabo.com/";
	private final int SHOW_PROG_DIALOG = 0;
	private final int HIDE_PROG_DIALOG = 1;
	private final int LOGIN_SUCCESS = 2;
	private final int LOGOUT_SUCCESS = 3;
	private PreferencesHelper mHelper;
	private ValueCallback<Uri> mUploadMessage;
	ValueCallback<Uri[]> mFilePathCallback5;
	private final static int FILECHOOSER_RESULTCODE = 1;
	String SaveID = "";
	String SavePass = "";

	public static boolean Lgin = false;
	public static boolean onProgressing = false;
	public static boolean isFinish = false;
	public static boolean isGoBack = false;
	public static boolean isFirstCheckDone = false;
	public static final String ADD_POINT_ACTION = "addPoint";
	private String LOGIN = "http://mbabo.com/login/servLogin.php?";
	private String FINDPASS = "http://mbabo.com/member_new/m.find.id.php";
	private String FINDPASS2 = "http://mbabo.com/member_new/m.find.pw.php";
	private String REGISTER = "http://mbabo.com/member_new/join.php";
	private String LOGOUT = "http://www.mbabo.com/login/logout_process.php";
	private String NON_MEMBER = "http://mbabo.com/login/index.php?tab=nomem";
	String urlCHPLay = "https://play.google.com/store/apps/details?id=com.mbabo.android";

	TextView tvVersionNew;
	private Activity context = this;
	Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case LOGOUT_SUCCESS:
				webview.loadUrl(LOGOUT);
				break;
			case LOGIN_SUCCESS:
				webview.loadUrl(logintext);
				break;
			case SHOW_PROG_DIALOG:
				break;

			case HIDE_PROG_DIALOG:
				HideCustomDiaglog();
				break;
			default:
				break;
			}
			return false;
		}
	});

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		IntentFilter filter = new IntentFilter(ADD_POINT_ACTION);
		registerReceiver(pointReceiver, filter);
		initial();
		new _GetVersionApp().execute();
		// ClearCookie();
		// automatic();
	}

	Dialog customAutologin, customAutologin2;

	public void automatic() {
		if (customAutologin == null) {
			if (!Lgin) {
				customAutologin = new Dialog(MainActivity.this);
				customAutologin.requestWindowFeature(Window.FEATURE_NO_TITLE);
				customAutologin.setContentView(R.layout.login1);
				account = (EditText) customAutologin.findViewById(R.id.btnacc);
				password = (EditText) customAutologin.findViewById(R.id.btpass);
				customAutologin.show();
				SharedPreferences pre = getSharedPreferences(
						Constants.PREFS_NAME, MODE_PRIVATE);
				boolean bchk = pre.getBoolean("checked", false);
				if (bchk) {
					String user = pre.getString("user", "");
					String pwd = pre.getString("pwd", "");
					// Log.e("asasasa", "" + user + pwd);
					account.setText(user);
					password.setText(pwd);
					loginActionAuto();
					customAutologin.dismiss();
				} else
					customAutologin.dismiss();
			}
		}
	}

	public void automatic2() {
		if (customAutologin == null) {
			if (Lgin) {
				customAutologin = new Dialog(MainActivity.this);
				customAutologin.requestWindowFeature(Window.FEATURE_NO_TITLE);
				customAutologin.setContentView(R.layout.login1);
				account = (EditText) customAutologin.findViewById(R.id.btnacc);
				password = (EditText) customAutologin.findViewById(R.id.btpass);
				customAutologin.show();
				SharedPreferences pre = getSharedPreferences(
						Constants.PREFS_NAME, MODE_PRIVATE);
				boolean bchk = pre.getBoolean("checked2", false);
				if (bchk) {
					String user = pre.getString("user", "");
					String pwd = pre.getString("pwd", "");
					SaveID = user;
					SavePass = pwd;
					// Log.e("asasasa", "" + user + pwd);
					account.setText(user);
					password.setText(pwd);
					loginActionAuto();
					customAutologin.dismiss();
				} else
					customAutologin.dismiss();
			}
		}
	}

	AsynLoginAuto asynLoginAuto;

	class AsynLoginAuto extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			UserApis ua = new UserApis();
			result = ua.login(acc, pass, Variables.gGCM_regId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
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

				// Functions.showToastMessage(getString(R.string.login_success),
				// MainActivity.this);
			} else {

			}
		}
	}

	private void loginActionAuto() {
		acc = account.getText().toString();
		pass = password.getText().toString();
		boolean isBlank = checkBlankField(acc, pass);

		if (!isBlank) {
			// Functions.showToastMessage(getString(R.string.login_blank_field),
			// MainActivity.this);
		} else {
			if (asynLoginAuto != null
					&& asynLoginAuto.getStatus() != AsyncTask.Status.FINISHED)
				asynLoginAuto.cancel(true);
			asynLoginAuto = new AsynLoginAuto();
			asynLoginAuto.execute(new String[] { acc, pass });
		}
	}

	public void ClearCookie() {
		// CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String url = intent.getStringExtra(Constants.KEY_LINK_NOTI);
		if (url != null) {
			webview.loadUrl(url);
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (!Lgin)
			menu.findItem(R.id.item_login).setTitle(
					getString(R.string.setting_login));
		else {
			menu.findItem(R.id.item_login).setTitle(
					getString(R.string.setting_logout));
		}
		return super.onPrepareOptionsMenu(menu);
	}

	Menu mnu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.startup, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString()
				.equals(getString(R.string.setting_title))) {
			if (onProgressing)
				Functions.showToastMessage(
						getString(R.string.dialog_msg_processing),
						getApplicationContext());
			else {
				// Intent intent = new Intent(getBaseContext(),
				// SettingActivity.class);
				// intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				// intent.putExtra("url_previous", URLCurrent);
				// startActivity(intent);
				DialogSetting = new Dialog(MainActivity.this);
				DialogSetting.requestWindowFeature(Window.FEATURE_NO_TITLE);
				DialogSetting.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));
				DialogSetting.setCanceledOnTouchOutside(true);
				DialogSetting.setContentView(R.layout.settings);
				ToggleButton tgbNotify = (ToggleButton) DialogSetting
						.findViewById(R.id.setting_tgb_notification);
				final ToggleButton chkVibrate = (ToggleButton) DialogSetting
						.findViewById(R.id.setting_chk_vibrate);
				final ToggleButton chkSound = (ToggleButton) DialogSetting
						.findViewById(R.id.setting_chk_sound);
				final ToggleButton chkLed = (ToggleButton) DialogSetting
						.findViewById(R.id.setting_chk_led);
				TextView tvVersion = (TextView) DialogSetting
						.findViewById(R.id.setting_tv_version);
				tvVersionNew = (TextView) DialogSetting
						.findViewById(R.id.setting_new_version);
				new _GetVersionInDialog().execute();
				try {
					String versionName = getPackageManager().getPackageInfo(
							getPackageName(), 0).versionName;
					float _cur = Float.parseFloat(versionName);
					String version = getResources().getString(R.string.ver_cur)
							+ ": " + _cur;
					tvVersion.setText(version);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				mHelper = new PreferencesHelper(getApplicationContext());
				mHelper.getPreference();

				tgbNotify.setChecked(Variables.gNotification);
				chkVibrate.setChecked(Variables.gVibrate);
				chkSound.setChecked(Variables.gSound);
				chkLed.setChecked(Variables.gLed);
				tgbNotify
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								mHelper.setNotification(isChecked);
								if (isChecked) {
									chkVibrate.setChecked(true);
									chkLed.setChecked(true);
									chkSound.setChecked(true);
								} else {
									chkVibrate.setChecked(false);
									chkLed.setChecked(false);
									chkSound.setChecked(false);
								}
							}
						});
				chkSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mHelper.setSound(isChecked);
					}
				});
				chkLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mHelper.setLed(isChecked);

					}
				});
				chkVibrate
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								mHelper.setVibriation(isChecked);
							}
						});
				Button btnlogin = (Button) DialogSetting
						.findViewById(R.id.btn_login);
				if (!MainActivity.Lgin)
					btnlogin.setText(R.string.setting_login);
				else
					btnlogin.setText(R.string.setting_logout);
				btnlogin.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (Lgin == true) {
							logoutAction();
							if (DialogSetting.isShowing()) {
								DialogSetting.dismiss();
							}
						} else {
							custom = new Dialog(MainActivity.this);
							custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
							custom.setContentView(R.layout.login1);
							account = (EditText) custom
									.findViewById(R.id.btnacc);
							password = (EditText) custom
									.findViewById(R.id.btpass);
							account.setOnFocusChangeListener(new View.OnFocusChangeListener() {

								@Override
								public void onFocusChange(View v,
										boolean hasFocus) {
									if (hasFocus) {
										account.setHint("");
									} else {
										account.setHint(getString(R.string.login_username));
									}

								}
							});
							password.setOnFocusChangeListener(new View.OnFocusChangeListener() {

								@Override
								public void onFocusChange(View v,
										boolean hasFocus) {
									if (hasFocus) {
										password.setHint("");
									} else {
										password.setHint(getString(R.string.login_password));
									}
								}
							});

							chexLogin = (CheckBox) custom
									.findViewById(R.id.checkLogin);
							chexLogin.setChecked(Variables.checkRemember);
							chexLogin
									.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

										@Override
										public void onCheckedChanged(
												CompoundButton buttonView,
												boolean isChecked) {
											mHelper.setCheckRemember(isChecked);
										}
									});
							//
							chexSaveID = (CheckBox) custom
									.findViewById(R.id.checksaveid);
							chexSaveID.setChecked(Variables.checkSaveID);
							chexSaveID
									.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

										@Override
										public void onCheckedChanged(
												CompoundButton buttonView,
												boolean isChecked) {
											mHelper.setCheckSaveID(isChecked);
										}
									});
							//
							chexSavePass = (CheckBox) custom
									.findViewById(R.id.checksavepass);
							chexSavePass.setChecked(Variables.checkSavePass);
							chexSavePass
									.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

										@Override
										public void onCheckedChanged(
												CompoundButton buttonView,
												boolean isChecked) {
											mHelper.setCheckSavePass(isChecked);
										}
									});
							//
							login = (Button) custom.findViewById(R.id.btnLogin);
							non_member = (Button) custom
									.findViewById(R.id.non_member);
							non_member.setOnClickListener(MainActivity.this);
							btnRegister = (Button) custom
									.findViewById(R.id.register_member);
							tv_forgot = (TextView) custom
									.findViewById(R.id.tv_forgot);
							tv_forgot.setOnClickListener(MainActivity.this);

							btnFindPass = (Button) custom
									.findViewById(R.id.btn3);
							btnFindPass.setOnClickListener(MainActivity.this);

							btnRegister.setOnClickListener(MainActivity.this);
							custom.setTitle("Custom Dialog");
							restoringPreferences();

							login.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View view) {
									SaveID = account.getText().toString();
									SavePass = password.getText().toString();
									loginAction3();
								}
							});
							custom.show();
							custom.setOnCancelListener(new OnCancelListener() {

								@Override
								public void onCancel(DialogInterface dialog) {
									custom = null;
								}
							});
							custom.setOnDismissListener(new OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
									custom = null;
								}
							});
						}

					}
				});
				Button btncscenter = (Button) DialogSetting
						.findViewById(R.id.btn_cscenter);
				btncscenter.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION)
								.putExtra("login", CSCENTER));
						if (DialogSetting.isShowing()) {
							DialogSetting.dismiss();
						}

					}
				});

				Intent intent = getIntent();
				Bundle b = intent.getExtras();
				if (b != null) {
					URLPrevious = (String) b.get("url_previous");
				}

				DialogSetting.show();

			}
			return true;
		}
		if (item.getTitle().toString().equals(getString(R.string.refresh))) {
			webview.reload();
			return true;
		}
		if (item.getTitle().toString()
				.equals(getString(R.string.setting_login))) {
			webview.loadUrl("http://www.mbabo.com/mypage/");
			return true;
		}
		if (item.getTitle().toString()
				.equals(getString(R.string.setting_logout))) {
			logoutAction();

			return true;
		}
		return true;
	}

	private final BroadcastReceiver pointReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ADD_POINT_ACTION)) {
				String adId = intent.getExtras().getString("login");
				Log.d("hcsong", "webview.loadurl(" + adId + ")");

				webview.loadUrl(adId);
			}
		}
	};

	public class webViewChromeClient extends WebChromeClient {

		// For Android < 3.0
		@SuppressWarnings("unused")
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			openFileChooser(uploadMsg, "");
		}

		// For Android 3.0+
		@SuppressWarnings("unused")
		public void openFileChooser(ValueCallback<Uri> uploadMsg,
				String acceptType) {
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			startActivityForResult(Intent.createChooser(i, "File Chooser"),
					MainActivity.FILECHOOSER_RESULTCODE);
		}

		// For Android 4.1+
		@SuppressWarnings("unused")
		public void openFileChooser(ValueCallback<Uri> uploadMsg,
				String acceptType, String capture) {
			// openFileChooser(uploadMsg, "");
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			startActivityForResult(Intent.createChooser(i, "File Chooser"),
					MainActivity.FILECHOOSER_RESULTCODE);
		}

		// file upload callback (Android 5.0 (API level 21) -- current) (public
		// method)
		@SuppressWarnings("all")
		public boolean onShowFileChooser(WebView webView,
				ValueCallback<Uri[]> filePathCallback,
				WebChromeClient.FileChooserParams fileChooserParams) {
			mFilePathCallback5 = filePathCallback;
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("*/*");
			startActivityForResult(
					Intent.createChooser(intent, "File Chooser"),
					MainActivity.FILECHOOSER_RESULTCODE);
			return true;

		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			if (message.equals("로그인")) {
				Lgin = false;
				result.confirm();
			} else if (message.equals("로그아웃")) {
				Lgin = true;
				result.confirm();
			} else if (message.contains("백신이 정상 동작 하지 않았습니다.")) {
				result.confirm();
			} else if (!message.equals("null")) {
				result.confirm();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
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

			new AlertDialog.Builder(MainActivity.this)
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

	boolean loadbottom = false;
	Button non_member;
	CheckBox chexLogin, chexSaveID, chexSavePass;

	public class webViewClient extends WebViewClient implements
			OnCheckedChangeListener {

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

			// if (url.equals("http://www.mbabo.com/event/")
			// || url.startsWith("http://mbabo.com/event/")) {
			// view.stopLoading();
			// Intent i_event = new Intent(MainActivity.this, WebView2.class);
			// i_event.putExtra("event_link", "http://mbabo.com/event/");
			// i_event.putExtra("event_name", "EVENT");
			// startActivity(i_event);
			// }

			// if (url.equals("http://www.mbabo.com/cart/order_form.v1.php")
			// || url.startsWith("http://www.mbabo.com/cart/order_form.v1.php"))
			// {
			// // Toast.makeText(getApplicationContext(), "PAY",
			// // Toast.LENGTH_SHORT).show();
			// // Intent intent = new Intent(getBaseContext(), WebView2.class);
			// // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// // intent.putExtra("url_previous", URLCurrent);
			// // startActivity(intent);
			// // view.stopLoading();
			// }

			if (custom == null) {
				if (url.equals("http://www.mbabo.com/mypage/") && !Lgin
						|| url.startsWith("http://mbabo.com/mypage/") && !Lgin) {
					custom = new Dialog(MainActivity.this);
					custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
					custom.setContentView(R.layout.login1);
					account = (EditText) custom.findViewById(R.id.btnacc);
					password = (EditText) custom.findViewById(R.id.btpass);
					account.setOnFocusChangeListener(new View.OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								account.setHint("");
							} else {
								account.setHint(getString(R.string.login_username));
							}

						}
					});
					password.setOnFocusChangeListener(new View.OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								password.setHint("");
							} else {
								password.setHint(getString(R.string.login_password));
							}
						}
					});
					chexLogin = (CheckBox) custom.findViewById(R.id.checkLogin);
					chexLogin.setChecked(Variables.checkRemember);
					chexLogin.setOnCheckedChangeListener(this);
					//
					chexSaveID = (CheckBox) custom
							.findViewById(R.id.checksaveid);
					chexSaveID.setChecked(Variables.checkSaveID);
					chexSaveID.setOnCheckedChangeListener(this);
					//
					chexSavePass = (CheckBox) custom
							.findViewById(R.id.checksavepass);
					chexSavePass.setChecked(Variables.checkSavePass);
					chexSavePass.setOnCheckedChangeListener(this);

					login = (Button) custom.findViewById(R.id.btnLogin);
					non_member = (Button) custom.findViewById(R.id.non_member);
					non_member.setOnClickListener(MainActivity.this);
					btnRegister = (Button) custom
							.findViewById(R.id.register_member);
					tv_forgot = (TextView) custom.findViewById(R.id.tv_forgot);
					tv_forgot.setOnClickListener(MainActivity.this);

					btnFindPass = (Button) custom.findViewById(R.id.btn3);
					btnFindPass.setOnClickListener(MainActivity.this);

					btnRegister.setOnClickListener(MainActivity.this);
					custom.setTitle("Custom Dialog");

					restoringPreferences();

					login.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							SaveID = account.getText().toString();
							SavePass = password.getText().toString();
							loginAction();
						}

					});
					custom.show();
					custom.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							custom = null;
						}
					});
					custom.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							custom = null;
						}
					});
					view.stopLoading();
				}
			}

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			onProgressing = true;

			ShowCustomDialog();
			if (url.contains("mbabo://window('CART','http://www.mbabo.com/cart/')")
					|| url.contains("mbabo://window('ORDER','http://www.mbabo.com/cart/order_form.v1.php')")) {
				HideCustomDiaglog();
			}
			if (url.contains(Apis.URLIntro) || url.contains(Apis.URLPopup))
				ln_bottom.setVisibility(LinearLayout.GONE);
			else
				loadbottom = true;

			if (isGoBack && url.contains("cart/index.php")) {
				webview.goBack();
			}
			isFinish = false;
			isGoBack = false;
			URLCurrent = url;
			if (!Functions.checkNetwork(getApplicationContext()))
				finish();
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) {
			Log.d("get URL", "" + url);

			if (url.startsWith("mbabo://order")) {
				view.stopLoading();
				String _VALUE = url.substring(13, url.length());
				Log.e("value", "" + _VALUE);
				String ORDER_LINK = "http://www.mbabo.com/cart/order_form.v1.php"
						+ _VALUE;
				Log.e("order link", "" + ORDER_LINK);
				Intent _intent = new Intent(MainActivity.this, WebView3.class);
				_intent.putExtra("ORDER", ORDER_LINK);
				_intent.putExtra("SEND_NAME",
						getResources().getString(R.string.o_der));
				startActivity(_intent);

			}
			//
			if (url.startsWith("mbabo://cart")) {
				view.stopLoading();
				String _VALUE = url.substring(12, url.length());
				String CART_LINK = "http://www.mbabo.com/cart/"
						+ _VALUE;
				Intent _intent = new Intent(MainActivity.this, WebView2.class);
				_intent.putExtra("SHOPPING_CART", CART_LINK);
				_intent.putExtra("SEND_NAME",
						getResources().getString(R.string.shopping_cart));
				startActivity(_intent);
			}

			if (!Functions.checkNetwork(getApplicationContext()))
				finish();

			if (url.equals("mbabo://usersetup")) {
				view.stopLoading();
				// Intent intent = new Intent(MainActivity.this,
				// SettingActivity.class);
				// startActivity(intent);
				dialogCon = new Dialog(MainActivity.this);
				dialogCon.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogCon.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialogCon.setCanceledOnTouchOutside(true);
				dialogCon.setContentView(R.layout.settings);
				ToggleButton tgbNotify = (ToggleButton) dialogCon
						.findViewById(R.id.setting_tgb_notification);
				final ToggleButton chkVibrate = (ToggleButton) dialogCon
						.findViewById(R.id.setting_chk_vibrate);
				final ToggleButton chkSound = (ToggleButton) dialogCon
						.findViewById(R.id.setting_chk_sound);
				final ToggleButton chkLed = (ToggleButton) dialogCon
						.findViewById(R.id.setting_chk_led);
				TextView tvVersion = (TextView) dialogCon
						.findViewById(R.id.setting_tv_version);
				tvVersionNew = (TextView) dialogCon
						.findViewById(R.id.setting_new_version);
				new _GetVersionInDialog().execute();
				try {
					// PackageInfo pInfo = getPackageManager().getPackageInfo(
					// getPackageName(), 0);
					// String version = String.format(
					// getString(R.string.setting_version_text),
					// pInfo.versionName);
					// String versionName = getPackageManager().getPackageInfo(
					// getPackageName(), 0).versionName;
					String versionName = getPackageManager().getPackageInfo(
							getPackageName(), 0).versionName;
					float _cur = Float.parseFloat(versionName);
					String version = getResources().getString(R.string.ver_cur)
							+ ": " + _cur;
					tvVersion.setText(version);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				mHelper = new PreferencesHelper(getApplicationContext());
				mHelper.getPreference();

				tgbNotify.setChecked(Variables.gNotification);
				chkVibrate.setChecked(Variables.gVibrate);
				chkSound.setChecked(Variables.gSound);
				chkLed.setChecked(Variables.gLed);
				tgbNotify
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								mHelper.setNotification(isChecked);
								if (isChecked) {
									chkVibrate.setChecked(true);
									chkLed.setChecked(true);
									chkSound.setChecked(true);
								} else {
									chkVibrate.setChecked(false);
									chkLed.setChecked(false);
									chkSound.setChecked(false);
								}
							}
						});
				chkSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mHelper.setSound(isChecked);
					}
				});
				chkLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mHelper.setLed(isChecked);

					}
				});
				chkVibrate
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								mHelper.setVibriation(isChecked);
							}
						});
				Button btnlogin = (Button) dialogCon
						.findViewById(R.id.btn_login);
				if (!MainActivity.Lgin)
					btnlogin.setText(R.string.setting_login);
				else
					btnlogin.setText(R.string.setting_logout);
				btnlogin.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (Lgin == true) {
							logoutAction();
							if (dialogCon.isShowing()) {
								dialogCon.dismiss();
							}
						} else {
							custom = new Dialog(MainActivity.this);
							custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
							custom.setContentView(R.layout.login1);
							account = (EditText) custom
									.findViewById(R.id.btnacc);
							password = (EditText) custom
									.findViewById(R.id.btpass);
							account.setOnFocusChangeListener(new View.OnFocusChangeListener() {

								@Override
								public void onFocusChange(View v,
										boolean hasFocus) {
									if (hasFocus) {
										account.setHint("");
									} else {
										account.setHint(getString(R.string.login_username));
									}

								}
							});
							password.setOnFocusChangeListener(new View.OnFocusChangeListener() {

								@Override
								public void onFocusChange(View v,
										boolean hasFocus) {
									if (hasFocus) {
										password.setHint("");
									} else {
										password.setHint(getString(R.string.login_password));
									}
								}
							});

							chexLogin = (CheckBox) custom
									.findViewById(R.id.checkLogin);
							chexLogin.setChecked(Variables.checkRemember);
							chexLogin
									.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

										@Override
										public void onCheckedChanged(
												CompoundButton buttonView,
												boolean isChecked) {
											mHelper.setCheckRemember(isChecked);
										}
									});
							//
							chexSaveID = (CheckBox) custom
									.findViewById(R.id.checksaveid);
							chexSaveID.setChecked(Variables.checkSaveID);
							chexSaveID
									.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

										@Override
										public void onCheckedChanged(
												CompoundButton buttonView,
												boolean isChecked) {
											mHelper.setCheckSaveID(isChecked);
										}
									});
							//
							chexSavePass = (CheckBox) custom
									.findViewById(R.id.checksavepass);
							chexSavePass.setChecked(Variables.checkSavePass);
							chexSavePass
									.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

										@Override
										public void onCheckedChanged(
												CompoundButton buttonView,
												boolean isChecked) {
											mHelper.setCheckSavePass(isChecked);
										}
									});
							//
							login = (Button) custom.findViewById(R.id.btnLogin);
							non_member = (Button) custom
									.findViewById(R.id.non_member);
							non_member.setOnClickListener(MainActivity.this);
							btnRegister = (Button) custom
									.findViewById(R.id.register_member);
							tv_forgot = (TextView) custom
									.findViewById(R.id.tv_forgot);
							tv_forgot.setOnClickListener(MainActivity.this);

							btnFindPass = (Button) custom
									.findViewById(R.id.btn3);
							btnFindPass.setOnClickListener(MainActivity.this);

							btnRegister.setOnClickListener(MainActivity.this);
							custom.setTitle("Custom Dialog");
							restoringPreferences();

							login.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View view) {
									SaveID = account.getText().toString();
									SavePass = password.getText().toString();
									loginAction2();
								}
							});
							custom.show();
							custom.setOnCancelListener(new OnCancelListener() {

								@Override
								public void onCancel(DialogInterface dialog) {
									custom = null;
								}
							});
							custom.setOnDismissListener(new OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
									custom = null;
								}
							});
						}

					}
				});
				Button btncscenter = (Button) dialogCon
						.findViewById(R.id.btn_cscenter);
				btncscenter.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION)
								.putExtra("login", CSCENTER));
						if (dialogCon.isShowing()) {
							dialogCon.dismiss();
						}

					}
				});

				Intent intent = getIntent();
				Bundle b = intent.getExtras();
				if (b != null) {
					URLPrevious = (String) b.get("url_previous");
				}

				dialogCon.show();

			}
			if (url.equals("mbabo://logout")) {
				view.stopLoading();
				logoutAction();
			}

			if (custom == null) {
				if (url.equals("mbabo://loginpopup") && !Lgin) {
					custom = new Dialog(MainActivity.this);
					custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
					custom.setContentView(R.layout.login1);
					account = (EditText) custom.findViewById(R.id.btnacc);
					password = (EditText) custom.findViewById(R.id.btpass);
					account.setOnFocusChangeListener(new View.OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								account.setHint("");
							} else {
								account.setHint(getString(R.string.login_username));
							}

						}
					});
					password.setOnFocusChangeListener(new View.OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								password.setHint("");
							} else {
								password.setHint(getString(R.string.login_password));
							}
						}
					});

					chexLogin = (CheckBox) custom.findViewById(R.id.checkLogin);
					chexLogin.setChecked(Variables.checkRemember);
					chexLogin.setOnCheckedChangeListener(this);
					//
					chexSaveID = (CheckBox) custom
							.findViewById(R.id.checksaveid);
					chexSaveID.setChecked(Variables.checkSaveID);
					chexSaveID.setOnCheckedChangeListener(this);
					//
					chexSavePass = (CheckBox) custom
							.findViewById(R.id.checksavepass);
					chexSavePass.setChecked(Variables.checkSavePass);
					chexSavePass.setOnCheckedChangeListener(this);
					//
					login = (Button) custom.findViewById(R.id.btnLogin);
					non_member = (Button) custom.findViewById(R.id.non_member);
					non_member.setOnClickListener(MainActivity.this);
					btnRegister = (Button) custom
							.findViewById(R.id.register_member);
					tv_forgot = (TextView) custom.findViewById(R.id.tv_forgot);
					tv_forgot.setOnClickListener(MainActivity.this);

					btnFindPass = (Button) custom.findViewById(R.id.btn3);
					btnFindPass.setOnClickListener(MainActivity.this);

					btnRegister.setOnClickListener(MainActivity.this);
					custom.setTitle("Custom Dialog");
					restoringPreferences();

					login.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							SaveID = account.getText().toString();
							SavePass = password.getText().toString();
							loginAction();

						}
					});
					custom.show();
					custom.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							custom = null;
						}
					});
					custom.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							custom = null;
						}
					});
					view.stopLoading();
				}
			}

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

		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			int keyCode = event.getKeyCode();
			if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) && webview.canGoBack()) {
				webview.goBack();
				return true;
			} else if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
					&& webview.canGoForward()) {
				webview.goForward();
				return true;
			}
			return false;
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
			onProgressing = false;
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
			if (url.equals("http://mbabo.com/login/login_process.php")) {
				Lgin = true;
			}
			Log.d("hcsong", "onPageFinished");
			initBackForward();
			if (loadbottom == true) {
				// ln_bottom.setVisibility(LinearLayout.VISIBLE);
			}
			// if (Lgin == true) {
			// ln_bottom.setVisibility(LinearLayout.GONE);
			// }
			// Log.d("hcsong", "here");

			// String s = "";
			// s =
			// "javascript:$(document).ready(function(){$('#main-footer table').hide();"
			// + "alert($('#main-footer a:first').find('strong').html());});";
			// Log.d("hcsong", "url:" + s);
			//
			// webview.loadUrl("javascript:$(document).ready(function(){$('#main-footer table').hide();"
			// + "alert($('#main-footer a:first').find('strong').html());}); ");

			Log.d("hcsong", "end");
			onProgressing = false;

			// view.clearCache(true);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.checkLogin:
				mHelper.setCheckRemember(isChecked);
				break;
			case R.id.checksaveid:
				mHelper.setCheckSaveID(isChecked);
				break;
			case R.id.checksavepass:
				mHelper.setCheckSavePass(isChecked);
				break;

			default:
				break;
			}

		}

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

	private void initial() {
		if (Functions.checkNetwork(getApplicationContext())) {
			webview = (WebView) findViewById(R.id.webview1);
			ln_bottom = (LinearLayout) findViewById(R.id.ln_bottom);
			under_footer_tv_home = (ImageView) findViewById(R.id.under_footer_tv_home);
			under_footer_tv_back = (ImageView) findViewById(R.id.under_footer_tv_back);
			under_footer_tv_forward = (ImageView) findViewById(R.id.under_footer_tv_forward);
			under_footer_tv_setting = (ImageView) findViewById(R.id.under_footer_tv_settings);
			under_footer_tv_top = (ImageView) findViewById(R.id.under_footer_tv_top);

			mHelper = new PreferencesHelper(getApplicationContext());
			mHelper.getPreference();

			under_footer_tv_home.setOnClickListener(this);
			under_footer_tv_back.setOnClickListener(this);
			under_footer_tv_forward.setOnClickListener(this);
			under_footer_tv_setting.setOnClickListener(this);
			under_footer_tv_top.setOnClickListener(this);
			websetting = webview.getSettings();
			// CookieSyncManager.createInstance(this);
			// CookieManager cookieManager = CookieManager.getInstance();
			// cookieManager.removeAllCookie();
			// cookieManager.setAcceptCookie(false);
			// websetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

			websetting.setJavaScriptEnabled(true);
			websetting.setJavaScriptCanOpenWindowsAutomatically(true);
			websetting.setDomStorageEnabled(true);
			websetting.setAppCacheEnabled(true);
			websetting.setPluginState(PluginState.ON);
			websetting.setRenderPriority(RenderPriority.HIGH);
			websetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
			webview.setWebChromeClient(new webViewChromeClient());
			webview.setWebViewClient(new webViewClient());

			Intent intent = getIntent();
			Bundle b = intent.getExtras();

			if (b != null && null != b.get("login")) {
				webview.loadUrl(b.get("login").toString());

			} else {
				if (b != null && b.getString(Constants.KEY_LINK_NOTI) != null) {
					webview.loadUrl(b.getString(Constants.KEY_LINK_NOTI));
				} else {
					isFirstCheckDone = false;
					webview.loadUrl(Apis.URLIntro);
				}
			}
		} else
			finish();
	}

	private void initBackForward() {
		if (webview.isFocused() && webview.canGoBack()) {
			under_footer_tv_back
					.setImageResource(R.drawable.ic_backward_enable);
		} else
			under_footer_tv_back
					.setImageResource(R.drawable.ic_backward_disable);

		if (webview.isFocused() && webview.canGoForward()) {
			under_footer_tv_forward
					.setImageResource(R.drawable.ic_forward_enable);
		} else
			under_footer_tv_forward
					.setImageResource(R.drawable.ic_forward_disable);
	}

	public void goBackInWebView() {
		WebBackForwardList history = webview.copyBackForwardList();
		int index = -1;
		String url = null;

		while (webview.canGoBackOrForward(index)) {
			if (!history.getItemAtIndex(history.getCurrentIndex() + index)
					.getUrl().equals("about:blank")) {
				webview.goBackOrForward(index);
				url = history.getItemAtIndex(-index).getUrl();
				Log.e("tag", "first non empty" + url);
				break;
			}
			index--;

		}
		// no history found that is not empty
		if (url == null) {
			finish();
		}
	}

	@Override
	public void onBackPressed() {

		try {
			if (isFinish) {
				// Intent setIntent = new Intent(Intent.ACTION_MAIN);
				// setIntent.addCategory(Intent.CATEGORY_HOME);
				// setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(setIntent);
				finish();
			} else if (webview.getUrl().equals(Apis.URLHome)
					|| webview.getUrl().equals(Apis.URLWWWHome)
					|| webview.getUrl().contains(Apis.URLEventMobile)
					|| webview.getUrl().contains(Apis.URLPopup)) {
				Functions.showToastMessage(getString(R.string.msg_shutdown),
						getApplicationContext());
				isFinish = true;
			} else {
				if (webview.isFocused() && webview.canGoBack()) {
					isGoBack = true;
					goBackInWebView();
				} else {
					// Intent setIntent = new Intent(Intent.ACTION_MAIN);
					// setIntent.addCategory(Intent.CATEGORY_HOME);
					// setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// startActivity(setIntent);
					finish();

				}
			}
		} catch (Exception e) {
			// finish();
			e.printStackTrace();
		}
		// try {
		// if (isFinish) {
		// savingPreferencesBack();
		// finish();
		// android.os.Process.killProcess(android.os.Process.myPid());
		// } else if (webview.getUrl().equals(Apis.URLHome)
		// || webview.getUrl().equals(Apis.URLWWWHome)
		// || webview.getUrl().contains(Apis.URLEventMobile)
		// || webview.getUrl().contains(Apis.URLPopup)) {
		// Functions.showToastMessage(getString(R.string.msg_shutdown),
		// getApplicationContext());
		// isFinish = true;
		// } else {
		// if (webview.isFocused() && webview.canGoBack()) {
		// isGoBack = true;
		// goBackInWebView();
		// } else {
		// savingPreferencesBack();
		// finish();
		// android.os.Process.killProcess(android.os.Process.myPid());
		//
		// }
		// }
		// } catch (Exception e) {
		// // finish();
		// e.printStackTrace();
		// }
	}

	@Override
	protected void onResume() {

		try {
			automatic2();
		} catch (Exception e) {
			// TODO: handle exception
		}
		// loadVguard();
		// loadMobileISP();
		loadDefault();
		// loadHyundai();
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.under_footer_tv_home:
			webview.loadUrl(Apis.URLHome);
			break;
		case R.id.register_member:
			webview.loadUrl(REGISTER);
			custom.dismiss();
			try {
				if (DialogSetting.isShowing()) {
					DialogSetting.dismiss();
				}
			} catch (Exception e) {

			}
			break;
		case R.id.non_member:
			webview.loadUrl(FINDPASS);
			custom.dismiss();
			try {
				if (DialogSetting.isShowing()) {
					DialogSetting.dismiss();
				}
			} catch (Exception e) {

			}
			break;
		case R.id.btn3:
			webview.loadUrl(FINDPASS2);
			custom.dismiss();
			try {
				if (DialogSetting.isShowing()) {
					DialogSetting.dismiss();
				}
			} catch (Exception e) {

			}
			break;
		case R.id.tv_forgot:
			webview.loadUrl(NON_MEMBER);
			custom.dismiss();
			try {
				if (DialogSetting.isShowing()) {
					DialogSetting.dismiss();
				}
			} catch (Exception e) {

			}
			break;
		case R.id.under_footer_tv_back:
			if (isFinish) {
				finish();
			} else if (webview.getUrl().equals(Apis.URLHome)
					|| webview.getUrl().equals(Apis.URLWWWHome)
					|| webview.getUrl().contains(Apis.URLEventMobile)
					|| webview.getUrl().contains(Apis.URLPopup)) {
				Functions.showToastMessage(getString(R.string.msg_shutdown),
						getApplicationContext());
				isFinish = true;
			} else {
				if (webview.isFocused() && webview.canGoBack()) {
					isGoBack = true;
					goBackInWebView();
				} else {
					finish();
				}
			}
			break;
		case R.id.under_footer_tv_forward:
			if (webview.isFocused() && webview.canGoForward()) {
				webview.goForward();
			}
			break;
		case R.id.under_footer_tv_settings:
			if (onProgressing)
				Functions.showToastMessage(
						getString(R.string.dialog_msg_processing),
						getApplicationContext());
			else {
				Intent intent = new Intent(MainActivity.this,
						SettingActivity.class);
				intent.putExtra("url_previous", URLCurrent);
				startActivity(intent);
			}
			break;
		case R.id.under_footer_tv_top:
			ObjectAnimator anim = ObjectAnimator.ofInt(webview, "scrollY",
					webview.getScrollY(), 0);
			anim.setDuration(500);
			anim.start();
			break;
		default:
			break;
		}
	}

	private void loadDefault() {
		if (URLDefault.contains("mbabo")) {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(URLDefault));
			startActivity(intent);
			URLDefault = "";
		}
	}

	private void loadVguard() {
		if (URLpkgVGuard.startsWith("vguardstart:")
				&& Functions.isInstalledApp(Apis.pkgVGuard, MainActivity.this)) {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(URLpkgVGuard));
			startActivity(intent);
			URLpkgVGuard = "";
		}
	}

	private void loadMobileISP() {
		if (URLpkgMobileISP.startsWith("ispmobile:")
				&& Functions.isInstalledApp(Apis.pkgMobileISP,
						MainActivity.this)) {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(URLpkgMobileISP));
			startActivity(intent);
			URLpkgMobileISP = "";
		}
	}

	private void loadHyundai() {
		if (URLpkgHyundai.startsWith("droidxantivirusweb:")
				&& Functions.isInstalledApp(Apis.pkgHyundai, MainActivity.this)) {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(URLpkgHyundai));
			startActivity(intent);
			URLpkgHyundai = "";
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

	AsynLogin3 asynLogin3;

	private void loginAction3() {
		acc = account.getText().toString();
		pass = password.getText().toString();
		boolean isBlank = checkBlankField(acc, pass);

		if (!isBlank) {
			Functions.showToastMessage(getString(R.string.login_blank_field),
					MainActivity.this);
		} else {
			if (asynLogin3 != null
					&& asynLogin3.getStatus() != AsyncTask.Status.FINISHED)
				asynLogin3.cancel(true);
			asynLogin3 = new AsynLogin3();
			asynLogin3.execute(new String[] { acc, pass });
		}
	}

	class AsynLogin3 extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			custom.dismiss();
			ShowCustomDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			UserApis ua = new UserApis();
			result = ua.login(acc, pass, Variables.gGCM_regId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			// progress_dialog.dismiss();
			HideCustomDiaglog();
			Log.d("hcsong", "onPostExecute:" + result);

			if (!result.equals("fail")) {
				logintext = LOGIN + "id" + "=" + URLEncoder.encode(acc) + "&"
						+ "pass" + "=" + URLEncoder.encode(pass);
				Log.d("hcsong", "logintext:" + logintext);
				mHandler.sendEmptyMessage(LOGIN_SUCCESS);
				savingPreferences();
				Functions.showToastMessage(getString(R.string.login_success),
						MainActivity.this);
				MainActivity.Lgin = true;
				if (DialogSetting.isShowing()) {
					DialogSetting.dismiss();
				}

			} else {
				Functions.showToastMessage(getString(R.string.login_failed),
						MainActivity.this);
			}
		}
	}

	AsynLogin asynLogin;

	private void loginAction() {
		acc = account.getText().toString();
		pass = password.getText().toString();
		boolean isBlank = checkBlankField(acc, pass);

		if (!isBlank) {
			Functions.showToastMessage(getString(R.string.login_blank_field),
					MainActivity.this);
		} else {
			if (asynLogin != null
					&& asynLogin.getStatus() != AsyncTask.Status.FINISHED)
				asynLogin.cancel(true);
			asynLogin = new AsynLogin();
			asynLogin.execute(new String[] { acc, pass });
		}
	}

	AsynLogin2 asynLogin2;

	private void loginAction2() {
		acc = account.getText().toString();
		pass = password.getText().toString();
		boolean isBlank = checkBlankField(acc, pass);

		if (!isBlank) {
			Functions.showToastMessage(getString(R.string.login_blank_field),
					MainActivity.this);
		} else {
			if (asynLogin2 != null
					&& asynLogin2.getStatus() != AsyncTask.Status.FINISHED)
				asynLogin2.cancel(true);
			asynLogin2 = new AsynLogin2();
			asynLogin2.execute(new String[] { acc, pass });
		}
	}

	class AsynLogin2 extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			custom.dismiss();
			ShowCustomDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			UserApis ua = new UserApis();
			result = ua.login(acc, pass, Variables.gGCM_regId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			// progress_dialog.dismiss();
			HideCustomDiaglog();
			Log.d("hcsong", "onPostExecute:" + result);

			if (!result.equals("fail")) {
				logintext = LOGIN + "id" + "=" + URLEncoder.encode(acc) + "&"
						+ "pass" + "=" + URLEncoder.encode(pass);
				Log.d("hcsong", "logintext:" + logintext);
				mHandler.sendEmptyMessage(LOGIN_SUCCESS);
				savingPreferences();
				Functions.showToastMessage(getString(R.string.login_success),
						MainActivity.this);
				MainActivity.Lgin = true;
				if (dialogCon.isShowing()) {
					dialogCon.dismiss();
				}

			} else {
				Functions.showToastMessage(getString(R.string.login_failed),
						MainActivity.this);
			}
		}
	}

	private boolean checkBlankField(String username, String password) {
		if (username.equals("") || password.equals(""))
			return false;

		return true;
	}

	String result = "";
	String logintext = "";
	boolean flag = false;

	class AsynLogin extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			custom.dismiss();
			ShowCustomDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			UserApis ua = new UserApis();
			result = ua.login(acc, pass, Variables.gGCM_regId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			// progress_dialog.dismiss();
			HideCustomDiaglog();
			Log.d("hcsong", "onPostExecute:" + result);

			if (!result.equals("fail")) {
				logintext = LOGIN + "id" + "=" + URLEncoder.encode(acc) + "&"
						+ "pass" + "=" + URLEncoder.encode(pass);
				Log.d("hcsong", "logintext:" + logintext);
				mHandler.sendEmptyMessage(LOGIN_SUCCESS);
				MainActivity.Lgin = true;
				savingPreferences();
				Functions.showToastMessage(getString(R.string.login_success),
						MainActivity.this);

				// if (dialogCon.isShowing()) {
				// dialogCon.dismiss();
				// }

			} else {
				Functions.showToastMessage(getString(R.string.login_failed),
						MainActivity.this);
			}
		}
	}

	@Override
	protected void onDestroy() {

		try {
			unregisterReceiver(pointReceiver);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		super.onDestroy();
	}

	Dialog mcustomdialog;

	// MyProgressBar pBar;

	public void ShowCustomDialog() {
		// try {
		// mcustomdialog = new Dialog(MainActivity.this);
		// mcustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// mcustomdialog.getWindow().setBackgroundDrawable(
		// new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// mcustomdialog.setCanceledOnTouchOutside(true);
		// mcustomdialog.setContentView(R.layout.dialog_item);
		// pBar = (MyProgressBar) mcustomdialog
		// .findViewById(R.id.myProgressBar1);
		// pBar.startAnimation();
		// mcustomdialog.show();
		// } catch (Exception ex) {
		// Log.e("TaiLog", ex.toString());
		// }
		try {

			mcustomdialog = new Dialog(MainActivity.this);
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
		// try {
		// pBar.dismiss();
		// mcustomdialog.dismiss();
		// mcustomdialog = null;
		// } catch (Exception ex) {
		// Log.e("TaiLog", ex.toString());
		// }
		try {
			mcustomdialog.dismiss();
			mcustomdialog = null;

		} catch (Exception ex) {
			Log.e("Hide dialog", ex.toString());
		}
	}

	AsynLogOut asynLogOut;

	private void logoutAction() {

		if (asynLogOut != null
				&& asynLogOut.getStatus() != AsyncTask.Status.RUNNING)
			asynLogOut.cancel(true);
		asynLogOut = new AsynLogOut();
		asynLogOut.execute();
		SharedPreferences pre = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = pre.edit();
		boolean bchk = false;
		if (!bchk) {
			editor.clear();
		} else {
			editor.putBoolean("checked", bchk);
		}
		editor.commit();

	}

	class AsynLogOut extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			// ShowCustomDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			UserApis ua = new UserApis();
			result = ua.logout();
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			// HideCustomDiaglog();
			if (!result.equals("fail")) {
				mHandler.sendEmptyMessage(LOGOUT_SUCCESS);
				Functions.showToastMessage(getString(R.string.logout_success),
						MainActivity.this);
				MainActivity.Lgin = false;
			} else {
				Functions.showToastMessage(getString(R.string.logout_fail),
						MainActivity.this);
			}
		}
	}

	public void savingPreferencesBack() {
		SharedPreferences pre = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = pre.edit();
		boolean bchk = true;
		if (bchk) {
			editor.putString("user", SaveID);
			editor.putString("pwd", SavePass);
			editor.putBoolean("checked2", bchk);
		}
		editor.commit();
	}

	public void savingPreferences() {
		SharedPreferences pre = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = pre.edit();
		String user = account.getText().toString();
		String pwd = password.getText().toString();
		boolean bchk = chexLogin.isChecked();
		boolean bchkSaveID = chexSaveID.isChecked();
		boolean bchkSavePass = chexSavePass.isChecked();
		if (bchk) {
			editor.putString("user", user);
			editor.putString("pwd", pwd);
			editor.putBoolean("checked", bchk);
		}
		if (bchkSaveID) {
			editor.putString("user", user);
			editor.putBoolean("checkedSaveID", bchkSaveID);
		}
		if (bchkSavePass) {
			editor.putString("pwd", pwd);
			editor.putBoolean("checkedSavePass", bchkSavePass);
			editor.putBoolean("checkedSaveID", true);
			editor.putString("user", user);

		}
		editor.commit();
	}

	public void restoringPreferences() {
		SharedPreferences pre = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		boolean bchk = pre.getBoolean("checked", false);
		boolean bchkSaveID = pre.getBoolean("checkedSaveID", false);
		boolean bchkSavePass = pre.getBoolean("checkedSavePass", false);
		if (bchk) {
			String user = pre.getString("user", "");
			String pwd = pre.getString("pwd", "");
			account.setText(user);
			password.setText(pwd);
		}
		if (bchkSaveID) {
			String user = pre.getString("user", "");
			account.setText(user);
		}
		if (bchkSavePass) {
			String pwd = pre.getString("pwd", "");
			password.setText(pwd);
		}
		chexLogin.setChecked(bchk);
		chexSaveID.setChecked(bchkSaveID);
		chexSavePass.setChecked(bchkSavePass);
	}

	public class _GetVersionApp extends AsyncTask<Void, Void, Void> {
		String content = "", versionCH = "";

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Document document = Jsoup.connect(urlCHPLay).get();
				Elements exephang = document
						.select("div[itemprop=softwareVersion]");
				versionCH = exephang.text();
				Elements cont = document.select("div.recent-change");
				for (int i = 0; i < cont.size(); i++) {
					content += "\n" + cont.get(i).text();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			try {
				String versionName = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionName;
				float _cur = Float.parseFloat(versionName);
				try {
					float _new = Float.parseFloat(versionCH);
					if (_cur < _new) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								MainActivity.this);
						builder.setTitle(R.string.new_version);
						String msg = getResources().getString(
								R.string.new_function)
								+ "\n" + content;

						builder.setMessage(msg);
						builder.setPositiveButton(R.string.new_update,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										Intent intent = new Intent(
												Intent.ACTION_VIEW,
												Uri.parse("market://details?id=com.mbabo.android"));
										startActivity(intent);
									}
								});
						builder.setNegativeButton(R.string.new_cancel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										dialog.cancel();
									}
								});
						builder.create().show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (NameNotFoundException e) {

				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
	}

	public class _GetVersionInDialog extends AsyncTask<Void, Void, Void> {
		String content = "", versionCH = "";

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Document document = Jsoup.connect(urlCHPLay).get();
				Elements exephang = document
						.select("div[itemprop=softwareVersion]");
				versionCH = exephang.text();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				String _Newv = getResources().getString(R.string.ver_new)
						+ ": " + versionCH;
				tvVersionNew.setText("" + _Newv);
			} catch (Exception e) {

			}
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (mUploadMessage != null) {
				Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
						: intent.getData();
				if (result != null) {
					String path = MediaUtility.getPath(MainActivity.this,
							result);
					Uri uri = Uri.fromFile(new File(path));
					mUploadMessage.onReceiveValue(uri);
				} else {
					mUploadMessage.onReceiveValue(null);
				}
			}
		}
		if (mFilePathCallback5 != null) {
			Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
					: intent.getData();
			if (result != null) {
				String path = MediaUtility.getPath(MainActivity.this, result);
				Uri uri = Uri.fromFile(new File(path));
				mFilePathCallback5.onReceiveValue(new Uri[] { uri });
			} else {
				mFilePathCallback5.onReceiveValue(null);
			}
		}
		mUploadMessage = null;
		mFilePathCallback5 = null;
	}

}
