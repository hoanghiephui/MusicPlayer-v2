package com.media.music.mvp.contract;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.media.music.mvp.model.Song;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/12/3.
 */

public interface AlbumDetailContract {

  interface View extends BaseView {

    Context getContext();

    void showAlbumSongs(List<Song> songList);

    void showAlbumArt(Drawable albumArt);

    void showAlbumArt(Bitmap bitmap);
  }

  interface Presenter extends BasePresenter<View> {

    void subscribe(long albumID);

    void loadAlbumSongs(long albumID);

    void loadAlbumArt(long albumID);
  }
}
