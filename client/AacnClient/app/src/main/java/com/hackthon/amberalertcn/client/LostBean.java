package com.hackthon.amberalertcn.client;

/**
 * Created by icyfox on 7/26/2015.
 */
public class LostBean {

    public String alert_id, title, description, from_user_id, longitude, latitude, position, uname, uface;
    public int count;
    public long time;

    public LostBean(){
        title = "孩子丢了啊！";

        from_user_id = "user123";

        position = "天安门";

        description = "莫西干发型.";
    }

    @Override
    public String toString() {
        return "LostBean{" +
                "alert_id='" + alert_id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", from_user_id='" + from_user_id + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", position='" + position + '\'' +
                ", uname='" + uname + '\'' +
                ", uface='" + uface + '\'' +
                ", count=" + count +
                '}';
    }
}
