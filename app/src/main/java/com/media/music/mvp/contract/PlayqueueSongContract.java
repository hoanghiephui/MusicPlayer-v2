package com.media.music.mvp.contract;

import android.content.Context;

import com.media.music.mvp.model.Song;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/12/27.
 */

public interface PlayqueueSongContract {

  interface View extends BaseView {

    Context getContext();

    void showSongs(List<Song> songs);

  }

  interface Presenter extends BasePresenter<View> {

    void loadSongs();
  }
}
