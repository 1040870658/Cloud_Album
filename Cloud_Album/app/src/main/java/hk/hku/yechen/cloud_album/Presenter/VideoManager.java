package hk.hku.yechen.cloud_album.Presenter;

import android.os.Handler;
import android.util.Log;
import android.view.ViewManager;

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
import hk.hku.yechen.cloud_album.View.VideoCaptureActivity;

/**
 * Created by yechen on 2016/11/24.
 */

public class VideoManager implements Runnable {

    private static final String TAG = ViewManager.class.getSimpleName();
    public static final int COMPELETION = 0x00000001;
    private  List<Album> albums;
    private Handler handler;

    private static String lineEnd = "\r\n";
    private static String twoHyphens = "--";
    private static String boundary = "*****";

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

    public void postVideoToServer(String uploadFileName){
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(uploadFileName);
            if (sourceFile.isFile()) {
                try{
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(uploadUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("file", uploadFileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                            + uploadFileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    if(serverResponseCode == 200){
                        Log.i(TAG,"upload success");
                    }
                    Log.i(TAG,"Server Response is: " + serverResponseMessage + ": " + serverResponseCode);
                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
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