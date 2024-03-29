package hk.hku.yechen.cloud_album.View;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import hk.hku.yechen.cloud_album.Presenter.VideoManager;
import hk.hku.yechen.cloud_album.R;

/**
 * Created by yechen on 2016/11/26.
 */

public class VideoCaptureActivity extends Activity{
    private static final String TAG = VideoCaptureActivity.class.getSimpleName();
    private String uploadFileName ;
    private String path;

    private Handler handler;
    private File mediaFile;
    private static final int VIDEO_CAPTURE_REQUEST = 1111;
    private static final int VIDEO_CAPTURE_PERMISSION = 2222;
    private static VideoManager videoManager = new VideoManager();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_capture_layout);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case VideoManager.UPLOADED:
                        AlertDialog.Builder builder = new AlertDialog.Builder(VideoCaptureActivity.this);
                        builder.setMessage("Recorded.");
                        builder.setTitle("Video Record");
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

        if (ContextCompat.checkSelfPermission(VideoCaptureActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(VideoCaptureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(VideoCaptureActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(VideoCaptureActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }
        if(permissions.size() > 0) {
            String[] permiss = permissions.toArray(new String[0]);

            ActivityCompat.requestPermissions(VideoCaptureActivity.this, permiss,
                    VIDEO_CAPTURE_PERMISSION);
        }
        AlertDialog.Builder nameBuilder = new AlertDialog.Builder(VideoCaptureActivity.this);
        Date now = new Date();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
        uploadFileName = new String("VID_" + timestamp + ".mp4");
        final EditText editText = (EditText) LayoutInflater.from(this).inflate(R.layout.name_dialog_layout,null);
        editText.setText(uploadFileName);
        nameBuilder.setView(editText);
        nameBuilder.setCancelable(false);
        nameBuilder.setTitle("enter video name");
        nameBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadFileName = editText.getText().toString();
                dialog.dismiss();
                StartVideoCapture();
            }
        }).show();

    }

    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        if(mediaFile.exists() && requestCode == VIDEO_CAPTURE_REQUEST) {
            new Thread(new UploadService()).start();
        }
        else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VIDEO_CAPTURE_PERMISSION) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StartVideoCapture();
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

    private void StartVideoCapture() {
        Uri viduri = getOutputMediaFileUri();

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, viduri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, (long) (4 * 1024 * 1024));
        startActivityForResult(intent, VIDEO_CAPTURE_REQUEST);
    }

    private Uri getOutputMediaFileUri() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        if (isExternalStorageAvailable()) {
            // get the Uri

            //1. Get the external storage directory
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath());

            //2. Create our subdirectory
            if (! mediaStorageDir.exists()) {
                if(! mediaStorageDir.mkdirs()){
                    Log.e(TAG, "Failed to create directory.");
                    return null;
                }
            }
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            if(uploadFileName == null || uploadFileName.equals(".mp4"))
                uploadFileName = new String("VID_" + timestamp + ".mp4");
            path = mediaStorageDir.getPath() + File.separator;
            mediaFile = new File(path + uploadFileName);

            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
            //5. Return the file's URI
            return Uri.fromFile(mediaFile);
        } else {
            return null;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        } else {
            return false;
        }
    }
    private class UploadService implements Runnable{
        @Override
        public void run() {
            videoManager.postVideoToServer(path+uploadFileName);
            handler.sendEmptyMessage(VideoManager.UPLOADED);
        }
    }
}

