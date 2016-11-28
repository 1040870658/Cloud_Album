package hk.hku.yechen.cloud_album.Presenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import hk.hku.yechen.cloud_album.Model.Album;
/**
 * Created by yechen on 2016/11/24.
 */

public class VideoManager implements Runnable {
    List<Album> albums;

    public VideoManager(List albums){
        this.albums = albums;
    }
    public void getDataFromServer() throws Exception{
        String path  = "http://i.cs.hku.hk/~cfang/test/upload.php";
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(5000);// 设置超时时间
        con.setRequestMethod("GET");// 设置请求方式
        if(con.getResponseCode() == 200){
            InputStream inStream = con.getInputStream();
            parseJSON(inStream);
        }
    }

    private void parseJSON (InputStream inStream) throws Exception{
        byte[] data = read(inStream);
        String json = new String(data);
        JSONArray array = new JSONArray(json);
        for (int i = 0;i<array.length();i++){
            JSONObject jsonObject = array.getJSONObject(i);
            Album album = new Album (jsonObject.getInt("id"),jsonObject.getString("name"),jsonObject.getString("timestamp"));
            albums.add(album);
        }
    }

    private byte[] read (InputStream inputStream) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(b))!=-1){
            outputStream.write(b,0,len);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

    public void postVideoToServer(){

    }

    public List<Album> getAlbums(){
        return albums;
    }

    @Override
    public void run() {
        try {
            getDataFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
