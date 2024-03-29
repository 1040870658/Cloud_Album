package hk.hku.yechen.cloud_album.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.cloud_album.Model.Album;
import hk.hku.yechen.cloud_album.Presenter.VideoManager;
import hk.hku.yechen.cloud_album.R;

/**
 * Created by yechen on 2016/11/25.
 */
public class AlbumListActivity extends Activity {

    private BitmapDrawable updated_pressed;
    private BitmapDrawable updated;
    private AlbumAdapter albumAdapter;
    private Handler handler;
    private VideoManager videoManager;
    private RecyclerView albumList;
    private List albums;
    private ImageButton ib_upload;
    private ImageButton ib_record;
    private ImageButton ib_update;
    private LinearLayoutManager linearLayoutManager;
    private Thread getListThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updated_pressed = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(),R.drawable.update_pressed));
        updated = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(),R.drawable.update));
        albums = new ArrayList();
        albumAdapter = new AlbumAdapter(AlbumListActivity.this,albums);
        setContentView(R.layout.album_list_layout);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case VideoManager.COMPELETION:
                        albumAdapter.notifyDataSetChanged();
                        ib_update.setImageDrawable(updated);
                }
            }
        };
        videoManager = new VideoManager(albums,handler);
        getListThread = new Thread(videoManager);
        linearLayoutManager = new LinearLayoutManager(this);
        albumList = (RecyclerView) findViewById(R.id.rv_albumlist);
        albumList.setLayoutManager(linearLayoutManager);
        albumList.setAdapter(albumAdapter);

        ib_upload = (ImageButton) findViewById(R.id.ib_upload);
        ib_record = (ImageButton) findViewById(R.id.ib_record);
        ib_update = (ImageButton) findViewById(R.id.ib_update);

        ib_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlbumListActivity.this,BrowserActivity.class);
                startActivity(intent);
            }
        });
        ib_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(AlbumListActivity.this,VideoCaptureActivity.class);
                startActivity(intent);
            }
        });
        ib_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_update.setImageDrawable(updated_pressed);
                getListThread = new Thread(videoManager);
                getListThread.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onRestart();
        getListThread  = new Thread(videoManager);
        getListThread.start();
    }
}
