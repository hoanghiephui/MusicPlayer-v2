package com.media.music.mvp.presenter;

import com.media.music.mvp.contract.FolderSongsContract;
import com.media.music.mvp.model.Song;
import com.media.music.mvp.usecase.GetFolderSongs;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hoang Hiep on 2016/12/12.
 */

public class FolderSongsPresenter implements FolderSongsContract.Presenter {

  private GetFolderSongs mUsecase;
  private FolderSongsContract.View mView;
  private CompositeSubscription mCompositeSubscription;

  public FolderSongsPresenter(GetFolderSongs getFolderSongs) {
    mUsecase = getFolderSongs;
  }

  @Override
  public void attachView(FolderSongsContract.View view) {
    mView = view;
    mCompositeSubscription = new CompositeSubscription();
  }

  @Override
  public void subscribe() {
  }

  @Override
  public void unsubscribe() {
    mCompositeSubscription.clear();
  }

  @Override
  public void loadSongs(String path) {
    mCompositeSubscription.clear();
    Subscription subscription = mUsecase.execute(new GetFolderSongs.RequestValues(path))
      .getSongList()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Action1<List<Song>>() {
        @Override
        public void call(List<Song> songList) {
          mView.showSongs(songList);
        }
      });
    mCompositeSubscription.add(subscription);
  }

}
