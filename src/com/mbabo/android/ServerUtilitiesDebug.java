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

import static com.mbabo.android.CommonUtilities.SERVER_URL;
import static com.mbabo.android.CommonUtilities.TAG;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.mbabo.android.R;
import com.mbabo.android.global.Apis;
import com.mbabo.android.util.JSONParser;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilitiesDebug {

	private static final int MAX_ATTEMPTS = 10;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	static boolean register(final Context context, final String regId,
			String androidid, String phonemodel, String androidversion) {
		Log.i(TAG, "registering device (regId = " + regId + ")");
		String serverUrl = SERVER_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		params.put("androidid", androidid);
		params.put("phonemodel", phonemodel);
		params.put("androidversion", androidversion);
		params.put("Version", "2");
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				// displayMessage(context, context.getString(
				// R.string.server_registering, i, MAX_ATTEMPTS));
				post(serverUrl, params);
				try {
					GCMRegistrar.setRegisteredOnServer(context, true);
				} catch (Exception e) {

				}
				// String message =
				// context.getString(R.string.server_registered);
				// CommonUtilities.displayMessage(context, message);
				return true;
			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(TAG, "Failed to register on attempt " + i, e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
		String message = "Error: Cant get GCM key after " + MAX_ATTEMPTS;
		// CommonUtilities.displayMessage(context, message);
		registerGCM(message, androidid, androidversion, phonemodel);
		return false;
	}

	static AsynRegisterFail asynRegisterFail;

	private static void registerGCM(String message, String androidid,
			String androidversion, String phonemodel) {
		if (asynRegisterFail == null
				|| asynRegisterFail.getStatus() != AsyncTask.Status.RUNNING) {
			asynRegisterFail = new AsynRegisterFail();
			asynRegisterFail.execute(new String[] { message, androidid,
					androidversion, phonemodel });
		}
	}

	static class AsynRegisterFail extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("error", params[0]));
			par.add(new BasicNameValuePair("androidid", params[1]));
			par.add(new BasicNameValuePair("androidversion", params[2]));
			par.add(new BasicNameValuePair("phonemodel", params[3]));
			JSONParser jsonParser = new JSONParser();
			JSONObject json;
			try {
				json = jsonParser
						.getJSONFromUrl(Apis.BABO_REGISTER, par, false);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	static void unregister(final Context context, final String regId) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");
		String serverUrl = SERVER_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		try {
			post(serverUrl, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			// String message = context.getString(R.string.server_unregistered);
			// CommonUtilities.displayMessage(context, message);
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			String message = context.getString(
					R.string.server_unregister_error, e.getMessage());
			CommonUtilities.displayMessage(context, message);
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	private static void post(String endpoint, Map<String, String> params)
			throws IOException {
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		Log.v(TAG, "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
}
