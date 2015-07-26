package com.hackthon.amberalertcn.client;

/**
 * Created by icyfox on 7/26/2015.
 */
public class LostBean {

    public String amber_alert_id, amber_alert_title, description, from_user_id, longitude, latitude, position;

    public LostBean(){
        amber_alert_title = "孩子丢了啊！";

        from_user_id = "user123";

        position = "天安门";

        description = "莫西干发型.";
    }
}
