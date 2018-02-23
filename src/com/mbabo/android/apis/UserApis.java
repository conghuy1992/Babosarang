package com.mbabo.android.apis;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;

import com.mbabo.android.entity.GcmEntity;
import com.mbabo.android.global.Apis;
import com.mbabo.android.global.Variables;
import com.mbabo.android.util.JSONParser;

public class UserApis {

	public String baboRegister(String uid, String email, String mobile,
			String gcm_regid, String deviceId) {
		String result = "";
		String format = "vn.developer@webis.com";
		try {
			if (email.length() == 0) {
				email = format;
			}
		} catch (Exception e) {
			email = format;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String push = "0";
		if (Variables.gNotification)
			push = "1";
		params.add(new BasicNameValuePair(GcmEntity.PUSH, push));
		params.add(new BasicNameValuePair(GcmEntity.UID, uid));
		params.add(new BasicNameValuePair(GcmEntity.EMAIL, email));
		params.add(new BasicNameValuePair(GcmEntity.MOBILE, mobile));
		params.add(new BasicNameValuePair(GcmEntity.GCM_REGID, gcm_regid));
		params.add(new BasicNameValuePair("deviceid", deviceId));
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		try {
			json = jsonParser.getJSONFromUrl(Apis.BABO_API_REGISTER, params,
					false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String serverMem(String email, String phone, String token,
			String appname) {
		String result = "";
		String google="google";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("phone", phone));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("appname", appname));
		params.add(new BasicNameValuePair("server", google));
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		try {
			json = jsonParser.getJSONFromUrl(Apis.API_SERMEM, params, false);
			if (!json.isNull("root"))
				result = json.getString("root");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String baboRegis(String uid, String gcm_regid, String androidid,
			String model, String version, String count) {
		String result = "";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String push = "0";
		if (Variables.gNotification)
			push = "1";
		params.add(new BasicNameValuePair(GcmEntity.UID, uid));
		params.add(new BasicNameValuePair("reg_gcm", gcm_regid));
		params.add(new BasicNameValuePair("deviceid", androidid));
		params.add(new BasicNameValuePair("phonemodel", model));
		params.add(new BasicNameValuePair("version", version));
		params.add(new BasicNameValuePair("cnt", count));
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		try {
			json = jsonParser.getJSONFromUrl(Apis.BABO_REGISTER, params, false);
			if (!json.isNull("root"))
				result = json.getString("root");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String login(String uid, String password, String gcm_key) {
		String result = "";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", uid));
		params.add(new BasicNameValuePair("pass", password));
		params.add(new BasicNameValuePair("gcm", gcm_key));
		Log.d("babo", URLEncoder.encode(password));
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		try {
			json = jsonParser.getJSONFromUrl(Apis.API_LOGIN, params, false);
			if (!json.isNull("msg"))
				result = json.getString("msg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String logout() {
		String result = "";
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		try {
			json = jsonParser.getJSONFromUrl(Apis.API_LOGOUT);
			if (!json.isNull("msg"))
				result = json.getString("msg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String debug() {
		String result = "";
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		try {
			json = jsonParser.getJSONFromUrl(Apis.API_COOKIE);
			if (!json.isNull("msg"))
				result = json.getString("msg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
