package com.media.music.mvp.contract;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.media.music.mvp.model.Song;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/12/5.
 */

public interface PlaylistDetailContract {

  interface View extends BaseView {

    Context getContext();

    void showPlaylistSongs(List<Song> songList);

    void showPlaylistArt(Drawable playlistArt);

    void showPlaylistArt(Bitmap bitmap);
  }

  interface Presenter extends BasePresenter<View> {

    void loadPlaylistSongs(long playlistID);

    void loadPlaylistArt(long firstAlbumID);
  }
}
