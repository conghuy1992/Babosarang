package com.mbabo.android.global;

import java.util.HashMap;

import com.mbabo.android.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;

public class Variables {
	public static String gGCM_regId = "";
	public static boolean checkRemember = true;
	public static boolean checkSaveID = true;
	public static boolean checkSavePass = true;
	public static boolean gNotification = true;
	public static boolean gVibrate = true;
	public static boolean gSound = true;
	public static boolean gLed = true;
	
	public static HashMap<String, Bitmap> gBitmaps = new HashMap<String, Bitmap>();
}
