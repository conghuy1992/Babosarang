package com.mbabo.android;

import java.io.IOException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mbabo.android.apis.UserApis;
import com.mbabo.android.global.Constants;
import com.mbabo.android.global.Functions;
import com.mbabo.android.global.Variables;
import com.mbabo.android.util.PreferencesHelper;

public class SettingActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	public static int REQUEST_CODE = 9876;
	public static int LOGIN_CODE = 9875;
	public static int LOGOUT_CODE = 9874;
	private PreferencesHelper mHelper;
	private TextView tvVersion, tvVersionNew;
	private ProgressDialog progress_dialog;
	private String progress_dialog_msg = "";
	private final int SHOW_PROG_DIALOG = 0;
	private final int HIDE_PROG_DIALOG = 1;
	private final int LOGIN_SUCCESS = 2;
	private final int LOGOUT_SUCCESS = 3;

	private ToggleButton tgbNotify;
	private ToggleButton chkVibrate, chkSound, chkLed;
	public static Button btnlogin;
	private Button btncscenter;
	private Button btnRegister;
	private TextView tv_forgot;
	private String LOGIN = "http://mbabo.com/login/servLogin.php?";
	private String CSCENTER = "http://www.mbabo.com/cscenter/index.php";
	private String LOGOUT = "http://www.mbabo.com/login/logout_process.php";
	private String URLPrevious = "http://mbabo.com/";
	private String FINDPASS = "http://mbabo.com/member_new/m.find.id.php";
	private String REGISTER = "http://mbabo.com/member_new/join.php";
	private String NON_MEMBER = "http://mbabo.com/login/nomem.php";
	String urlCHPLay = "https://play.google.com/store/apps/details?id=com.mbabo.android";

	Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case LOGOUT_SUCCESS:
				btnlogin.setText(getString(R.string.setting_login));
				break;
			case LOGIN_SUCCESS:
				btnlogin.setText(getApplicationContext().getString(
						R.string.setting_logout));
				break;
			case SHOW_PROG_DIALOG:
				showProgDialog();
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

	private void showProgDialog() {
		progress_dialog = null;
		progress_dialog = new ProgressDialog(SettingActivity.this);
		progress_dialog.setMessage(progress_dialog_msg);
		progress_dialog.setCancelable(false);
		progress_dialog.show();
	}

	private void hideProgDialog() {
		try {
			if (progress_dialog != null && progress_dialog.isShowing())
				progress_dialog.dismiss();
			progress_dialog = null;
		} catch (Exception e) {
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		initView();
		setdata();
		new _GetVersionApp().execute();

	}

	private void initView() {

		tgbNotify = (ToggleButton) findViewById(R.id.setting_tgb_notification);
		chkVibrate = (ToggleButton) findViewById(R.id.setting_chk_vibrate);
		chkSound = (ToggleButton) findViewById(R.id.setting_chk_sound);
		chkLed = (ToggleButton) findViewById(R.id.setting_chk_led);
		tvVersion = (TextView) findViewById(R.id.setting_tv_version);
		tvVersionNew = (TextView) findViewById(R.id.setting_new_version);
		btnlogin = (Button) findViewById(R.id.btn_login);

		if (!MainActivity.Lgin)
			btnlogin.setText(R.string.setting_login);
		else
			btnlogin.setText(R.string.setting_logout);
		btncscenter = (Button) findViewById(R.id.btn_cscenter);
		btnlogin.setOnClickListener(this);
		btncscenter.setOnClickListener(this);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			URLPrevious = (String) b.get("url_previous");
		}
	}

	private void setdata() {
		try {
			// PackageInfo pInfo = getPackageManager().getPackageInfo(
			// getPackageName(), 0);
			// String version = String
			// .format(getString(R.string.setting_version_text),
			// pInfo.versionName);
			String versionName = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			float _cur = Float.parseFloat(versionName);
			String version = getResources().getString(R.string.ver_cur) + ": "
					+ _cur;
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
		tgbNotify.setOnCheckedChangeListener(this);
		chkSound.setOnCheckedChangeListener(this);
		chkLed.setOnCheckedChangeListener(this);
		chkVibrate.setOnCheckedChangeListener(this);

	}

	private boolean checkBlankField(String username, String password) {
		if (username.equals("") || password.equals(""))
			return false;

		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {

		case R.id.setting_chk_vibrate:
			mHelper.setVibriation(isChecked);
			break;

		case R.id.setting_tgb_notification:
			if (isChecked) {
				chkLed.setChecked(true);
				chkSound.setChecked(true);
				chkVibrate.setChecked(true);
			} else {
				chkLed.setChecked(false);
				chkSound.setChecked(false);
				chkVibrate.setChecked(false);
			}
			mHelper.setNotification(isChecked);
			break;

		case R.id.setting_chk_sound:
			mHelper.setSound(isChecked);
			break;

		case R.id.setting_chk_led:
			mHelper.setLed(isChecked);
			break;
		case R.id.checkLogin:
			mHelper.setCheckRemember(isChecked);
			break;

		default:
			break;
		}
	}

	EditText account, password;
	String acc = "";
	String pass = "";
	CheckBox chexLogin;
	Button login;
	Button non_member;
	Dialog custom;

	@Override
	public void onClick(View v) {
		if (!Functions.checkNetwork(getApplicationContext())) {
			finish();
		}
		switch (v.getId()) {
		case R.id.btn_login:

			if (custom == null) {
				if (!MainActivity.Lgin) {
					custom = new Dialog(SettingActivity.this);
					custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
					custom.setContentView(R.layout.login1);
					account = (EditText) custom.findViewById(R.id.btnacc);
					password = (EditText) custom.findViewById(R.id.btpass);
					account.setOnFocusChangeListener(new View.OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							// TODO Auto-generated method stub
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
							// TODO Auto-generated method stub
							if (hasFocus) {
								password.setHint("");
							} else {
								password.setHint(getString(R.string.login_password));
							}
						}
					});
					chexLogin = (CheckBox) custom.findViewById(R.id.checkLogin);
					non_member = (Button) custom.findViewById(R.id.non_member);
					non_member.setOnClickListener(this);
					btnRegister = (Button) custom
							.findViewById(R.id.register_member);
					tv_forgot = (TextView) custom.findViewById(R.id.tv_forgot);
					chexLogin.setChecked(Variables.checkRemember);
					chexLogin.setOnCheckedChangeListener(this);
					tv_forgot.setOnClickListener(this);
					btnRegister.setOnClickListener(this);
					login = (Button) custom.findViewById(R.id.btnLogin);
					custom.setTitle("Custom Dialog");

					restoringPreferences();

					login.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							// TODO Auto-generated method stub
							loginAction();
							custom.dismiss();

						}

					});
					custom.show();
					custom.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							custom = null;
						}
					});
					custom.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							custom = null;
						}
					});
				} else {
					logoutAction();
				}
			}
			break;
		case R.id.tv_forgot:
			sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION).putExtra(
					"login", FINDPASS));
			finish();
			break;
		case R.id.non_member:
			sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION).putExtra(
					"login", NON_MEMBER));
			finish();
			break;
		case R.id.register_member:
			sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION).putExtra(
					"login", REGISTER));
			finish();
			break;
		case R.id.btn_cscenter:
			sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION).putExtra(
					"login", CSCENTER));
			finish();
			break;
		default:
			break;
		}

	}

	Dialog mcustomdialog;
	MyProgressBar pBar;

	public void ShowCustomDialog() {
		// try {
		// mcustomdialog = new Dialog(SettingActivity.this);
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
			mcustomdialog = new Dialog(SettingActivity.this);
			mcustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mcustomdialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			mcustomdialog.setCanceledOnTouchOutside(true);
			mcustomdialog.setContentView(R.layout.dialog_circle);
			mcustomdialog.show();
		} catch (Exception ex) {
			Log.e("TaiLog", ex.toString());
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
			Log.e("TaiLog", ex.toString());
		}
	}

	AsynLogin asynLogin;

	private void loginAction() {
		acc = account.getText().toString();
		pass = password.getText().toString();
		boolean isBlank = checkBlankField(acc, pass);

		if (!isBlank) {
			Functions.showToastMessage(getString(R.string.login_blank_field),
					SettingActivity.this);
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
			ShowCustomDialog();
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
			mHandler.sendEmptyMessage(HIDE_PROG_DIALOG);

			Log.d("hcsong", "login result:" + result.toString());

			if (!result.equals("fail")) {
				mHandler.sendEmptyMessage(LOGIN_SUCCESS);
				MainActivity.Lgin = true;
				String login = LOGIN + "id" + "=" + URLEncoder.encode(acc)
						+ "&" + "pass" + "=" + URLEncoder.encode(pass);

				Log.d("hcsong", login);

				sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION)
						.putExtra("login", login));
				savingPreferences();
				Functions.showToastMessage(getString(R.string.login_success),
						SettingActivity.this);
			} else {
				Functions.showToastMessage(getString(R.string.login_failed),
						SettingActivity.this);
			}
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
			// TODO Auto-generated method stub
			ShowCustomDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			UserApis ua = new UserApis();
			result = ua.logout();
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mHandler.sendEmptyMessage(HIDE_PROG_DIALOG);

			if (!result.equals("fail")) {
				mHandler.sendEmptyMessage(LOGOUT_SUCCESS);
				sendBroadcast(new Intent(MainActivity.ADD_POINT_ACTION)
						.putExtra("login", LOGOUT));
				MainActivity.Lgin = false;
				Functions.showToastMessage(getString(R.string.logout_success),
						SettingActivity.this);
			} else {
				Functions.showToastMessage(getString(R.string.logout_fail),
						SettingActivity.this);
			}
		}
	}

	// @Override
	// protected void onPause() {
	// // TODO Auto-generated method stub
	// super.onPause();
	// savingPreferences();
	// }
	//
	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// restoringPreferences();
	// }

	public void savingPreferences() {
		SharedPreferences pre = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = pre.edit();
		String user = account.getText().toString();
		String pwd = password.getText().toString();
		boolean bchk = chexLogin.isChecked();
		if (!bchk) {
			editor.clear();
		} else {
			editor.putString("user", user);
			editor.putString("pwd", pwd);
			editor.putBoolean("checked", bchk);
		}
		editor.commit();
	}

	public void restoringPreferences() {
		SharedPreferences pre = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		boolean bchk = pre.getBoolean("checked", false);
		if (bchk) {
			String user = pre.getString("user", "");
			String pwd = pre.getString("pwd", "");
			account.setText(user);
			password.setText(pwd);
		}
		chexLogin.setChecked(bchk);
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
				// TODO: handle exception
			}
			super.onPostExecute(result);
		}
	}

}
