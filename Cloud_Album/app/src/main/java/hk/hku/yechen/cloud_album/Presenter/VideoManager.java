package hk.hku.yechen.cloud_album.Presenter;

import java.util.List;

import hk.hku.yechen.cloud_album.Model.Album;

/**
 * Created by yechen on 2016/11/24.
 */

public class VideoManager implements Runnable{
    List<Album> albums;

    public VideoManager(List albums){
        this.albums = albums;
    }
    public void getDataFromServer(){

    }

    public void postVideoToServer(){

    }

    public List<Album> getAlbums(){
        return albums;
    }

    @Override
    public void run() {
        getDataFromServer();
    }
}
