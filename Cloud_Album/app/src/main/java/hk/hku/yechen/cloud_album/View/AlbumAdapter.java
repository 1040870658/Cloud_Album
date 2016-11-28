package hk.hku.yechen.cloud_album.View;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hk.hku.yechen.cloud_album.Model.Album;
import hk.hku.yechen.cloud_album.R;

/**
 * Created by yechen on 2016/11/25.
 */
public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private LayoutInflater layoutInflater;
    private Context context;
    private List datas;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        return new AlbumViewHolder(layoutInflater.inflate(R.layout.album_item_layout,null,false));
    }
    public AlbumAdapter(Context context,List datas){
        this.context = context;
        this.datas = datas;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AlbumViewHolder albumViewHolder = (AlbumViewHolder)holder;
        Album album = (Album)datas.get(position);
        ((AlbumViewHolder) holder).album = album;
        albumViewHolder.textView.setText(album.getName());
        albumViewHolder.imageTextView.setText(album.getName());
        albumViewHolder.timestampTextview.setText(album.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
    public  class AlbumViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public TextView imageTextView;
        public TextView timestampTextview;
        public Album album;
        public AlbumViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_album_title);
            imageTextView = (TextView) itemView.findViewById(R.id.tv_album_image);
            timestampTextview = (TextView) itemView.findViewById(R.id.tv_album_timestamp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,DisplayActivity.class);
                    intent.putExtra("address",album.getAddress());
                    context.startActivity(intent);
                }
            });
        }
    }
}