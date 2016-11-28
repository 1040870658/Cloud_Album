package hk.hku.yechen.cloud_album.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private AlbumAdapter albumAdapter;
    private Handler handler;
    private VideoManager videoManager;
    private RecyclerView albumList;
    private List albums;
    private ImageButton ib_upload;
    private ImageButton ib_record;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albums = new ArrayList();
        albumAdapter = new AlbumAdapter(AlbumListActivity.this,albums);
        setContentView(R.layout.album_list_layout);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case VideoManager.COMPELETION:
                        albumAdapter.notifyDataSetChanged();
                }
            }
        };
        videoManager = new VideoManager(albums,handler);
        new Thread(videoManager).start();
        linearLayoutManager = new LinearLayoutManager(this);
        albumList = (RecyclerView) findViewById(R.id.rv_albumlist);
        albumList.setLayoutManager(linearLayoutManager);
        initData();
        albumList.setAdapter(albumAdapter);

        ib_upload = (ImageButton) findViewById(R.id.ib_upload);
        ib_record = (ImageButton) findViewById(R.id.ib_record);

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
    }
    private void initData(){

        /* temporary test data*/
        if(albums.size() == 0)
        for(int i = 0;i != 20;i ++){
            albums.add(i,"album_"+i);
        }
    }
}
