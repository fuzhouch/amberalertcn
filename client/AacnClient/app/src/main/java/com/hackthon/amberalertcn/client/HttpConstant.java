package com.hackthon.amberalertcn.client;

/**
 * Created by icyfox on 2015/7/27.
 * Store http constants.
 */
public class HttpConstant {

    public static final String URL_BASE = "http://atn1.dummydigit.net:8080/api/v1/";
    public static final String SENDMESSAGE = URL_BASE + "sendmessage?&user_id=%s&channel_id=%s&amber_alert_id=%d";
    public static final String PUBLISHALERT = URL_BASE + "publishalert?&user_id=%s&channel_id=%s&longitude=%f&latitude=%f";
    public static final String UPDATELOC = URL_BASE + "updatelocation?&user_id=%s&channel_id=%s&longitude=%f&latitude=%f&user_name=%s&user_face=%s";

    public static final String GETALERTS = URL_BASE + "alerts";
    public static final String GETALERTBYID = URL_BASE + "alert?alert_id=";
    public static final String MYFOLLOWALERTS = URL_BASE + "myfollowingalerts";

    public static final String BAIDUINFO = "https://openapi.baidu.com/rest/2.0/passport/users/getLoggedInUser?access_token=";
    public static final String BAIDUFACE = "http://tb.himg.baidu.com/sys/portrait/item/";
}
