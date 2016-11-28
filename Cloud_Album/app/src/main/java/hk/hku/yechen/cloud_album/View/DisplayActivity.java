package hk.hku.yechen.cloud_album.View;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.cloud_album.R;

/**
 * Created by yechen on 2016/11/24.
 */

public class DisplayActivity extends Activity {
    private VideoView videoView;
    private Button button;
    private MediaController mediaController;
    private ViewPager viewPager;
    private List textViews;

    /*temporary test data*/
    private final String url ="http://i.cs.hku.hk/~cfang/app/videos/20161124_144139.mp4";

    private String video_url;
    private Uri uri;
    private RelativeLayout.LayoutParams full_screen_param;
    private RelativeLayout.LayoutParams normal_screen_param;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.vp_albums);
        init();
        viewPager.setAdapter(new vpAdapter(textViews));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(2);
        video_url = getIntent().getStringExtra("address");

        full_screen_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        full_screen_param.addRule(RelativeLayout.CENTER_IN_PARENT);
        normal_screen_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        normal_screen_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mediaController = new MediaController(this);
        videoView = (VideoView) findViewById(R.id.vv_main);
        button = (Button) findViewById(R.id.bt_start);
        uri = Uri.parse(url);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setLayoutParams(normal_screen_param);
                button.setVisibility(View.VISIBLE);
            }
        });
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

    private void init(){
        textViews = new ArrayList<>();
        TextView textView;
        for(int i = 0;i < 20;i ++){
            textView = (TextView) LayoutInflater.from(DisplayActivity.this).inflate(R.layout.album_image,null,false);
            textView.setText(i+"");
            textViews.add(textView);
        }
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
    private class vpAdapter extends PagerAdapter{
        private List imageViews;

        public vpAdapter(List imageViews){
            this.imageViews = imageViews;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView((TextView)imageViews.get(position));
            return imageViews.get(position);
        }

        @Override
        public float getPageWidth(int position) {
            return 0.5f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((TextView)imageViews.get(position));
        }

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
