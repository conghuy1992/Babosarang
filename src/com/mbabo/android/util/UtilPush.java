package com.mbabo.android.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import com.mbabo.android.model.PushModel;

public class UtilPush {
	private static final String TAG_MSG 			= "msg";
	private static final String TAG_TITLE 			= "title";
	private static final String TAG_CONTENT 		= "content";
	private static final String TAG_IMAGE			= "image";
    private static final String TAG_GO_URL 			= "gourl";
    
    public static PushModel getPushModelFromIntent(Intent intent){ 
    	PushModel modelPush = new PushModel();
    	if (intent!=null) {
    		String title = intent.getStringExtra(TAG_TITLE);
    		String content = intent.getStringExtra(TAG_CONTENT);
    		String image = intent.getStringExtra(TAG_IMAGE);
    		String goUrl = intent.getStringExtra(TAG_GO_URL);
    		
    		modelPush.setContent(content);
    		modelPush.setGoUrl(goUrl);
    		modelPush.setTitle(title);
    		modelPush.setUrlImg(image);
		}
    	
    	return modelPush;
    }
    
    
    public static PushModel getPushModelFromJson(String json){ 
    	PushModel modelPush = new PushModel();
    	try {
    		try {
    			json = URLDecoder.decode(json.toString(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    		
			JSONObject jsonObj = new JSONObject(json);
			JSONObject jsonData = jsonObj.getJSONObject("data");
			
			String msg = jsonData.getString(TAG_MSG);
			String title = jsonData.getString(TAG_TITLE);
			String content = jsonData.getString(TAG_CONTENT);
			String urlImg = jsonData.getString(TAG_IMAGE);
			String goUrl = jsonData.getString(TAG_GO_URL);
			
			modelPush.setUrlImg(urlImg);
			modelPush.setTitle(title);
			modelPush.setContent(content);
			modelPush.setMsg(msg);
			modelPush.setGoUrl(goUrl);
			
		} catch (JSONException e) {
			// TODO: handle exception
			Log.v("PARSER JSON PUSH: ", e.toString());
		}
    	return modelPush;
    }
    
    
    //Get Image from url
//    private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
//        try {
//            InputStream is = (InputStream) this.fetch(url);
//            Drawable d = Drawable.createFromStream(is, "src");
//            return d;
//        } catch (MalformedURLException e) {
//            return null;
//        } catch (IOException e) {
//            return null;
//        }
//    }
//    
//    public Object fetch(String address) throws MalformedURLException,IOException {
//        URL url = new URL(address);
//        Object content = url.getContent();
//        return content;
//    }
    
}
