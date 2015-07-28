package com.hackthon.amberalertcn.client;

/**
 * Created by icyfox on 7/26/2015.
 */
public class LostBean {

    public String alert_id, title, description, from_user_id, longitude, latitude, position;
    public int count;

    public LostBean(){
        title = "孩子丢了啊！";

        from_user_id = "user123";

        position = "天安门";

        description = "莫西干发型.";
    }
}
