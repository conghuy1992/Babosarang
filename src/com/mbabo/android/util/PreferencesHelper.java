package com.mbabo.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import com.mbabo.android.global.Constants;
import com.mbabo.android.global.Variables;

public class PreferencesHelper {

	Context context;
	SharedPreferences myPrefs;
	Editor myEditors;

	private final int MODE_MULTI_PROCESS = 4;
	private final int MODE_PRIVATE = 0;
	
	
	public PreferencesHelper(Context _con) {
		this.context = _con;

		int sdk_version = Build.VERSION.SDK_INT;
		if (sdk_version <= 10) {
			myPrefs = context.getSharedPreferences(Constants.PREFS_NAME,
					MODE_PRIVATE);
		} else {
			myPrefs = context.getSharedPreferences(Constants.PREFS_NAME,
					MODE_MULTI_PROCESS);
		}

		myEditors = myPrefs.edit();
	}

	public void getPreference() {
		Variables.gGCM_regId = myPrefs.getString(Constants.PREFS_GCM_REGID, "");
		Variables.gNotification = myPrefs
				.getBoolean(Constants.PREFS_NOTI, true);
		Variables.gVibrate = myPrefs.getBoolean(Constants.PREFS_VIBRIATION,
				true);
		Variables.gSound = myPrefs.getBoolean(Constants.PREFS_SOUND, true);
		Variables.gLed = myPrefs.getBoolean(Constants.PREFS_LED, true);
		Variables.checkRemember = myPrefs.getBoolean(Constants.PREFS_REMEMBER, true);
		Variables.checkSaveID = myPrefs.getBoolean(Constants.PREFS_SAVEID, true);
		Variables.checkSavePass = myPrefs.getBoolean(Constants.PREFS_SAVEPASS, true);
	}

	public boolean setCheckSavePass(boolean saveid) {
		myEditors.putBoolean(Constants.PREFS_SAVEPASS, saveid);
		Variables.checkSavePass = saveid;
		return myEditors.commit();
	}
	
	public boolean setCheckSaveID(boolean saveid) {
		myEditors.putBoolean(Constants.PREFS_SAVEID, saveid);
		Variables.checkSaveID = saveid;
		return myEditors.commit();
	}
	
	public boolean setCheckRemember(boolean notification) {
		myEditors.putBoolean(Constants.PREFS_REMEMBER, notification);
		Variables.checkRemember = notification;
		return myEditors.commit();
	}

	public boolean setRegIdGCM(String regId) {
		myEditors.putString(Constants.PREFS_GCM_REGID, regId);
		Variables.gGCM_regId = regId;
		return myEditors.commit();
	}

	public boolean setNotification(boolean notification) {
		myEditors.putBoolean(Constants.PREFS_NOTI, notification);
		Variables.gNotification = notification;
		return myEditors.commit();
	}

	public boolean setVibriation(boolean vibriation) {
		myEditors.putBoolean(Constants.PREFS_VIBRIATION, vibriation);
		Variables.gVibrate = vibriation;
		return myEditors.commit();
	}

	public boolean setSound(boolean sound) {
		myEditors.putBoolean(Constants.PREFS_SOUND, sound);
		Variables.gSound = sound;
		return myEditors.commit();
	}

	public boolean setLed(boolean led) {
		myEditors.putBoolean(Constants.PREFS_LED, led);
		Variables.gLed = led;
		return myEditors.commit();
	}

	public boolean getVibriation() {
		return myPrefs.getBoolean(Constants.PREFS_VIBRIATION, false);
	}
	
	
}
