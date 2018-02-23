package com.mbabo.android.global;

import android.os.Environment;

public class Constants {
	// Share Preference
	public static final String PREFS_NAME = "mbabo_pref";
	public static final String PREFS_GCM_REGID = "gcm_regid";
	public static final String PREFS_USERNAME = "username";
	public static final String PREFS_PASSWORD = "password";
	public static final String PREFS_SHOW_LOCK = "show_lock";
	public static final String PREFS_NOTI = "notification";
	public static final String PREFS_VIBRIATION = "vibriation";
	public static final String PREFS_NOTI_AD = "notification_ad";
	public static final String PREFS_SOUND = "sound";
	public static final String PREFS_LED = "led";
	public static final String PREFS_REMEMBER = "remember";
	public static final String PREFS_SAVEID = "saveid";
	public static final String PREFS_SAVEPASS = "savepass";

	public static final String PREFS_LOCKSCREEN_DIRECTION = "lockscreen_direction";
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_DOWN = 2;
	public static final int DIRECTION_UP = 3;

	// GCM
	// public static final String GCM_SENDER_ID = "735149622263";
	public static final String GCM_SENDER_ID = "658084756219";

	public static final String GENDER_MALE = "M";
	public static final String GENDER_FEMALE = "F";

	public static final String ADS_ANSWER = "0";
	public static final String ADS_ANSWERED = "1";

	public static final String ADS_LIKE = "0";
	public static final String ADS_LIKED = "1";
	public static final String ADS_LIKE_INVISIBLE = "-11";

	public static final String INFO_SHARE = "0";
	public static final String INFO_SHARED = "1";
	public static final String INFO_SHARE_INVISIBLE = "-11";

	public static final String ADS_INSTALL_INVISIBLE = "-1";
	public static final String ADS_INSTALLED = "1";
	public static final String ADS_INSTALL = "0";

	public static final String PLAYSTORE_APP_URL = "https://play.google.com/store/apps/details?id=%s";
	public static final String PATH_SDCARD = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/AdWebis/";

	public static final String KEY_LINK_NOTI = "link_notification";
}
