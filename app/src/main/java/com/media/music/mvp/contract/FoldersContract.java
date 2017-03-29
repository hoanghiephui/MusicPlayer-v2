package com.media.music.mvp.contract;

import android.content.Context;

import com.media.music.mvp.model.FolderInfo;
import com.media.music.mvp.presenter.BasePresenter;
import com.media.music.mvp.view.BaseView;

import java.util.List;

/**
 * Created by Hoang Hiep on 2016/12/11.
 */

public interface FoldersContract {

  interface View extends BaseView {

    Context getContext();

    void showEmptyView();

    void showFolders(List<FolderInfo> folderInfos);
  }

  interface Presenter extends BasePresenter<View> {

    void loadFolders();
  }
}
