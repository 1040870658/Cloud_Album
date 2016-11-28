package hk.hku.yechen.cloud_album.Model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by yechen on 2016/11/25.
 */

public class Album implements Serializable{
    public final static String SERVER_ADDRESS = "http://i.cs.hku.hk/~cfang/app/upload.htm";
    public final static String UPLOAD_ADDRESS = "http://i.cs.hku.hk/~cfang/app/videos/";
    private int id;
    private String name;
    private String title;
    private String address;
    private String timestamp;
    public Album(int id,String address,String name){
        this.id = id;
        this.address = address;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
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
