package com.media.music.mvp.contract;

import com.media.music.mvp.model.Album;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/11/12.
 */

public interface AlbumsContract {

  interface View extends BaseView {

    void showAlbums(List<Album> albumList);

    void showEmptyView();
  }

  interface Presenter extends BasePresenter<View> {

    void loadAlbums(String action);

  }
}
