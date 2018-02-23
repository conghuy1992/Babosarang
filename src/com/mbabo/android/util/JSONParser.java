package com.mbabo.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
	private InputStream is = null;
	private JSONObject jObj = null;
	private JSONArray jArr = null;
	private String json = "";
	private static DefaultHttpClient httpClient = null;
	private static final Object lock = new Object();

	private DefaultHttpClient getClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

	public JSONObject getJSONFromUrl(String url, List<NameValuePair> params,
			boolean isUrl) throws ClientProtocolException, IOException {
		// DefaultHttpClient httpClient = null;
		HttpPost httpPost = null;
		if (isUrl) {
			StringBuffer bUrl = new StringBuffer(url);
			bUrl.append("?");
			for (NameValuePair param : params) {
				if (param != null) {
					bUrl.append(param.getName());
					bUrl.append("=");
					bUrl.append(URLEncoder.encode(param.getValue()));
					bUrl.append("&");
				}
			}

			// Making HTTP request
			// defaultHttpClient
			httpPost = new HttpPost(bUrl.toString());
		} else {
			// Making HTTP request
			// defaultHttpClient
			httpPost = new HttpPost(url);
			httpPost.setEntity((new UrlEncodedFormEntity(params, "UTF-8")));
		}

		synchronized (lock) {
			HttpResponse httpResponse = getClient().execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
			} catch (Exception e) {
			}
		}
		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
		}
		// return JSON String
		return jObj;
	}

	public JSONObject getJSONFromUrl(String url) throws IllegalStateException,
			IOException, JSONException {

		// Making HTTP request
		// defaultHttpClient

		HttpPost httpPost = new HttpPost(url);

		synchronized (lock) {
			HttpResponse httpResponse = getClient().execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();

		}
		// try parse the string to a JSON object
		jObj = new JSONObject(json);

		// return JSON String
		return jObj;

	}

	public JSONArray getJSONArrFromUrl(String url, List<NameValuePair> params)
			throws IllegalStateException, IOException, JSONException {
		StringBuffer bUrl = new StringBuffer(url);
		bUrl.append("?");
		for (NameValuePair param : params) {
			if (param != null) {
				bUrl.append(param.getName());
				bUrl.append("=");
				bUrl.append(URLEncoder.encode(param.getValue()));
				bUrl.append("&");
			}
		}
		// Making HTTP request
		// defaultHttpClient
		HttpPost httpPost = new HttpPost(bUrl.toString());

		synchronized (lock) {
			HttpResponse httpResponse = getClient().execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();

		}
		// try parse the string to a JSON object
		jArr = new JSONArray(json);

		// return JSON String
		return jArr;
	}
}
