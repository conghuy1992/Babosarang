package com.mbabo.android.model;

public class PushModel {
	private String msg;
	private String title;
	private String content;
	private String urlImg;
	private String goUrl;
	
	public PushModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PushModel(String msg,String urlImg,String goUrl,String title,String content) {
		super();
		this.urlImg = urlImg;
		this.msg = msg;
		this.goUrl = goUrl;
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getGoUrl() {
		return goUrl;
	}

	public void setGoUrl(String goUrl) {
		this.goUrl = goUrl;
	}

	public String getUrlImg() {
		return urlImg;
	}

	public void setUrlImg(String urlImg) {
		this.urlImg = urlImg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
