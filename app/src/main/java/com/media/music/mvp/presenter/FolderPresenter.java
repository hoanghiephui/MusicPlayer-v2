package com.media.music.mvp.presenter;

import com.media.music.mvp.contract.FoldersContract;
import com.media.music.mvp.model.FolderInfo;
import com.media.music.mvp.usecase.GetFolders;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hoang Hiep on 2016/12/11.
 */

public class FolderPresenter implements FoldersContract.Presenter {

  private GetFolders mUsecase;
  private FoldersContract.View mView;
  private CompositeSubscription mCompositeSubscription;

  public FolderPresenter(GetFolders getFolders) {
    this.mUsecase = getFolders;
  }

  @Override
  public void attachView(FoldersContract.View view) {
    this.mView = view;
    mCompositeSubscription = new CompositeSubscription();
  }

  @Override
  public void subscribe() {
    loadFolders();
  }

  @Override
  public void unsubscribe() {
    mCompositeSubscription.clear();
  }

  @Override
  public void loadFolders() {
    mCompositeSubscription.clear();
    Subscription subscription = mUsecase.execute(new GetFolders.RequestValues())
      .getFolderList()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Action1<List<FolderInfo>>() {
        @Override
        public void call(List<FolderInfo> folderInfos) {
          if (folderInfos == null || folderInfos.size() == 0) {
            mView.showEmptyView();
          } else {
            mView.showFolders(folderInfos);
          }
        }
      });
    mCompositeSubscription.add(subscription);
  }
}
