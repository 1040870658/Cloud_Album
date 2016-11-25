package hk.hku.yechen.cloud_album.Model;

import android.net.Uri;

/**
 * Created by yechen on 2016/11/25.
 */

public class Album {
    public final static String SERVER_ADDRESS = "";
    private String title;
    private String address;
    private String timestamp;
    public Album(String title,String address,String timestamp){
        this.title = title;
        this.address = address;
        this.timestamp = timestamp;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getTimestamp(){
        return timestamp;
    }
}
