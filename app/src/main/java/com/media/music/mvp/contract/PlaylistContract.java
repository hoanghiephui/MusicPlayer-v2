package com.media.music.mvp.contract;

import com.media.music.mvp.model.Playlist;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/12/4.
 */

public interface PlaylistContract {

  interface View extends BaseView {

    void showPlaylist(List<Playlist> playlists);

    void showEmptyView();

  }

  interface Presenter extends BasePresenter<View> {

    void loadPlaylist();
  }
}
