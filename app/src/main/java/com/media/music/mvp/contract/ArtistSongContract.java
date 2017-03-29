package com.media.music.mvp.contract;

import com.media.music.mvp.model.Song;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/11/25.
 */

public interface ArtistSongContract {

  interface View extends BaseView {

    void showSongs(List<Song> songList);
  }

  interface Presenter extends BasePresenter<View> {

    void subscribe(long artistID);

    void loadSongs(long artistID);
  }

}
