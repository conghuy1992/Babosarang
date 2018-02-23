package com.mbabo.android.entity;

public class GcmNoticeEntity {

	public static final String GCM_REGID = "gcm_regid";
	public static final String MSG = "message";

	private String gcm_regid;
	private String msg;

	public GcmNoticeEntity() {
		gcm_regid = "";
		msg = "";
	}

	public GcmNoticeEntity(String msg, String gcm_regid) {
		this.gcm_regid = gcm_regid;
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMobile(String msg) {
		this.msg = msg;
	}

	public String getGcm_regid() {
		return gcm_regid;
	}

	public void setGcm_regid(String gcm_regid) {
		this.gcm_regid = gcm_regid;
	}

}
