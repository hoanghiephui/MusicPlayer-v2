package com.media.music.mvp.contract;

import com.media.music.mvp.model.Artist;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/11/13.
 */

public interface ArtistContract {

  interface View extends BaseView {

    void showArtists(List<Artist> artists);

    void showEmptyView();
  }

  interface Presenter extends BasePresenter<View> {

    void loadArtists(String action);
  }
}
