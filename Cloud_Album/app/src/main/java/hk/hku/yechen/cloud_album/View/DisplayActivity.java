package hk.hku.yechen.cloud_album.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.cloud_album.Model.Album;
import hk.hku.yechen.cloud_album.R;

/**
 * Created by yechen on 2016/11/24.
 */

public class DisplayActivity extends Activity {
    private VideoView videoView;
    private MediaController mediaController;
    private RelativeLayout rl_main;
    private ViewPager viewPager;
    private List textViews;
    private List albums;
    private ProgressBar progressBar;
    private int selected_index;
    private DisplayMetrics metrics;

    private String video_url = Album.UPLOAD_ADDRESS;
    private Uri uri;
    private RelativeLayout.LayoutParams full_screen_param;
    private RelativeLayout.LayoutParams normal_screen_param;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        metrics = getResources().getDisplayMetrics();
        rl_main = (RelativeLayout) findViewById(R.id.rl_fmain);
        selected_index = -1;
        video_url += getIntent().getStringExtra("address");
        albums = (List) getIntent().getSerializableExtra("albums");
        init();
        progressBar = (ProgressBar) findViewById(R.id.pb_dlg);
        progressBar.setVisibility(View.VISIBLE);
        viewPager = (ViewPager) findViewById(R.id.vp_albums);
        viewPager.setAdapter(new vpAdapter(textViews));
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPageMargin(2);
        mediaController = new MediaController(this);
        videoView = (VideoView) findViewById(R.id.vv_main);
        full_screen_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        full_screen_param.addRule(RelativeLayout.CENTER_IN_PARENT);
        normal_screen_param = (RelativeLayout.LayoutParams) rl_main.getLayoutParams();
        normal_screen_param.height = metrics.heightPixels/5*3;
        videoView.setMediaController(mediaController);
        uri = Uri.parse(video_url);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.INVISIBLE);
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                rl_main.setLayoutParams(normal_screen_param);
            }
        });

    }

    private void init(){
        textViews = new ArrayList<>();
        Album album;
        for(int i = 0;i < albums.size();i ++){
            album = (Album) albums.get(i);
            TextView textView = (TextView) LayoutInflater.from(DisplayActivity.this).inflate(R.layout.album_image,null,false);
            textView.setText(album.getName());
            textViews.add(textView);
        }
        for(int i = 0;i < albums.size();i ++){
            final int index = i;
            album = (Album) albums.get(i);
            final String url = Album.UPLOAD_ADDRESS + album.getName();
            final TextView textView = (TextView)textViews.get(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(Uri.parse(url));
                    textView.setBackgroundColor(getResources().getColor(R.color.blue));
                    textView.setTextColor(Color.WHITE);
                    if(selected_index != -1) {
                        TextView tmp = (TextView) textViews.get(selected_index);
                        tmp.setTextColor(getResources().getColor(R.color.blue));
                        tmp.setBackgroundColor(Color.WHITE);
                    }
                    selected_index = index;
                }
            });
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            rl_main.setLayoutParams(full_screen_param);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else if(rotation == Surface.ROTATION_0){
            rl_main.setLayoutParams(normal_screen_param);
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
