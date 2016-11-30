package hk.hku.yechen.cloud_album.View;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hk.hku.yechen.cloud_album.Model.Album;
import hk.hku.yechen.cloud_album.Presenter.VideoManager;
import hk.hku.yechen.cloud_album.R;

/**
 * Created by yechen on 2016/11/25.
 */

public class BrowserActivity extends Activity {// Called when the activity is first created.

    private static final String TAG = BrowserActivity.class.getSimpleName();
    public static final int RETURNFROMUPDATE = 0x00000003;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";

    private WebView mWebView;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraVideoPath;
    private Handler handler;

    static final int WEB_VIEW_PERMISSION = 7777;


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a video
                if (mCameraVideoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraVideoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }

        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;

        return;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_activity);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case RETURNFROMUPDATE:
                        AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
                        builder.setMessage("Upadated.");
                        builder.setTitle("Video Updated");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                }
            }
        };

        ArrayList<String> permissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(BrowserActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(BrowserActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }

        if (permissions.size() > 0) {
            String[] permiss = permissions.toArray(new String[0]);

            ActivityCompat.requestPermissions(BrowserActivity.this, permiss,
                    WEB_VIEW_PERMISSION);
        } else {
            StartWebView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WEB_VIEW_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StartWebView();
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

    private void StartWebView() {
        mWebView = (WebView) findViewById(R.id.wv_browser);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        mWebView = new WebView(this);
        setUpWebViewDefaults(mWebView);

        mWebView.setWebChromeClient(new MyWebChromeClient());

        String url = Album.SERVER_ADDRESS;
        openBrowser(url);

        setContentView(mWebView);
    }

    // Open a browser on the URL specified in the text box
    private void openBrowser(String url) {

        if (!url.trim().startsWith("http://")) {
            url = "http://" + url.trim();
        }
        Log.e("url", url);
        mWebView.loadUrl(url.trim());
    }

    private File createVideoFile() throws IOException {
        // Create an video file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(
                videoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );
        return videoFile;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.addJavascriptInterface(new ReturnObject(handler),"ReturnObject");
    }

    private class MyWebChromeClient extends WebChromeClient {

        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            // Create the File where the video should go
            File videoFile = null;
            try {
                videoFile = createVideoFile();
                takeVideoIntent.putExtra("VideoPath", mCameraVideoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Unable to create video File", ex);
            }

            // Continue only if the File was successfully created
            if (videoFile != null) {
                mCameraVideoPath = "file:" + videoFile.getAbsolutePath();
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(videoFile));
            } else {
                takeVideoIntent = null;
            }


            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("video/*");

            Intent[] intentArray;
            if (takeVideoIntent != null) {
                intentArray = new Intent[]{takeVideoIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Video Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;
        }

    }
    private static class ReturnObject {
        private Handler handler;
        ReturnObject(Handler handler) {
            this.handler = handler;
        }
        @JavascriptInterface
        public void ReturnFromUpdate(String str) {
            Log.e("js","js");
            handler.sendEmptyMessage(RETURNFROMUPDATE);
        }
    }
}