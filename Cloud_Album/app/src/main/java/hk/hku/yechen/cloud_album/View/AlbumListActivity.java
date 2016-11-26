package hk.hku.yechen.cloud_album.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.cloud_album.Presenter.VideoManager;
import hk.hku.yechen.cloud_album.R;

/**
 * Created by yechen on 2016/11/25.
 */
public class AlbumListActivity extends Activity {

    private VideoManager videoManager;
    private RecyclerView albumList;
    private List albums;
    private ImageButton ib_upload;
    private ImageButton ib_record;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list_layout);
        videoManager = new VideoManager(albums);
        new Thread(videoManager).start();
        linearLayoutManager = new LinearLayoutManager(this);
        albumList = (RecyclerView) findViewById(R.id.rv_albumlist);
        albumList.setLayoutManager(linearLayoutManager);
        initData();
        albumList.setAdapter(new AlbumAdapter(AlbumListActivity.this,albums));

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
                /* invoke core function of the project provided by Dr.Chim */
            }
        });
    }
    private void initData(){

        /* temporary test data*/
        albums = new ArrayList();
        for(int i = 0;i != 20;i ++){
            albums.add(i,"album_"+i);
        }
    }
}
