package com.media.music.mvp.presenter;

import com.media.music.mvp.view.BaseView;

/**
 * Created by Hoang Hiep on 2016/11/7.
 */

public interface BasePresenter<T extends BaseView> {

  void attachView(T view);

  void subscribe();

  void unsubscribe();
}
