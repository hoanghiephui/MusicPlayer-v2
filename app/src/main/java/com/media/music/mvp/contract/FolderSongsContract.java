package com.media.music.mvp.contract;

import com.media.music.mvp.model.Song;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/12/12.
 */

public interface FolderSongsContract {

  interface View extends BaseView {

    void showSongs(List<Song> songList);

  }

  interface Presenter extends BasePresenter<View> {

    void loadSongs(String path);

  }
}
