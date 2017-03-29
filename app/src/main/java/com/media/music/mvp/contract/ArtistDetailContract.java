package com.media.music.mvp.contract;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

/**
 * Created by Hoang Hiep on 2016/11/24.
 */

public interface ArtistDetailContract {

  interface View extends BaseView {

    Context getContext();

    void showArtistArt(Bitmap bitmap);

    void showArtistArt(Drawable drawable);

  }

  interface Presenter extends BasePresenter<View> {

    void subscribe(long artistID);

    void loadArtistArt(long artistID);
  }

}
