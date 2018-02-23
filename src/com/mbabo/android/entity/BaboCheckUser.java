package com.mbabo.android.entity;

public class BaboCheckUser {

	public static final String MSG = "msg";

	private String msg;

	public BaboCheckUser() {
		msg = "";
	}

	public BaboCheckUser(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMobile(String msg) {
		this.msg = msg;
	}

}
