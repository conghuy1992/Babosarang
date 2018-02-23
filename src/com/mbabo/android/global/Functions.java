package com.mbabo.android.global;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.mbabo.android.R;

public class Functions {
	private static Toast mToast;

	public static String formatDouble(double in) {
		return String.format("%.0f", in);
	}

	public static String getUserCodeFromImei(Activity activity) {
		TelephonyManager telephonyManager = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceID = telephonyManager.getDeviceId();
		long h = 0;
		if (h == 0) {
			int off = 0;
			char val[] = deviceID.toCharArray();
			int len = val.length;

			for (int i = 0; i < len; i++) {
				h = 31 * h + val[off++];
			}

			h = Math.abs(h);

			String strH = String.valueOf(h);
			StringBuffer strb = new StringBuffer(strH);
			if (strH.length() < 12) {
				for (int i = 0; i < 12 - strH.length(); i++) {
					strb.append("0");
				}
				strH = strb.toString();
			} else {
				strH = strb.substring(0, 12);
			}

			h = Long.valueOf(strH) * (-1);
		}

		return String.valueOf(h);
	}

	public static int countGridviewContainer(ViewPager v, Context context,
			int horMargin, int verMargin) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();

		float witdh = v.getWidth() / (metrics.densityDpi / 160f);
		float height = v.getHeight() / (metrics.densityDpi / 160f);

		int numWidth = (int) ((witdh - 5) / (50 + horMargin));
		int numHeight = (int) ((height - 5) / (50 + verMargin));

		return numWidth * numHeight;
	}

	public static String hideIdLastChars(String in) {
		if (in.length() > 3) {
			return in.substring(0, in.length() - 3) + "***";
		} else
			return in;
	}

	public static String getPhoneString(String area_code, String part_a,
			String part_b) {
		return area_code + "-" + part_a + "-" + part_b;
	}

	public static int GetDipsFromPixel(float pixels, Context context) {
		// Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}

	public static int getPercentDifference(double a, double b) {
		int c = 100 - (int) ((b / a) * 100);
		return c;
	}

	public static void hideSoftKeyboard(Context con, View view) {
		InputMethodManager imm = (InputMethodManager) con
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void showDialogMessage(String msg, Activity activity) {
		new AlertDialog.Builder(activity).setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).show();
	}

	public static void showToastMessage(String msg, Context context) {
		mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static boolean checkNetwork(Context con) {
		ConnectivityManager conMgr = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr != null) {

			if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnected()
					|| conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
							.isConnected()) {
				return true;
			} else {
				Functions.showToastMessage(
						con.getString(R.string.text_no_internet), con);
				return false;
			}
		}
		return true;
	}

	public static String getCurrentDate() {
		SimpleDateFormat formatter;
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		formatter = new SimpleDateFormat("yyyy/MM/dd");
		return formatter.format(date);
	}

	public static String getCurrentTime() {
		SimpleDateFormat formatter;
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		formatter = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss");
		return formatter.format(date);
	}

	/**
	 * yyyy-MM-dd'T'HH:mm:ss to yyyy/MM/dd
	 * 
	 * @param standart_datetime
	 * @return
	 */
	public static String getShortTime(String time) {
		SimpleDateFormat intput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = intput.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			return time;
		}
		return output.format(d);
	}

	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @param ipv4
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
								// suffix
								return delim < 0 ? sAddr : sAddr.substring(0,
										delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "0.0.0.0";
	}

	// get account from phone
	public static class UserEmailFetcher {

		public static String getEmail(Context context) {
			AccountManager accountManager = AccountManager.get(context);
			Account account = getAccount(accountManager);

			if (account == null) {
				return "";
			} else {
				return account.name;
			}
		}
	}

	private static Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];
		} else {
			account = null;
		}
		return account;
	}

	public static boolean isInstalledApp(String app_name, Activity activity) {
		String uri = app_name.split(".apk")[0];
		PackageManager pm = activity.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}
}
