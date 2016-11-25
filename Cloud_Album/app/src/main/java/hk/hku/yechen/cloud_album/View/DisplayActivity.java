package hk.hku.yechen.cloud_album.View;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.File;

import hk.hku.yechen.cloud_album.R;

/**
 * Created by yechen on 2016/11/24.
 */

public class DisplayActivity extends Activity {
    private VideoView videoView;
    private Button button;
    private MediaController mediaController;

    /*temporary test data*/
    private final String url ="http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private String video_url;
    private Uri uri;
    private RelativeLayout.LayoutParams full_screen_param;
    private RelativeLayout.LayoutParams normal_screen_param;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        video_url = getIntent().getDataString();

        full_screen_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        full_screen_param.addRule(RelativeLayout.CENTER_IN_PARENT);
        normal_screen_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        normal_screen_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mediaController = new MediaController(this);
        videoView = (VideoView) findViewById(R.id.vv_main);
        button = (Button) findViewById(R.id.bt_start);
        uri = Uri.parse(url);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.start();
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            button.setVisibility(View.INVISIBLE);
            videoView.setLayoutParams(full_screen_param);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else if(rotation == Surface.ROTATION_0){
            button.setVisibility(View.VISIBLE);
            videoView.setLayoutParams(normal_screen_param);
        }
        else{}
    }
}
