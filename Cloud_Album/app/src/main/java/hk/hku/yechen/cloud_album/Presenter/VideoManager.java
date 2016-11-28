package hk.hku.yechen.cloud_album.Presenter;

import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import hk.hku.yechen.cloud_album.Model.Album;
/**
 * Created by yechen on 2016/11/24.
 */

public class VideoManager implements Runnable {

    public static final int COMPELETION = 0x00000001;
    private  List<Album> albums;
    private Handler handler;

    private String end = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "******";

    private static String uploadUrl = "https://i.cs.hku.hk/~cfang/app/upload.php";
    private static String urlPath  = "https://i.cs.hku.hk/~cfang/app/getlist.php";

    public VideoManager(){}

    public VideoManager(List albums,Handler handler){
        this.handler = handler;
        this.albums = albums;
    }

    public void getDataFromServer() throws Exception{

        URL url = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(5000);// 设置超时时间
        con.setRequestMethod("POST");// 设置请求方式
        if(con.getResponseCode() == 200){
            InputStream inStream = con.getInputStream();
            parseJSON(inStream);
        }
    }

    private void parseJSON (InputStream inStream) throws Exception{
        String json = convertStreamToString(inStream);
        Log.d("data:",json);
        JSONArray array = new JSONArray(json);
        for (int i = 0;i<array.length();i++){
            JSONObject jsonObject = array.getJSONObject(i);
            Album album = new Album (jsonObject.getInt("id"),jsonObject.getString("name"),jsonObject.getString("timestamp"));
            Log.d("video","id:"+album.getId()+"name:"+album.getName()+"time:"+album.getTimestamp());
            albums.add(album);
        }
    }

//    private byte[] read (InputStream inputStream) throws Exception{
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] b = new byte[1024];
//        int len = 0;
//        while ((len = inputStream.read(b))!=-1){
//            outputStream.write(b,0,len);
//        }
//        inputStream.close();
//        return outputStream.toByteArray();
//    }

    public void postVideoToServer(File uploadFile, String uploadFileName){
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setChunkedStreamingMode(100 * 1024 * 1024);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + uploadFileName.substring(uploadFileName.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(uploadFile);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Album> getAlbums(){
        return albums;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    public void run() {
        try {
            getDataFromServer();
            handler.sendEmptyMessage(COMPELETION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}