/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mbabo.android;

import static com.mbabo.android.CommonUtilities.SENDER_ID;
import static com.mbabo.android.CommonUtilities.displayMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.mbabo.android.global.Constants;
import com.mbabo.android.global.Variables;
import com.mbabo.android.model.PushModel;
import com.mbabo.android.util.PreferencesHelper;
import com.mbabo.android.util.UtilPush;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
	private static int FM_NOTIFICATION_ID = 100;
	static Bitmap iconBitmap = null;
	static Bitmap bigImage = null;

	@SuppressWarnings("hiding")
	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	PreferencesHelper mPreferencesHelper;

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		displayMessage(context, getString(R.string.gcm_registered));
		mPreferencesHelper = new PreferencesHelper(context);
		mPreferencesHelper.setRegIdGCM(registrationId);
		Variables.gGCM_regId = registrationId;
		ServerUtilitiesDebug.register(context, registrationId,
				SplashActivity.androidId, SplashActivity.PhoneModel,
				SplashActivity.AndroidVersion);
		ServerUtilitiesSerIntro.register(context, SplashActivity.UID,
				SplashActivity.UserEmail, SplashActivity.PhoneNumber,
				registrationId, SplashActivity.androidId);

	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilitiesDebug.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		
		Log.i(TAG, "Received message");
		String abc = intent.getDataString();
//		String msg = intent.getStringExtra("msg");
		
		mPreferencesHelper = new PreferencesHelper(getApplicationContext());
		mPreferencesHelper.getPreference();
		if (!Variables.gNotification)
			return;
//		if (msg != null) {
//			// generateNotification(context, msg);
//			msg = msg.replaceAll("\\\\", "");
//			Log.v("Test: ", msg.toString());
//			PushModel model = new PushModel();
//			model = UtilPush.getPushModelFromJson(msg);
//			generateNotification(context, model);
//		}

		PushModel model = new PushModel();
		model = UtilPush.getPushModelFromIntent(intent);
		generateNotification(context, model);
		
		// CommonUtilities.displayMessage(context, message);
		// // notifies user
		// generateNotification(context, message);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		// notifies user
		// generateNotification(context, message);
		message = message.replaceAll("\\\\\\", "");
		PushModel model = new PushModel();
		model = UtilPush.getPushModelFromJson(message);
		generateNotification(context, model);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	// private static void generateNotification(Context context, String message)
	// {
	// int icon = R.drawable.ic_launcher;
	// long when = System.currentTimeMillis();
	// NotificationManager notificationManager = (NotificationManager) context
	// .getSystemService(Context.NOTIFICATION_SERVICE);
	// Notification notification = new Notification(icon, message, when);
	//
	// String title = context.getString(R.string.app_name);
	//
	// Intent notificationIntent = new Intent(context, SplashActivity.class);
	// // notificationIntent.putExtra("step", "2");
	// // notificationIntent.putExtra("msg", message);
	// // set intent so it does not start a new activity
	// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
	// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	// PendingIntent intent = PendingIntent.getActivity(context, 0,
	// notificationIntent, 0);
	// notification.setLatestEventInfo(context, title, message, intent);
	// notification.flags |= Notification.FLAG_AUTO_CANCEL;
	//
	// // Play default notification sound
	// if (Variables.gSound) {
	// notification.defaults |= Notification.DEFAULT_SOUND;
	// }
	// // notification.sound = Uri.parse("android.resource://" +
	// // context.getPackageName() + "your_sound_file_name.mp3");
	//
	// // Vibrate if vibrate is enabled
	// if (Variables.gVibrate) {
	// notification.defaults |= Notification.DEFAULT_VIBRATE;
	// }
	// if (Variables.gLed) {
	// notification.defaults |= Notification.DEFAULT_LIGHTS;
	// }
	// notificationManager.notify(0, notification);
	//
	// }

	private static void generateNotification(Context context,PushModel modelPush) {
		NotificationCompat.Builder builderNoti = new NotificationCompat.Builder(context);
		builderNoti.setContentTitle(modelPush.getTitle());
		builderNoti.setContentText(modelPush.getContent());
		builderNoti.setSmallIcon(R.drawable.ic_launcher);
		builderNoti.setTicker(modelPush.getTitle());
		
		try {
			bigImage = BitmapFactory.decodeStream((InputStream) new URL(modelPush.getUrlImg()).getContent());
			//bigImage = Bitmap.createScaledBitmap(bigImage, 250, (bigImage.getHeight()*150)/(bigImage.getWidth()), true);
			
			//bigImage = Bitmap.createScaledBitmap(bigImage, 150, (bigImage.getHeight()*150)/(bigImage.getWidth()), false);			
		} catch (IOException e) {
			Log.v("generateNotification : ", e.toString());
		}
		
		/*
		Matrix matrix = new Matrix(); 
		Bitmap resizeImage = Bitmap.createBitmap(bigImage, 0, 0, bigImage.getWidth(), bigImage.getHeight(), matrix, false);
		
		Resources res = context.getResources(); 
		float scaleWidth = ((float) res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width)) / bigImage.getWidth();
		float scaleHeight = ((float) res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height)) / bigImage.getHeight();
		

		matrix.postScale(scaleWidth, scaleHeight); 
		 */
		
		Intent notiIntent = new Intent(context, MainActivity.class);
		Log.v("QUANG TEST PUSH : ", modelPush.getGoUrl());
		notiIntent.putExtra(Constants.KEY_LINK_NOTI, modelPush.getGoUrl());
		notiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, FM_NOTIFICATION_ID,
				notiIntent,PendingIntent.FLAG_ONE_SHOT + PendingIntent.FLAG_UPDATE_CURRENT);

		builderNoti.setContentIntent(pendingIntent);
		builderNoti.setWhen(System.currentTimeMillis());

		// BigPictureStyle
	    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle(builderNoti);
	    bigPictureStyle.bigPicture(bigImage);
	    bigPictureStyle.setBigContentTitle(modelPush.getTitle());
	    bigPictureStyle.setSummaryText(modelPush.getContent());
		
		Notification notification = bigPictureStyle.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		if (Variables.gSound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (Variables.gVibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		if (Variables.gLed) {
			notification.defaults |= Notification.DEFAULT_LIGHTS;
		}

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(FM_NOTIFICATION_ID, notification);
	}

}
