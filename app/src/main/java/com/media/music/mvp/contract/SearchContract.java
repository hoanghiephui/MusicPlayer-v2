package com.media.music.mvp.contract;

import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2017/1/3.
 */

public interface SearchContract {

  interface View extends BaseView {

    void showSearchResult(List<Object> list);

    void showEmptyView();
  }

  interface Presenter extends BasePresenter<View> {

    void search(String queryString);
  }

}
