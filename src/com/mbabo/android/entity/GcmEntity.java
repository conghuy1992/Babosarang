package com.mbabo.android.entity;

public class GcmEntity {

    public static final String UID = "uid";
    public static final String PUSH = "push";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String GCM_REGID = "gcm_regid";
    public static final String NO_VERIFY = "no_verify";

    private String uid;
    private String push;
    private String email;
    private String mobile;
    private String gcm_regid;

    public GcmEntity() {
        gcm_regid = "";
        push = "";
        uid = "";
        email = "";
        mobile = "";
    }

    public GcmEntity(String uid, String push, String email, String mobile, String gcm_regid) {
        this.gcm_regid = gcm_regid;
        this.push = push;
        this.uid = uid;
        this.email = email;
        this.mobile = mobile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGcm_regid() {
        return gcm_regid;
    }

    public void setGcm_regid(String gcm_regid) {
        this.gcm_regid = gcm_regid;
	}

}
